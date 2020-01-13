package com.bora.thesis.dataaccess;

/**
 * @author: bora
 */
public class TrajectoryRecord {

	private String vizualizedTrajectory;

	private String inicalTrajectory;

	/**
	 * @return the vizualizedTrajectory
	 */
	public String getVizualizedTrajectory() {
		return vizualizedTrajectory;
	}

	/**
	 * @return the inicalTrajectory
	 */
	public String getInicalTrajectory() {
		return inicalTrajectory;
	}

	/**
	 * @param vizualizedTrajectory
	 *           the vizualizedTrajectory to set
	 */
	public void setVizualizedTrajectory(String vizualizedTrajectory) {
		this.vizualizedTrajectory = vizualizedTrajectory;
	}

	/**
	 * @param inicalTrajectory
	 *           the inicalTrajectory to set
	 */
	public void setInicalTrajectory(String inicalTrajectory) {
		this.inicalTrajectory = inicalTrajectory;
	}
}
