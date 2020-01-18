package com.bora.thesis.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.dataaccess.VisualTrajectoryRecord;

/**
 * @author: bora
 */
@Service
public class ClusterRecordService {

	@Autowired
	private SingleRecordService singleRecordService;

	@Autowired
	private ParentService parentService;

	private TrajectoryRecord getRandomTrajectory(List<TrajectoryRecord> trajectories) {
		return trajectories.get(new Random().nextInt(trajectories.size()));
	}

	private int calculateSimilarity(final String left, final String right) {
		return this.parentService.apply(left, right);
	}

	private int maxDistance(List<Integer> distances) {
		return Collections.min(distances);
	}

	/**
	 * @param alltrajectories
	 * @return furthest {@link TrajectoryRecord} from a random trajectory of all trajectories
	 */
	public TrajectoryRecord getFurthiestRecord(final List<TrajectoryRecord> alltrajectories) {
		final TrajectoryRecord entryPointTrajecotry = this.getRandomTrajectory(alltrajectories);
		TrajectoryRecord furthiestRecord = null;
		final VisualTrajectoryRecord visualEntryPoint = this.singleRecordService.translateToVisualisedTrajectory(entryPointTrajecotry);
		final List<VisualTrajectoryRecord> visualTrajectories = alltrajectories.stream().map(x -> this.singleRecordService.translateToVisualisedTrajectory(x))
				.collect(Collectors.toList());
		final List<String> initialTrajectories = visualTrajectories.stream().map(y -> y.getInicalTrajectory()).collect(Collectors.toList());
		final List<Integer> allDistances = initialTrajectories.stream().map(k -> this.calculateSimilarity(k, visualEntryPoint.getInicalTrajectory())).collect(Collectors.toList());
		for (TrajectoryRecord record : alltrajectories) {
			final VisualTrajectoryRecord visualRecord = this.singleRecordService.translateToVisualisedTrajectory(record);
			if (this.calculateSimilarity(visualEntryPoint.getInicalTrajectory(), visualRecord.getInicalTrajectory()) == this.maxDistance(allDistances)) {
				furthiestRecord = record;
				break;
			}
		}
		return furthiestRecord;
	}
}
