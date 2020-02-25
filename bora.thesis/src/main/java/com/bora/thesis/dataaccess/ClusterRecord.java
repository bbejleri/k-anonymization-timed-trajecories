package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class ClusterRecord {

	private String centroidSpatial;

	private String centroidTemporal;

	private int density;

	private long id;

	private List<TrajectoryRecord> trajectories;

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
	 * @return the trajectories
	 */
	public List<TrajectoryRecord> getTrajectories() {
		return trajectories;
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
	 * @param trajectories
	 *           the trajectories to set
	 */
	public void setTrajectories(List<TrajectoryRecord> trajectories) {
		this.trajectories = trajectories;
	}
}
