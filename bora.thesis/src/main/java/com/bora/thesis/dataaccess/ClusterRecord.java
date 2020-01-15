package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class ClusterRecord {

	private List<VisualTrajectoryRecord> trajectories;

	/**
	 * @return the trajectories
	 */
	public List<VisualTrajectoryRecord> getTrajectories() {
		return trajectories;
	}

	/**
	 * @param trajectories
	 *           the trajectories to set
	 */
	public void setTrajectories(List<VisualTrajectoryRecord> trajectories) {
		this.trajectories = trajectories;
	}
}
