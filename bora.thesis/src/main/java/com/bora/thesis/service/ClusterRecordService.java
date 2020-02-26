package com.bora.thesis.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

	public TrajectoryRecord getRandomTrajectory(List<TrajectoryRecord> trajectories) {
		return trajectories.get(new Random().nextInt(trajectories.size()));
	}

	public int calculateLCSSSimilarity(final String left, final String right) {
		return this.parentService.apply(left, right);
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
	 * checks against full overlap of trajectories
	 * 
	 * @param string
	 * @return {@link Long} distance
	 */
	private long fullOverlap(final String string) {
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

	/**
	 * checks if 2 initial trajectories have the same length
	 * 
	 * @param s1
	 * @param s2
	 * @return {@link boolean} same
	 */
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
	 * compares 2 clusters on their location centroids and removes points which differ
	 * 
	 * @param c1
	 * @param c2
	 * @return {@link ClusterRecord} c1
	 */
	public ClusterRecord removeObsoletePointsOfCluster(final ClusterRecord c1, final ClusterRecord c2) {
		final HashMap<String, String> map = this.singleRecordService.getSymbolicZones();
		final String centroidSpatialC1 = c1.getCentroidSpatial();
		final String centroidSpatialC2 = c2.getCentroidSpatial();
		final List<Character> chars1 = centroidSpatialC1.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		final List<Character> chars2 = centroidSpatialC2.chars().mapToObj(x -> (char) x).collect(Collectors.toList());
		for (Character ch : chars1) {
			if (!chars2.contains(ch)) {
				for (TrajectoryRecord t : c1.getTrajectories()) {
					List<SingleRecord> points = t.getPoints();
					System.out.println("removing point");
					points.removeIf(x -> x.getZone().equalsIgnoreCase(map.get(ch.toString())));
				}
			}
		}
		return c1;
	}

	/**
	 * generates all relevant subsets of spatial centroid of a cluster
	 * 
	 * @param cluster
	 * @return {@link List<String>} filtered
	 */
	public List<String> getRelevantClusterSubsets(final ClusterRecord cluster) {
		List<String> list = this.parentService.getAllSubstrings(cluster.getCentroidSpatial(), new ArrayList<String>());
		List<String> filtered = list.stream().filter(s -> s.chars().count() > 1).collect(Collectors.toList());
		return filtered;
	}

	/**
	 * checks if each subset of a cluster's spatial centroid is found at at least one other spatial centroid cluster from all clusters
	 * 
	 * @param cluster
	 * @param clusters
	 * @return {@link Boolean} found
	 */
	public ClusterRecord checkClusterSubsets(final ClusterRecord cluster, final List<ClusterRecord> clusters) {
		boolean found = Boolean.TRUE;
		ClusterRecord prunedCluster = new ClusterRecord();
		final List<String> clusterSubsets = this.getRelevantClusterSubsets(cluster);
		System.out.println("-----------");
		System.out.println(cluster.getCentroidSpatial());
		System.out.println("Number of points: " + cluster.getCentroidSpatial().chars().count());
		System.out.println("-----------");
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
					System.out.println("Subset " + clusterSubsets.get(i) + " of trajectory " + cluster.getCentroidSpatial() + " not found at all.");
					List<Integer> similarities = new ArrayList<Integer>();
					for (ClusterRecord c1 : clusters) {
						similarities.add(this.calculateLCSSSimilarity(cluster.getCentroidSpatial(), c1.getCentroidSpatial()));
					}
					for (ClusterRecord c2 : clusters) {
						if (cluster.getId() != c2.getId()) {
							if (this.calculateLCSSSimilarity(cluster.getCentroidSpatial(), c2.getCentroidSpatial()) == this.minDistance(similarities)) {
								// TODO: FIX REMOVE!
								prunedCluster = this.removeObsoletePointsOfCluster(cluster, c2);
								// return prunedCluster;
							}
						}
					}
					break;
				} else {
					System.out.println("Subset " + clusterSubsets.get(i) + " of trajectory " + cluster.getCentroidSpatial() + " FOUND.");
				}
			}
		} else {
			System.out.println("Not checked");
			found = Boolean.TRUE;
		}
		System.out.println("//////");
		System.out.println(found);
		System.out.println("//////");
		return cluster;
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
						.fullOverlap(furthiestRecordVisual.getInicalTrajectory()) && this.singleRecordService.haveSameTemporalClassification(furthiestRecord, record)) {
					return record;
				}
			}
		}
		return null;
	}

	public ClusterRecord findBestCluster(final List<ClusterRecord> clusters, final TrajectoryRecord trajectory) {
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

	public ClusterRecord createClusterWithElements(final List<TrajectoryRecord> alltrajectories) {
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

	public List<ClusterRecord> kMember(final int k) {
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
			/**
			 * TODO: check if the subsets of location for any cluster centroid is found at another cluster centroid, if not find the maximum
			 * lcss between the actual cluster and other cluster centroids and prune the points to make the trajectory match 100%
			 */
		}
		return clusters;
	}

	public List<ClusterWrapper> getAllClusterTrajectories() {
		List<ClusterWrapper> clusterwrappers = new ArrayList<ClusterWrapper>();
		final List<ClusterRecord> clusters = this.kMember(5);
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

	public ClusterRecord getClusterById(final int id) {
		List<ClusterRecord> clusters = this.kMember(5);
		for (ClusterRecord c : clusters) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	/**
	 * @param clusters
	 * @return {@link List<SingleRecord>}
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
}
