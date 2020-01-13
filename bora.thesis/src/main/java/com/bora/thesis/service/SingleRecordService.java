package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.SingleRecord;
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

	public static String removeLastChars(String s) {
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

	public String getTrajectoryForMacRoutes(List<SingleRecord> routes) {
		final List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		final List<SingleRecord> selectedRoutes = new ArrayList<SingleRecord>();
		locations.stream().forEach(l -> {
			routes.stream().filter(x -> x.getZone() == l).findFirst().ifPresent(selectedRoutes::add);
		});
		StringBuilder sb = new StringBuilder();
		String finaltrajectory = null;
		for (SingleRecord record : selectedRoutes) {
			finaltrajectory = sb.append(" --> ").append(record.getZone()).toString();
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

	@Transactional
	public void removeTimestampLocaltime() {
		this.getList().stream().forEach(x -> {
			this.singleRecordRepository.updateTimestamp(SingleRecordService.removeLastChars(x.getTimestamp()), x.getTrackid(), this.getById(x.getTrackid()));
		});
	}
}
