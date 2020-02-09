package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.dataaccess.VisualTrajectoryRecord;
import com.bora.thesis.repositories.SingleRecordRepository;

/**
 * @author: bora
 */
@Service
public class SingleRecordService {

	@Autowired
	private SingleRecordRepository singleRecordRepository;

	@Autowired
	private HelperService helperService;

	/**
	 * Delete {@link SingleRecord}
	 */
	@Transactional
	public void delete(final SingleRecord record) {
		this.singleRecordRepository.delete(record);
	}

	/**
	 * @param keyExtractor
	 * @return list of distinct {@link T} based on T attributes
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public List<SingleRecord> getList() {
		return singleRecordRepository.getAll();
	}

	public List<String> getDistinctMacAdresses() {
		List<String> maclist = new ArrayList<String>();
		for (SingleRecord record : this.getList()) {
			if (!maclist.contains(record.getHashMac())) {
				maclist.add(record.getHashMac());
			}
		}
		return maclist;
	}

	public List<SingleRecord> getByMacAddress(final String hashmac) {
		return this.singleRecordRepository.getByMacAddress(hashmac);
	}

	public SingleRecord getById(final Long id) {
		return this.singleRecordRepository.getById(id);
	}

	public List<SingleRecord> getByZone(final String zone) {
		return this.singleRecordRepository.getByZone(zone);
	}

	public SingleRecord getByZoneAndTimestamp(final String zone, final String timestamp) {
		return this.singleRecordRepository.getByZoneAndTimestamp(zone, timestamp);
	}

	public List<SingleRecord> getAllEntered() {
		return this.getList().stream().filter(x -> x.getEventtype() == 0).collect(Collectors.toList());
	}

	public List<SingleRecord> getAllLeft() {
		return this.getList().stream().filter(x -> x.getEventtype() == 1).collect(Collectors.toList());
	}

	public List<String> getDistinctZones() {
		List<String> zones = new ArrayList<String>();
		zones = this.getList().stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		return zones;
	}

	public List<TrajectoryRecord> generateAllTrajectories() {
		List<String> allDistinctMacs = this.getDistinctMacAdresses();
		List<TrajectoryRecord> alltrajectories = new ArrayList<TrajectoryRecord>();
		allDistinctMacs.stream().forEach(x -> {
			List<SingleRecord> routes = this.getByMacAddress(x);
			alltrajectories.add(this.formTrajectoryByPointLocations(routes));
		});
		return alltrajectories;
	}

	public boolean isMultipleLocationTrajectory(String initializedTrajectory) {
		List<String> multipleLocationTrajectories = new ArrayList<String>();
		boolean isMultipleLocation = Boolean.FALSE;
		List<Character> chars = initializedTrajectory.chars().mapToObj(y -> (char) y).collect(Collectors.toList());
		if (chars.size() > 1) {
			multipleLocationTrajectories.add(chars.toString());
			isMultipleLocation = Boolean.TRUE;
		}
		return isMultipleLocation;
	}

	public List<String> getTimestampsForZone(final String zone) {
		List<SingleRecord> records = this.singleRecordRepository.getByZone(zone);
		List<String> timestamps = records.stream().map(t -> t.getTimestamp()).collect(Collectors.toList());
		return timestamps;
	}

	/**
	 * Forms {@link TrajectoryRecord} out of {@link SingleRecord} without noise
	 */
	public TrajectoryRecord formTrajectoryByPointLocations(List<SingleRecord> route) {
		TrajectoryRecord trajectory = new TrajectoryRecord();
		List<SingleRecord> selected = route.stream().filter(distinctByKey(pr -> Arrays.asList(pr.getZone()))).collect(Collectors.toList());
		trajectory.setPoints(selected);
		return trajectory;
	}

	public List<SingleRecord> collectNoise(List<SingleRecord> route) {
		TrajectoryRecord formedTrajectory = this.formTrajectoryByPointLocations(route);
		List<SingleRecord> selected = formedTrajectory.getPoints();
		List<SingleRecord> noise = route.stream().filter(i -> !selected.contains(i)).collect(Collectors.toList());
		return noise;
	}

	@Transactional
	public void removeSinglesFromDataset() {
		List<String> distinctMacAddresses = this.getDistinctMacAdresses();
		List<SingleRecord> route = new ArrayList<SingleRecord>();
		List<List<SingleRecord>> allRoutes = new ArrayList<List<SingleRecord>>();
		for (String mac : distinctMacAddresses) {
			route = this.getByMacAddress(mac);
			allRoutes.add(route);
		}
		for (List<SingleRecord> r : allRoutes) {
			if (r.size() == 1) {
				this.delete(r.get(0));
			}
		}
	}

	@Transactional
	public void removeNoiseFromDataset() {
		List<String> distinctMacAddresses = this.getDistinctMacAdresses();
		List<SingleRecord> route = new ArrayList<SingleRecord>();
		List<List<SingleRecord>> allRoutes = new ArrayList<List<SingleRecord>>();
		List<SingleRecord> noise = new ArrayList<SingleRecord>();
		List<List<SingleRecord>> allNoise = new ArrayList<List<SingleRecord>>();
		for (String mac : distinctMacAddresses) {
			route = this.getByMacAddress(mac);
			allRoutes.add(route);
		}
		for (List<SingleRecord> r : allRoutes) {
			noise = this.collectNoise(r);
			allNoise.add(noise);
		}
		for (List<SingleRecord> n : allNoise) {
			for (SingleRecord foo : n) {
				this.delete(foo);
			}
		}
	}

	public VisualTrajectoryRecord translateToVisualisedTrajectory(final TrajectoryRecord trajectory) {
		final HashMap<String, String> zoneInicials = this.getZoneInicials();
		final HashMap<String, String> zoneNames = this.getZoneNames();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		String visualised = null;
		String named = null;
		String initialized = null;
		VisualTrajectoryRecord visualTrajectoryRecord = new VisualTrajectoryRecord();
		for (SingleRecord point : trajectory.getPoints()) {
			visualised = sb.append(" -> ").append(point.getZone()).toString();
			named = sb1.append(" -> ").append(zoneNames.get(point.getZone())).append(" (").append(point.getTimestamp().substring(11, point.getTimestamp().length() - 7)).append(") ")
					.toString();
			initialized = sb2.append(zoneInicials.get(point.getZone())).toString();
		}
		visualTrajectoryRecord.setVizualizedTrajectory(visualised);
		visualTrajectoryRecord.setNamedTrajectory(named);
		visualTrajectoryRecord.setInicalTrajectory(initialized);
		return visualTrajectoryRecord;
	}

	public TrajectoryRecord translateToTrajectoryRecord(final VisualTrajectoryRecord visualTrajectoryRecord) {
		final TrajectoryRecord finalTrajectory = new TrajectoryRecord();
		final List<Character> chars = visualTrajectoryRecord.getInicalTrajectory().chars().mapToObj(ch -> (char) ch).collect(Collectors.toList());
		final List<String> zones = new ArrayList<String>();
		final List<SingleRecord> singleRecords = new ArrayList<SingleRecord>();
		for (Character ch : chars) {
			zones.add(this.getSymbolicZones().get(ch));
		}
		for (String zone : zones) {
			singleRecords.add(this.getBySingleZone(zone));
		}
		finalTrajectory.setPoints(singleRecords);
		return finalTrajectory;
	}

	private SingleRecord getBySingleZone(String zone) {
		return this.singleRecordRepository.getBySingleZone(zone);
	}

	public HashMap<String, String> getZoneInicials() {
		final HashMap<String, String> zoneInicials = new HashMap<String, String>();
		zoneInicials.put("bz1060", "A");
		zoneInicials.put("bz1069", "B");
		zoneInicials.put("bz1078", "C");
		zoneInicials.put("bz1082", "D");
		zoneInicials.put("bz1083", "E");
		zoneInicials.put("bz1084", "F");
		return zoneInicials;
	}

	public HashMap<String, String> getSymbolicZones() {
		final HashMap<String, String> zoneInicials = new HashMap<String, String>();
		zoneInicials.put("A", "bz1060");
		zoneInicials.put("B", "bz1069");
		zoneInicials.put("C", "bz1078");
		zoneInicials.put("D", "bz1082");
		zoneInicials.put("E", "bz1083");
		zoneInicials.put("F", "bz1084");
		return zoneInicials;
	}

	public HashMap<String, String> getZoneNames() {
		final HashMap<String, String> zoneNames = new HashMap<String, String>();
		zoneNames.put("bz1060", "Obere Brücke");
		zoneNames.put("bz1069", "Gabelmann");
		zoneNames.put("bz1078", "Gruenermarkt");
		zoneNames.put("bz1082", "Obstmarkt");
		zoneNames.put("bz1083", "Infostand");
		zoneNames.put("bz1084", "Maxplatz");
		return zoneNames;
	}

	@Transactional
	public void removeTimestampLocaltime() {
		this.getList().stream().forEach(x -> {
			this.singleRecordRepository.updateTimestamp(this.helperService.removeLastChars(x.getTimestamp()), x.getTrackid(), this.getById(x.getTrackid()));
		});
	}
}
