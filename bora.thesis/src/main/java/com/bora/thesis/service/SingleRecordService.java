package com.bora.thesis.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

	public List<SingleRecord> getAllEntered() {
		return this.getList().stream().filter(x -> x.getEventtype() == 0).collect(Collectors.toList());
	}

	public List<SingleRecord> getAllLeft() {
		return this.getList().stream().filter(x -> x.getEventtype() == 1).collect(Collectors.toList());
	}

	public String removeLastChars(final String s) {
		return Optional.ofNullable(s).filter(str -> str.length() != 0).map(str -> str.substring(0, str.length() - 3)).orElse(s);
	}

	public boolean checkDateRange(final Date min, final Date max, final Date current) {
		boolean check = Boolean.FALSE;
		if (current.after(min) && current.before(max)) {
			check = Boolean.TRUE;
		}
		return check;
	}

	public List<String> getDistinctZones() {
		List<String> zones = new ArrayList<String>();
		zones = this.getList().stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		return zones;
	}

	public List<String> getMultipleLocationTrajectories(final List<String> trajectoriesWithInicials) {
		List<String> multipleLocationTrajectories = new ArrayList<String>();
		trajectoriesWithInicials.stream().forEach(k -> {
			List<Character> chars = k.chars().mapToObj(y -> (char) y).collect(Collectors.toList());
			if (chars.size() > 1) {
				multipleLocationTrajectories.add(chars.toString());
			}
		});
		return multipleLocationTrajectories;
	}

	public String getMinimalTimestamp(final List<String> timestamps) throws ParseException {
		List<Date> dates = new ArrayList<Date>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		for (String timestamp : timestamps) {
			timestamp = this.removeLastChars(timestamp);
			Date toDate = formatter.parse(timestamp);
			dates.add(toDate);
		}
		Date minDate = Collections.min(dates);
		String searchTimestamp = formatter.format(minDate) + "+02";
		return searchTimestamp;
	}

	public List<String> getTimestampsForZone(final String zone) {
		List<SingleRecord> records = this.singleRecordRepository.getByZone(zone);
		List<String> timestamps = records.stream().map(t -> t.getTimestamp()).collect(Collectors.toList());
		return timestamps;
	}

	/**
	 * Forms {@link TrajectoryRecord} out of {@link SingleRecord} without noise
	 */
	public TrajectoryRecord formTrajectoryByPointLocations(List<SingleRecord> routes) {
		final List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		TrajectoryRecord trajecotry = new TrajectoryRecord();
		List<SingleRecord> points = new ArrayList<SingleRecord>();
		locations.stream().forEach(k -> {
			SingleRecord point = new SingleRecord();
			final List<String> timestamps = this.getTimestampsForZone(k);
			try {
				point = this.singleRecordRepository.getByZoneAndTimestamp(k, this.getMinimalTimestamp(timestamps));
			} catch (ParseException e) {
				e.printStackTrace();
				// TODO: Handle it
			}
			points.add(point);
		});
		trajecotry.setPoints(points);
		return trajecotry;
	}

	public String getTrajectoryForMacRoutes(List<SingleRecord> routes) {
		final List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		final List<SingleRecord> selectedRoutes = new ArrayList<SingleRecord>();
		locations.stream().forEach(l -> {
			routes.stream().filter(x -> x.getZone() == l).findFirst().ifPresent(selectedRoutes::add);
		});
		StringBuilder sb = new StringBuilder();
		String finaltrajectory = null;
		for (SingleRecord record : selectedRoutes) {
			finaltrajectory = sb.append(" -> ").append(record.getZone()).toString();
		}
		return finaltrajectory;
	}

	public String getTrajectoriesWithInicials(List<SingleRecord> routes) {
		final List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		final List<SingleRecord> selectedRoutes = new ArrayList<SingleRecord>();
		final HashMap<String, String> zoneInicials = this.getZoneInicials();
		locations.stream().forEach(l -> {
			routes.stream().filter(x -> x.getZone() == l).findFirst().ifPresent(selectedRoutes::add);
		});
		StringBuilder sb = new StringBuilder();
		String finaltrajectory = null;
		for (SingleRecord record : selectedRoutes) {
			finaltrajectory = sb.append(zoneInicials.get(record.getZone())).toString();
		}
		return finaltrajectory;
	}

	public String getTrajectoriesWithNames(final List<SingleRecord> routes) {
		final List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		final List<SingleRecord> selectedRoutes = new ArrayList<SingleRecord>();
		final HashMap<String, String> zoneNames = this.getZoneNames();
		locations.stream().forEach(l -> {
			routes.stream().filter(x -> x.getZone() == l).findFirst().ifPresent(selectedRoutes::add);
		});
		StringBuilder sb = new StringBuilder();
		String finaltrajectory = null;
		for (SingleRecord record : selectedRoutes) {
			finaltrajectory = sb.append(" -> ").append(zoneNames.get(record.getZone())).append(" (").append(record.getTimestamp().substring(11, 19)).append(") ").toString();
		}
		return finaltrajectory;
	}

	public List<VisualTrajectoryRecord> fillTrajectoryRecords(final List<String> rawNames, final List<String> trajectoryInitials, List<String> trajectoryNames) {
		final List<VisualTrajectoryRecord> records = new ArrayList<VisualTrajectoryRecord>();
		for (int i = 0; i < rawNames.size(); i++) {
			VisualTrajectoryRecord record = new VisualTrajectoryRecord();
			record.setVizualizedTrajectory(rawNames.get(i));
			record.setInicalTrajectory(trajectoryInitials.get(i));
			record.setNamedTrajectory(trajectoryNames.get(i));
			records.add(record);
		}
		return records;
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
			this.singleRecordRepository.updateTimestamp(this.removeLastChars(x.getTimestamp()), x.getTrackid(), this.getById(x.getTrackid()));
		});
	}
}
