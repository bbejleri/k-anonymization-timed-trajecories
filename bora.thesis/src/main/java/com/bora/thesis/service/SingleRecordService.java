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

import org.apache.commons.lang3.StringUtils;
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

	/**
	 * checks if trajectories fall close temporarily
	 * 
	 * @param t1
	 * @param t2
	 * @return {@link Boolean}
	 */
	public boolean haveSameTemporalClassification(final TrajectoryRecord t1, final TrajectoryRecord t2) {
		boolean check = Boolean.FALSE;
		final String startT1 = t1.getPoints().get(0).getTimestamp().substring(11, 13);
		final String endT1 = t1.getPoints().get(t1.getPoints().size() - 1).getTimestamp().substring(11, 13);
		final String startT2 = t2.getPoints().get(0).getTimestamp().substring(11, 13);
		final String endT2 = t2.getPoints().get(t2.getPoints().size() - 1).getTimestamp().substring(11, 13);
		final HashMap<List<String>, String> map = this.getTemporalClassification();
		String st1 = null;
		String st2 = null;
		String et1 = null;
		String et2 = null;
		for (Map.Entry<List<String>, String> entry : map.entrySet()) {
			if (entry.getKey().contains(startT1)) {
				st1 = entry.getValue();
			}
			if (entry.getKey().contains(endT1)) {
				et1 = entry.getValue();
			}
			if (entry.getKey().contains(startT2)) {
				st2 = entry.getValue();
			}
			if (entry.getKey().contains(endT2)) {
				et2 = entry.getValue();
			}
		}
		if (!StringUtils.isEmpty(st1) && !StringUtils.isEmpty(st2) && !StringUtils.isEmpty(et1) && !StringUtils.isEmpty(et2)) {
			final String periodT1 = st1 + et1;
			final String periodT2 = st2 + et2;
			if (periodT1.equalsIgnoreCase(periodT2)) {
				check = Boolean.TRUE;
			}
		}
		return check;
	}

	/**
	 * collect noises of the dataset for all trajectories
	 * 
	 * @param route
	 * @return {@link List<SingleRecord>}
	 */
	public List<SingleRecord> collectNoise(List<SingleRecord> route) {
		TrajectoryRecord formedTrajectory = this.formTrajectoryByPointLocations(route);
		List<SingleRecord> selected = formedTrajectory.getPoints();
		List<SingleRecord> noise = route.stream().filter(i -> !selected.contains(i)).collect(Collectors.toList());
		return noise;
	}

	/**
	 * @param keyExtractor
	 * @return list of distinct {@link T} based on T attributes
	 */
	public <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	/**
	 * Delete {@link SingleRecord}
	 */
	@Transactional
	public void delete(final SingleRecord record) {
		this.singleRecordRepository.delete(record);
	}

	/**
	 * Forms {@link TrajectoryRecord} out of {@link SingleRecord} without noise
	 */
	public TrajectoryRecord formTrajectoryByPointLocations(List<SingleRecord> route) {
		TrajectoryRecord trajectory = new TrajectoryRecord();
		List<SingleRecord> selected = route.stream().filter(this.distinctByKey(pr -> Arrays.asList(pr.getZone()))).collect(Collectors.toList());
		trajectory.setPoints(selected);
		trajectory.setVendor(route.get(0).getTruncmac());
		return trajectory;
	}

	public List<SingleRecord> getAllEntered() {
		return this.getList().stream().filter(x -> x.getEventtype() == 0).collect(Collectors.toList());
	}

	public List<SingleRecord> getAllLeft() {
		return this.getList().stream().filter(x -> x.getEventtype() == 1).collect(Collectors.toList());
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

	public List<String> getDistinctMacAdresses() {
		List<String> maclist = new ArrayList<String>();
		for (SingleRecord record : this.getList()) {
			if (!maclist.contains(record.getHashMac())) {
				maclist.add(record.getHashMac());
			}
		}
		return maclist;
	}

	public List<String> getDistinctZones() {
		List<String> zones = new ArrayList<String>();
		zones = this.getList().stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		return zones;
	}

	public String getGeneralTimestamp(final TrajectoryRecord trajectory) {
		final HashMap<List<String>, String> map = this.getTemporalClassification();
		final String start = trajectory.getPoints().get(0).getTimestamp().substring(11, 13);
		final String end = trajectory.getPoints().get(trajectory.getPoints().size() - 1).getTimestamp().substring(11, 13);
		String s = null;
		String e = null;
		for (Map.Entry<List<String>, String> entry : map.entrySet()) {
			if (entry.getKey().contains(start)) {
				s = entry.getValue();
			}
			if (entry.getKey().contains(end)) {
				e = entry.getValue();
			}
		}
		return s + "-" + e;
	}

	public List<SingleRecord> getList() {
		return singleRecordRepository.getAll();
	}

	/**
	 * translates a list of trajectories to visualised trajectories
	 * 
	 * @param trajectories
	 * @return {@link List<VisualTrajectoryRecord>}
	 */
	public List<VisualTrajectoryRecord> getListVisualTrajectoryRecord(List<TrajectoryRecord> trajectories) {
		List<VisualTrajectoryRecord> list = trajectories.stream().map(t -> this.translateToVisualisedTrajectory(t)).collect(Collectors.toList());
		return list;
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

	public List<String> getTimestampsForZone(final String zone) {
		List<SingleRecord> records = this.singleRecordRepository.getByZone(zone);
		List<String> timestamps = records.stream().map(t -> t.getTimestamp()).collect(Collectors.toList());
		return timestamps;
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

	public HashMap<List<String>, String> getTemporalClassification() {
		final HashMap<List<String>, String> temporalClassification = new HashMap<List<String>, String>();
		temporalClassification.put(Arrays.asList("07", "08", "09", "10", "11", "12"), "morning");
		temporalClassification.put(Arrays.asList("13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"), "afternoon");
		temporalClassification.put(Arrays.asList("00", "01", "02", "03", "04", "05", "06"), "overnight");
		return temporalClassification;
	}

	public boolean isMultipleLocationTrajectory(final String initializedTrajectory) {
		List<String> multipleLocationTrajectories = new ArrayList<String>();
		boolean isMultipleLocation = Boolean.FALSE;
		List<Character> chars = initializedTrajectory.chars().mapToObj(y -> (char) y).collect(Collectors.toList());
		if (chars.size() > 1) {
			multipleLocationTrajectories.add(chars.toString());
			isMultipleLocation = Boolean.TRUE;
		}
		return isMultipleLocation;
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

	/**
	 * removes points which differentiate trajectory from its best neighbour
	 * 
	 * @param centroid
	 * @param trajectory
	 * @return {@link TrajectoryRecord}
	 */
	public TrajectoryRecord removeObsoletePoint(final String centroid, final TrajectoryRecord trajectory) {
		final String initialTrajectory = this.translateToVisualisedTrajectory(trajectory).getInicalTrajectory();
		final List<Character> chars1 = centroid.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		final List<Character> chars2 = initialTrajectory.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		final HashMap<String, String> map = this.getSymbolicZones();
		for (Character ch : chars2) {
			if (!chars1.contains(ch)) {
				String pointstring = ch.toString();
				List<SingleRecord> points = trajectory.getPoints();
				points.removeIf(t -> t.getZone().equalsIgnoreCase(map.get(pointstring)));
				break;
			}
		}
		return trajectory;
	}

	/**
	 * removes single location trajectories from the dataset
	 */
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

	/**
	 * translates {@link TrajectoryRecord} into {@link VisualTrajectoryRecord}
	 * 
	 * @param trajectory
	 * @return {@link VisualTrajectoryRecord}
	 */
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
			named = sb1.append(" -> ").append(zoneNames.get(point.getZone())).append(" (").append(point.getTimestamp().substring(11, 23)).append(") ").toString();
			initialized = sb2.append(zoneInicials.get(point.getZone())).toString();
		}
		visualTrajectoryRecord.setVizualizedTrajectory(visualised);
		visualTrajectoryRecord.setNamedTrajectory(named);
		visualTrajectoryRecord.setInicalTrajectory(initialized);
		visualTrajectoryRecord.setVendor(trajectory.getVendor());
		return visualTrajectoryRecord;
	}
}
