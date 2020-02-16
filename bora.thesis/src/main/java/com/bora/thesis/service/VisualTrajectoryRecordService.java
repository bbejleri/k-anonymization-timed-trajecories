package com.bora.thesis.service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.VisualTrajectoryRecord;

/**
 * @author: bora
 */
@Service
public class VisualTrajectoryRecordService {

	public HashMap<String, Integer> countDistinctInitialTrajectories(final List<VisualTrajectoryRecord> visuals) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (VisualTrajectoryRecord record : visuals) {
			if (map.containsKey(record.getInicalTrajectory())) {
				map.put(record.getInicalTrajectory(), map.get(record.getInicalTrajectory()) + 1);
			} else {
				map.put(record.getInicalTrajectory(), 1);
			}
		}
		return map;
	}

	public List<String> getDistinctInitialTrajectories(final List<VisualTrajectoryRecord> visuals) {
		List<String> list = visuals.stream().map(x -> x.getInicalTrajectory()).distinct().collect(Collectors.toList());
		return list;
	}
}
