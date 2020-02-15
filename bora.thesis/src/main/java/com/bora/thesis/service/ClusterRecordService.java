package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.ClusterWrapper;
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

	private long minDistance(final String string) {
		return string.chars().mapToObj(ch -> (char) ch).count();
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

	public boolean sameNumberOfPoints(final String s1, final String s2) {
		boolean same = Boolean.FALSE;
		long points1 = s1.chars().mapToObj(x -> (char) x).count();
		long points2 = s2.chars().mapToObj(x -> (char) x).count();
		if (points1 == points2) {
			same = Boolean.TRUE;
		}
		return same;
	}

	/**
	 * Decides the furthiest trajectory from a random trajectory.
	 * 
	 * @param alltrajectories
	 * @param entryPointTrajecotry
	 * @return furthest {@link TrajectoryRecord} from a random trajectory of all trajectories
	 */
	public TrajectoryRecord getFurthiestRecord(final List<TrajectoryRecord> alltrajectories, final TrajectoryRecord entryPoint) {
		TrajectoryRecord furthiestRecord = new TrajectoryRecord();
		final VisualTrajectoryRecord visualEntryPoint = this.singleRecordService.translateToVisualisedTrajectory(entryPoint);
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
		final VisualTrajectoryRecord furthiestRecordVisual = this.singleRecordService.translateToVisualisedTrajectory(furthiestRecord);
		for (TrajectoryRecord record : alltrajectories) {
			final VisualTrajectoryRecord visualTrajectoryRecord = this.singleRecordService.translateToVisualisedTrajectory(record);
			if (this.sameNumberOfPoints(furthiestRecordVisual.getInicalTrajectory(), visualTrajectoryRecord.getInicalTrajectory())) {
				if (this.calculateLCSSSimilarity(furthiestRecordVisual.getInicalTrajectory(), visualTrajectoryRecord.getInicalTrajectory()) == this
						.minDistance(furthiestRecordVisual.getInicalTrajectory()) && this.singleRecordService.haveSameTemporalClassification(furthiestRecord, record)) {
					return record;
				}
			}
		}
		return null;
	}

	public ClusterRecord findBestCluster(final List<ClusterRecord> clusters, final TrajectoryRecord trajectory) {
		final String trajectoryInitials = this.singleRecordService.translateToVisualisedTrajectory(trajectory).getInicalTrajectory();
		for (ClusterRecord cluster : clusters) {
			if (this.calculateLCSSSimilarity(cluster.getCentroid(), trajectoryInitials) == this.minDistance(trajectoryInitials) - 1) {
				return cluster;
			}
		}
		return null;
	}

	public ClusterRecord createClusterWithKElements(final List<TrajectoryRecord> alltrajectories, final int k) {
		final TrajectoryRecord randomTrajectory = this.getRandomTrajectory(alltrajectories);
		final TrajectoryRecord furthiestRecord = this.getFurthiestRecord(alltrajectories, randomTrajectory);
		final List<TrajectoryRecord> clusterTrajectories = new ArrayList<TrajectoryRecord>();
		ClusterRecord cluster = new ClusterRecord();
		alltrajectories.remove(randomTrajectory);
		while (this.findBestNeighbour(alltrajectories, furthiestRecord) != null) {
			clusterTrajectories.add(this.findBestNeighbour(alltrajectories, furthiestRecord));
			alltrajectories.remove(this.findBestNeighbour(alltrajectories, furthiestRecord));
		}
		cluster.setCentroid(this.singleRecordService.translateToVisualisedTrajectory(clusterTrajectories.get(0)).getInicalTrajectory() + " "
				+ this.singleRecordService.getGeneralTimestamp(clusterTrajectories.get(0)));
		cluster.setTrajectories(clusterTrajectories);
		cluster.setDensity(clusterTrajectories.size());
		return cluster;
	}

	public List<ClusterRecord> kMember(final int k) {
		final List<TrajectoryRecord> alltrajectories = this.singleRecordService.generateAllTrajectories();
		final List<ClusterRecord> clusters = new ArrayList<ClusterRecord>();
		int count = 1;
		while (alltrajectories.size() >= k) {
			ClusterRecord cluster = this.createClusterWithKElements(alltrajectories, k);
			if (cluster.getTrajectories().size() >= k) {
				cluster.setId(count);
				clusters.add(cluster);
				count++;
			} else {
				for (TrajectoryRecord trajectory : cluster.getTrajectories()) {
					cluster = this.findBestCluster(clusters, trajectory);
					if (ObjectUtils.isNotEmpty(cluster)) {
						VisualTrajectoryRecord visualTrajectory = this.singleRecordService.translateToVisualisedTrajectory(trajectory);
						final long initialsLength = visualTrajectory.getInicalTrajectory().chars().count();
						final long centroidLength = cluster.getCentroid().substring(0, (int) initialsLength - 1).chars().count();
						if (initialsLength - centroidLength > 0) {
							// cluster.getTrajectories().add(this.singleRecordService.removeObsoletePoint(cluster.getCentroid(), trajectory));
						}
					}
				}
			}
		}
		return clusters;
	}

	public List<ClusterWrapper> getAllClusterTrajectories() {
		List<ClusterWrapper> clusterwrappers = new ArrayList<ClusterWrapper>();
		final List<ClusterRecord> clusters = this.kMember(5);
		for (ClusterRecord c : clusters) {
			ClusterWrapper clusterwrapper = new ClusterWrapper();
			clusterwrapper.setId(c.getId());
			clusterwrapper.setCentroid(c.getCentroid());
			clusterwrapper.setDensity(c.getTrajectories().size());
			List<VisualTrajectoryRecord> visuals = new ArrayList<VisualTrajectoryRecord>();
			List<TrajectoryRecord> trajectories = new ArrayList<TrajectoryRecord>();
			for (TrajectoryRecord t : c.getTrajectories()) {
				visuals.add(this.singleRecordService.translateToVisualisedTrajectory(t));
				trajectories.add(t);
			}
			clusterwrapper.setVisualtrajectories(visuals);
			clusterwrapper.setTrajectoryRecords(trajectories);
			clusterwrappers.add(clusterwrapper);
		}
		return clusterwrappers;
	}

	public List<TrajectoryRecord> getClusterById(final int id) {
		List<ClusterRecord> clusters = this.kMember(5);
		for (ClusterRecord c : clusters) {
			if (c.getId() == id) {
				return c.getTrajectories();
			}
		}
		return null;
	}
}
