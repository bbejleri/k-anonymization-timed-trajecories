package com.bora.thesis.dataaccess;

/**
 * @author: bora
 */
public class VisualTrajectoryRecord {

	private String vizualizedTrajectory;

	private String inicalTrajectory;

	private String namedTrajectory;

	private String anonymizedTrajectory;

	private String vendor;

	/**
	 * @return the anonymizedTrajectory
	 */
	public String getAnonymizedTrajectory() {
		return anonymizedTrajectory;
	}

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
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @return the vizualizedTrajectory
	 */
	public String getVizualizedTrajectory() {
		return vizualizedTrajectory;
	}

	/**
	 * @param anonymizedTrajectory
	 *           the anonymizedTrajectory to set
	 */
	public void setAnonymizedTrajectory(String anonymizedTrajectory) {
		this.anonymizedTrajectory = anonymizedTrajectory;
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
	 * @param vendor
	 *           the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	/**
	 * @param vizualizedTrajectory
	 *           the vizualizedTrajectory to set
	 */
	public void setVizualizedTrajectory(String vizualizedTrajectory) {
		this.vizualizedTrajectory = vizualizedTrajectory;
	}
}
