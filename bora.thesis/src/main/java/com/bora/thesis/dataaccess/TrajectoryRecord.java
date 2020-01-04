package com.bora.thesis.dataaccess;

/**
 * @author: bora
 */
public class TrajectoryRecord {

	private int eventtype;

	private String zone;

	private String time;

	/**
	 * @return the eventtype
	 */
	public int getEventtype() {
		return eventtype;
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param eventtype
	 *           the eventtype to set
	 */
	public void setEventtype(int eventtype) {
		this.eventtype = eventtype;
	}

	/**
	 * @param zone
	 *           the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * @param time
	 *           the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
}
