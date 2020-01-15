package com.bora.thesis.dataaccess;

import java.util.List;

/**
 * @author: bora
 */
public class TrajectoryRecord {

	private List<SingleRecord> points;

	/**
	 * @return the points
	 */
	public List<SingleRecord> getPoints() {
		return points;
	}

	/**
	 * @param points
	 *           the points to set
	 */
	public void setPoints(List<SingleRecord> points) {
		this.points = points;
	}
}
