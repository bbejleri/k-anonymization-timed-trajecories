package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class ClusterWrapper {

	private long id;

	private List<VisualTrajectoryRecord> visualtrajectories;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the visualtrajectories
	 */
	public List<VisualTrajectoryRecord> getVisualtrajectories() {
		return visualtrajectories;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param visualtrajectories
	 *           the visualtrajectories to set
	 */
	public void setVisualtrajectories(List<VisualTrajectoryRecord> visualtrajectories) {
		this.visualtrajectories = visualtrajectories;
	}
}
