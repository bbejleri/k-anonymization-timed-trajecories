package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
/**
 * @author User
 *
 */
public class ClusterWrapper {

	private String centroid;

	private int density;

	private long id;

	private List<VisualTrajectoryRecord> visualtrajectories;

	private List<TrajectoryRecord> trajectoryRecords;

	/**
	 * @return the centroid
	 */
	public String getCentroid() {
		return centroid;
	}

	/**
	 * @return the density
	 */
	public int getDensity() {
		return density;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the trajectoryRecords
	 */
	public List<TrajectoryRecord> getTrajectoryRecords() {
		return trajectoryRecords;
	}

	/**
	 * @return the visualtrajectories
	 */
	public List<VisualTrajectoryRecord> getVisualtrajectories() {
		return visualtrajectories;
	}

	/**
	 * @param centroid
	 *           the centroid to set
	 */
	public void setCentroid(String centroid) {
		this.centroid = centroid;
	}

	/**
	 * @param density
	 *           the density to set
	 */
	public void setDensity(int density) {
		this.density = density;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param trajectoryRecords
	 *           the trajectoryRecords to set
	 */
	public void setTrajectoryRecords(List<TrajectoryRecord> trajectoryRecords) {
		this.trajectoryRecords = trajectoryRecords;
	}

	/**
	 * @param visualtrajectories
	 *           the visualtrajectories to set
	 */
	public void setVisualtrajectories(List<VisualTrajectoryRecord> visualtrajectories) {
		this.visualtrajectories = visualtrajectories;
	}
}
