package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class ClusterRecord {

	private long id;

	private List<TrajectoryRecord> trajectories;

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
