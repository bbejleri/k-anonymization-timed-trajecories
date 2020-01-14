package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class ClusterRecord {

	private List<TrajectoryRecord> trajectories;

	/**
	 * @return the trajectories
	 */
	public List<TrajectoryRecord> getTrajectories() {
		return trajectories;
	}

	/**
	 * @param trajectories
	 *           the trajectories to set
	 */
	public void setTrajectories(List<TrajectoryRecord> trajectories) {
		this.trajectories = trajectories;
	}
}
