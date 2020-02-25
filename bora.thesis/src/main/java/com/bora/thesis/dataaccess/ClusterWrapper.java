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

	private String centroidSpatial;

	private String centroidTemporal;

	private int density;

	private long id;

	private List<VisualTrajectoryRecord> visualtrajectories;

	private List<TrajectoryRecord> trajectoryRecords;

	/**
	 * @return the centroidSpatial
	 */
	public String getCentroidSpatial() {
		return centroidSpatial;
	}

	/**
	 * @return the centroidTemporal
	 */
	public String getCentroidTemporal() {
		return centroidTemporal;
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
	 * @param centroidSpatial
	 *           the centroidSpatial to set
	 */
	public void setCentroidSpatial(String centroidSpatial) {
		this.centroidSpatial = centroidSpatial;
	}

	/**
	 * @param centroidTemporal
	 *           the centroidTemporal to set
	 */
	public void setCentroidTemporal(String centroidTemporal) {
		this.centroidTemporal = centroidTemporal;
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
