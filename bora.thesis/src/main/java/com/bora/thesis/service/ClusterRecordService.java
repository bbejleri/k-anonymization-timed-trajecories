package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.ClusterRecord;
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

	public TrajectoryRecord getRandomTrajectory(List<TrajectoryRecord> trajectories) {
		return trajectories.get(new Random().nextInt(trajectories.size()));
	}

	public int calculateLCSSSimilarity(final String left, final String right) {
		return this.parentService.apply(left, right);
	}

	private int maxDistance(List<Integer> distances) {
		return Collections.min(distances);
	}

	private int minDistance(List<Integer> distances) {
		return Collections.max(distances);
	}

	public TrajectoryRecord getMinimalInitials(final VisualTrajectoryRecord entryPoint, final List<TrajectoryRecord> allTrajectories) {
		TrajectoryRecord rightRecord = null;
		List<Character> entryPointChars = entryPoint.getInicalTrajectory().chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		int count = entryPointChars.size();
		for (TrajectoryRecord record : allTrajectories) {
			List<Character> recordChars = this.singleRecordService.translateToVisualisedTrajectory(record).getInicalTrajectory().chars().mapToObj(x -> (char) x)
					.collect(Collectors.toList());
			if (count == recordChars.size()) {
				rightRecord = record;
				break;
			}
		}
		return rightRecord;
	}

	/**
	 * Decides the furthiest trajectory from a random trajectory.
	 * 
	 * @param alltrajectories
	 * @param entryPointTrajecotry
	 * @return furthest {@link TrajectoryRecord} from a random trajectory of all trajectories
	 */
	public TrajectoryRecord getFurthiestRecord(final List<TrajectoryRecord> alltrajectories, final VisualTrajectoryRecord visualEntryPoint) {
		TrajectoryRecord furthiestRecord = new TrajectoryRecord();
		final List<VisualTrajectoryRecord> visualTrajectories = alltrajectories.stream().map(x -> this.singleRecordService.translateToVisualisedTrajectory(x))
				.collect(Collectors.toList());
		final List<String> initialTrajectories = visualTrajectories.stream().map(y -> y.getInicalTrajectory()).collect(Collectors.toList());
		final List<Integer> allDistances = initialTrajectories.stream().map(k -> this.calculateLCSSSimilarity(k, visualEntryPoint.getInicalTrajectory()))
				.collect(Collectors.toList());
		for (TrajectoryRecord record : alltrajectories) {
			final VisualTrajectoryRecord visualRecord = this.singleRecordService.translateToVisualisedTrajectory(record);
			if (this.calculateLCSSSimilarity(visualEntryPoint.getInicalTrajectory(), visualRecord.getInicalTrajectory()) == this.maxDistance(allDistances)) {
				furthiestRecord = record;
				break;
			}
		}
		return furthiestRecord;
	}

	/**
	 * Finds the best neighbour of the furthiest record i.e entry point
	 * 
	 * @param alltrajectories
	 * @param furthiestRecord
	 * @return bestNeighbour
	 */
	public TrajectoryRecord findBestNeighbour(final List<TrajectoryRecord> alltrajectories, final TrajectoryRecord furthiestRecord) {
		TrajectoryRecord bestNeighbour = new TrajectoryRecord();
		final VisualTrajectoryRecord furthiestRecordVisual = this.singleRecordService.translateToVisualisedTrajectory(furthiestRecord);
		final List<VisualTrajectoryRecord> visualTrajectories = alltrajectories.stream().map(x -> this.singleRecordService.translateToVisualisedTrajectory(x))
				.collect(Collectors.toList());
		final List<String> initialTrajectories = visualTrajectories.stream().map(y -> y.getInicalTrajectory()).collect(Collectors.toList());
		final List<Integer> allDistances = initialTrajectories.stream().map(k -> this.calculateLCSSSimilarity(k, furthiestRecordVisual.getInicalTrajectory()))
				.collect(Collectors.toList());
		for (TrajectoryRecord record : alltrajectories) {
			final VisualTrajectoryRecord visualTrajectoryRecord = this.singleRecordService.translateToVisualisedTrajectory(record);
			if (this.calculateLCSSSimilarity(furthiestRecordVisual.getInicalTrajectory(), visualTrajectoryRecord.getInicalTrajectory()) == this.minDistance(allDistances)) {
				bestNeighbour = this.getMinimalInitials(visualTrajectoryRecord, alltrajectories);
				// TODO: Fix NullPointerException!
			}
		}
		return bestNeighbour;
	}

	public ClusterRecord createClusterWithKElements(final List<TrajectoryRecord> alltrajectories, int k) {
		final TrajectoryRecord randomTrajectory = this.getRandomTrajectory(alltrajectories);
		final VisualTrajectoryRecord visualRandomTrajectory = this.singleRecordService.translateToVisualisedTrajectory(randomTrajectory);
		final TrajectoryRecord furthiestRecord = this.getFurthiestRecord(alltrajectories, visualRandomTrajectory);
		final List<TrajectoryRecord> clusterTrajectories = new ArrayList<TrajectoryRecord>();
		clusterTrajectories.add(furthiestRecord);
		alltrajectories.remove(randomTrajectory);
		for (int i = 1; i < k; i++) {
			TrajectoryRecord bestNeighbour = this.findBestNeighbour(alltrajectories, furthiestRecord);
			clusterTrajectories.add(bestNeighbour);
			alltrajectories.remove(bestNeighbour);
		}
		final List<VisualTrajectoryRecord> visualTrajectories = new ArrayList<VisualTrajectoryRecord>();
		for (TrajectoryRecord record : clusterTrajectories) {
			visualTrajectories.add(this.singleRecordService.translateToVisualisedTrajectory(record));
		}
		System.out.println("------------");
		for (VisualTrajectoryRecord v : visualTrajectories) {
			System.out.println(v.getInicalTrajectory());
		}
		System.out.println("------------");
		return null;
	}
}
