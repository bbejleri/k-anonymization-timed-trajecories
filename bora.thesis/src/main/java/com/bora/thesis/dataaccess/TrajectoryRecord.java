package com.bora.thesis.dataaccess;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bora
 */
public class TrajectoryRecord {

	private List<SingleRecord> points;

	private String vendor;

	public TrajectoryRecord() {
		points = new ArrayList<SingleRecord>();
	}

	/**
	 * @return the points
	 */
	public List<SingleRecord> getPoints() {
		return points;
	}

	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @param points
	 *           the points to set
	 */
	public void setPoints(List<SingleRecord> points) {
		this.points = points;
	}

	/**
	 * @param vendor
	 *           the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
}
