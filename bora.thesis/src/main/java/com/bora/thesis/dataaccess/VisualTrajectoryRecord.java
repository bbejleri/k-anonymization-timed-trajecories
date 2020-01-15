package com.bora.thesis.dataaccess;

/**
 * @author: bora
 */
public class VisualTrajectoryRecord {

	private String vizualizedTrajectory;

	private String inicalTrajectory;

	private String namedTrajectory;

	/**
	 * @return the inicalTrajectory
	 */
	public String getInicalTrajectory() {
		return inicalTrajectory;
	}

	/**
	 * @return the namedTrajectory
	 */
	public String getNamedTrajectory() {
		return namedTrajectory;
	}

	/**
	 * @return the vizualizedTrajectory
	 */
	public String getVizualizedTrajectory() {
		return vizualizedTrajectory;
	}

	/**
	 * @param inicalTrajectory
	 *           the inicalTrajectory to set
	 */
	public void setInicalTrajectory(String inicalTrajectory) {
		this.inicalTrajectory = inicalTrajectory;
	}

	/**
	 * @param namedTrajectory
	 *           the namedTrajectory to set
	 */
	public void setNamedTrajectory(String namedTrajectory) {
		this.namedTrajectory = namedTrajectory;
	}

	/**
	 * @param vizualizedTrajectory
	 *           the vizualizedTrajectory to set
	 */
	public void setVizualizedTrajectory(String vizualizedTrajectory) {
		this.vizualizedTrajectory = vizualizedTrajectory;
	}
}
