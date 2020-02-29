package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.ClusterWrapper;
import com.bora.thesis.dataaccess.SingleRecord;
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

	/**
	 * calculates Longest Common Subsequence of 2 {@link String}
	 * 
	 * @param left
	 * @param right
	 * @return {@link int} lcss value
	 */
	private int calculateLCSSSimilarity(final String left, final String right) {
		return this.parentService.apply(left, right);
	}

	/**
	 * checks if each subset of a cluster's spatial centroid is found at at least one other spatial centroid cluster from all clusters
	 * 
	 * @param cluster
	 * @param clusters
	 * @return {@link Boolean} found
	 */
	private ClusterRecord checkClusterSubsets(final ClusterRecord cluster, final List<ClusterRecord> clusters) {
		boolean found = Boolean.TRUE;
		ClusterRecord prunedCluster = new ClusterRecord();
		final List<String> clusterSubsets = this.getRelevantClusterSubsets(cluster);
		if (cluster.getCentroidSpatial().chars().count() > 2) {
			for (int i = 1; i < clusterSubsets.size(); i++) {
				for (ClusterRecord c : clusters) {
					if (cluster.getId() != c.getId()) {
						if (this.calculateLCSSSimilarity(clusterSubsets.get(i), c.getCentroidSpatial()) != clusterSubsets.get(i).length()) {
							found = Boolean.FALSE;
						} else {
							found = Boolean.TRUE;
							break;
						}
					}
				}
				if (!found) {
					List<Integer> similarities = new ArrayList<Integer>();
					for (ClusterRecord c1 : clusters) {
						if (cluster.getId() != c1.getId()) {
							similarities.add(this.calculateLCSSSimilarity(cluster.getCentroidSpatial(), c1.getCentroidSpatial()));
						}
					}
					for (ClusterRecord c2 : clusters) {
						if (cluster.getId() != c2.getId()) {
							if (this.calculateLCSSSimilarity(cluster.getCentroidSpatial(), c2.getCentroidSpatial()) == this.minDistance(similarities)) {
								prunedCluster = this.removeObsoletePointsOfCluster(cluster, c2);
								return prunedCluster;
							}
						}
					}
					break;
				}
			}
		}
		return null;
	}

	private ClusterRecord createClusterWithElements(final List<TrajectoryRecord> alltrajectories) {
		final TrajectoryRecord randomTrajectory = this.getRandomTrajectory(alltrajectories);
		final TrajectoryRecord furthiestRecord = this.getFurthiestRecord(alltrajectories, randomTrajectory);
		final List<TrajectoryRecord> clusterTrajectories = new ArrayList<TrajectoryRecord>();
		ClusterRecord cluster = new ClusterRecord();
		alltrajectories.remove(randomTrajectory);
		while (this.findBestNeighbour(alltrajectories, furthiestRecord) != null) {
			clusterTrajectories.add(this.findBestNeighbour(alltrajectories, furthiestRecord));
			alltrajectories.remove(this.findBestNeighbour(alltrajectories, furthiestRecord));
		}
		cluster.setCentroidSpatial(this.singleRecordService.translateToVisualisedTrajectory(clusterTrajectories.get(0)).getInicalTrajectory());
		cluster.setCentroidTemporal(this.singleRecordService.getGeneralTimestamp(clusterTrajectories.get(0)));
		cluster.setTrajectories(clusterTrajectories);
		cluster.setDensity(clusterTrajectories.size());
		return cluster;
	}

	/**
	 * double checks all clusters with the adjusted clusters and merges on case of findings
	 * 
	 * @param k
	 * @return {@link List<ClusterRecord> list}
	 */
	public List<ClusterRecord> finalizeKMember(final int k) {
		List<ClusterRecord> list = this.kMember(k);
		ClusterRecord cluster = null;
		List<ClusterRecord> adjustedClusters = new ArrayList<ClusterRecord>();
		List<Long> idsToRemove = new ArrayList<Long>();
		for (ClusterRecord record : list) {
			cluster = this.checkClusterSubsets(record, list);
			if (cluster != null) {
				adjustedClusters.add(cluster);
			}
		}
		if (adjustedClusters != null) {
			for (ClusterRecord c1 : list) {
				for (ClusterRecord c2 : adjustedClusters) {
					if (c1.getId() != c2.getId()) {
						if (c1.getCentroidSpatial().equals(c2.getCentroidSpatial()) && c1.getCentroidTemporal().equals(c2.getCentroidTemporal())) {
							this.mergeClusters(c1, c2);
							idsToRemove.add(c2.getId());
						}
					}
				}
			}
			for (Long id : idsToRemove) {
				Iterator<ClusterRecord> iter = list.iterator();
				while (iter.hasNext()) {
					ClusterRecord c = iter.next();
					if (c.getId() == id) {
						iter.remove();
					}
				}
			}
		}
		return list;
	}

	/**
	 * finds the most suitable cluster for a trajectory considering spatio-temporal positions
	 * 
	 * @param clusters
	 * @param trajectory
	 * @return {@link ClusterRecord} cluster
	 */
	private ClusterRecord findBestCluster(final List<ClusterRecord> clusters, final TrajectoryRecord trajectory) {
		final String trajectoryInitials = this.singleRecordService.translateToVisualisedTrajectory(trajectory).getInicalTrajectory();
		for (ClusterRecord cluster : clusters) {
			if (this.calculateLCSSSimilarity(cluster.getCentroidSpatial(), trajectoryInitials) == this.fullOverlap(trajectoryInitials) - 1) {
				if (this.singleRecordService.haveSameTemporalClassification(cluster.getTrajectories().get(0), trajectory)) {
					return cluster;
				}
			}
		}
		return null;
	}

	/**
	 * finds the best neighbour of the furthiest record i.e entry point
	 * 
	 * @param alltrajectories
	 * @param furthiestRecord
	 * @return {@link TrajectoryRecord} bestNeighbour
	 */
	public TrajectoryRecord findBestNeighbour(final List<TrajectoryRecord> alltrajectories, final TrajectoryRecord furthiestRecord) {
		final VisualTrajectoryRecord furthiestRecordVisual = this.singleRecordService.translateToVisualisedTrajectory(furthiestRecord);
		for (TrajectoryRecord record : alltrajectories) {
			final VisualTrajectoryRecord visualTrajectoryRecord = this.singleRecordService.translateToVisualisedTrajectory(record);
			if (this.sameNumberOfPoints(furthiestRecordVisual.getInicalTrajectory(), visualTrajectoryRecord.getInicalTrajectory())) {
				if (this.calculateLCSSSimilarity(furthiestRecordVisual.getInicalTrajectory(), visualTrajectoryRecord.getInicalTrajectory()) == this
						.fullOverlap(furthiestRecordVisual.getInicalTrajectory()) && this.singleRecordService.haveSameTemporalClassification(furthiestRecord, record)) {
					return record;
				}
			}
		}
		return null;
	}

	/**
	 * checks against full overlap of trajectories
	 * 
	 * @param string
	 * @return {@link Long} distance
	 */
	private long fullOverlap(final String string) {
		return string.chars().mapToObj(ch -> (char) ch).count();
	}

	/**
	 * generates all {@link List<TrajectoryRecord>} trajectories of all formed clusters
	 * 
	 * @return {@link List<ClusterWrapper>} clusterwrappers
	 */
	public List<ClusterWrapper> getAllClusterTrajectories() {
		List<ClusterWrapper> clusterwrappers = new ArrayList<ClusterWrapper>();
		final List<ClusterRecord> clusters = this.finalizeKMember(5);
		for (ClusterRecord c : clusters) {
			ClusterWrapper clusterwrapper = new ClusterWrapper();
			clusterwrapper.setId(c.getId());
			clusterwrapper.setCentroidSpatial(c.getCentroidSpatial());
			clusterwrapper.setCentroidTemporal(c.getCentroidTemporal());
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

	/**
	 * picks a random {@link TrajectoryRecord} trajectory out of the list {@link List<TrajectoryRecord>} of all trajectories
	 * 
	 * @param trajectories
	 * @return {@link TrajectoryRecord} random trajectory
	 */
	private TrajectoryRecord getRandomTrajectory(List<TrajectoryRecord> trajectories) {
		return trajectories.get(new Random().nextInt(trajectories.size()));
	}

	/**
	 * gets {@link ClusterRecord} cluster of specific id out of k-membered clusters
	 * 
	 * @param id
	 * @return {@link ClusterRecord} c
	 */
	public ClusterRecord getClusterById(final int id) {
		List<ClusterRecord> clusters = this.finalizeKMember(5);
		for (ClusterRecord c : clusters) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @param clusters
	 * @return {@link List<SingleRecord>} list
	 */
	public List<SingleRecord> getAllClustersPoints(List<ClusterRecord> clusters) {
		List<SingleRecord> list = new ArrayList<SingleRecord>();
		for (ClusterRecord c : clusters) {
			for (TrajectoryRecord t : c.getTrajectories()) {
				for (SingleRecord s : t.getPoints()) {
					list.add(s);
				}
			}
		}
		return list;
	}

	/**
	 * generates all relevant subsets of spatial centroid of a cluster
	 * 
	 * @param cluster
	 * @return {@link List<String>} filtered
	 */
	private List<String> getRelevantClusterSubsets(final ClusterRecord cluster) {
		List<String> list = this.parentService.getAllSubstrings(cluster.getCentroidSpatial(), new ArrayList<String>());
		List<String> filtered = list.stream().filter(s -> s.chars().count() > 1).collect(Collectors.toList());
		return filtered;
	}

	/**
	 * decides the furthiest trajectory from a random trajectory.
	 * 
	 * @param alltrajectories
	 * @param entryPointTrajecotry
	 * @return furthest {@link TrajectoryRecord} from a random trajectory of all trajectories
	 */
	private TrajectoryRecord getFurthiestRecord(final List<TrajectoryRecord> alltrajectories, final TrajectoryRecord entryPoint) {
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
	 * gives the minimal distance between 2 trajectories i.e they're almost not similar at all
	 * 
	 * @param distances
	 * @return {@link Long} distance
	 */
	private int maxDistance(List<Integer> distances) {
		return Collections.min(distances);
	}

	/**
	 * gives the minimal distance between 2 trajectories i.e they're almost similar
	 * 
	 * @param distances
	 * @return {@link Long} distance
	 */
	private int minDistance(List<Integer> distances) {
		return Collections.max(distances);
	}

	/**
	 * concatinates trajectories of the first cluster with trajectories of the second cluster and sets new density
	 * 
	 * @param c1
	 * @param c2
	 */
	private void mergeClusters(final ClusterRecord c1, final ClusterRecord c2) {
		List<TrajectoryRecord> mergedlist = Stream.concat(c1.getTrajectories().stream(), c2.getTrajectories().stream()).collect(Collectors.toList());
		c1.getTrajectories().clear();
		c1.setTrajectories(mergedlist);
		c1.setDensity(mergedlist.size());
	}

	/**
	 * creates clusters of pre-defined size K
	 * 
	 * @param k
	 * @return {@link List<ClusterRecord>} clusters
	 */
	private List<ClusterRecord> kMember(final int k) {
		final List<TrajectoryRecord> alltrajectories = this.singleRecordService.generateAllTrajectories();
		final List<ClusterRecord> clusters = new ArrayList<ClusterRecord>();
		int count = 1;
		while (alltrajectories.size() >= k) {
			ClusterRecord cluster = this.createClusterWithElements(alltrajectories);
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
						final long centroidLength = cluster.getCentroidSpatial().chars().count();
						if (initialsLength - centroidLength > 0) {
							cluster.getTrajectories().add(this.singleRecordService.removeObsoletePoint(cluster.getCentroidSpatial(), trajectory));
						}
					}
				}
			}
		}
		return clusters;
	}

	/**
	 * compares 2 clusters on their location centroids and removes points which differ
	 * 
	 * @param c1
	 * @param c2
	 * @return {@link ClusterRecord} c1
	 */
	private ClusterRecord removeObsoletePointsOfCluster(final ClusterRecord c1, final ClusterRecord c2) {
		final HashMap<String, String> map = this.singleRecordService.getSymbolicZones();
		final String centroidSpatialC1 = c1.getCentroidSpatial();
		final String centroidSpatialC2 = c2.getCentroidSpatial();
		final List<Character> chars1 = centroidSpatialC1.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		final List<Character> chars2 = centroidSpatialC2.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		for (Character ch : chars1) {
			if (!chars2.contains(ch)) {
				for (TrajectoryRecord t : c1.getTrajectories()) {
					List<SingleRecord> points = t.getPoints();
					points.removeIf(x -> x.getZone().equalsIgnoreCase(map.get(ch.toString())));
				}
			}
		}
		c1.setCentroidSpatial(this.singleRecordService.translateToVisualisedTrajectory(c1.getTrajectories().get(0)).getInicalTrajectory());
		return c1;
	}

	/**
	 * checks if 2 initial trajectories have the same length
	 * 
	 * @param s1
	 * @param s2
	 * @return {@link boolean} same
	 */
	private boolean sameNumberOfPoints(final String s1, final String s2) {
		boolean same = Boolean.FALSE;
		long points1 = s1.chars().mapToObj(x -> (char) x).count();
		long points2 = s2.chars().mapToObj(x -> (char) x).count();
		if (points1 == points2) {
			same = Boolean.TRUE;
		}
		return same;
	}
}
