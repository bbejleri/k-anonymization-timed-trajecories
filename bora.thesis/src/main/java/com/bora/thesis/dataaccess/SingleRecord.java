package com.bora.thesis.dataaccess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.bora.thesis.configs.EntityModel;

/**
 * @author: bora
 */
@Entity(name = "flowtrack_raw_copy")
@NamedQueries({ @NamedQuery(name = "SingleRecord.getAll", query = "SELECT a FROM flowtrack_raw_copy a"),
		@NamedQuery(name = "SingleRecord.getByMac", query = "SELECT s FROM flowtrack_raw_copy s WHERE s.hashMac = :hashmac"),
		@NamedQuery(name = "SingleRecord.getById", query = "SELECT i FROM flowtrack_raw_copy i WHERE i.trackid = :trackid"),
		@NamedQuery(name = "SingleRecord.getByZone", query = "SELECT z FROM flowtrack_raw_copy z WHERE z.zone = :zone"),
		@NamedQuery(name = "SingleRecord.getByZoneAndTimestamp", query = "SELECT r FROM flowtrack_raw_copy r WHERE r.zone = :zone AND r.insert_timestamp = :timestamp"),
		@NamedQuery(name = "SingleRecord.getDateRange", query = "SELECT e FROM flowtrack_raw_copy e WHERE e.insert_timestamp BETWEEN :startDate AND :endDate") })
@Table(name = "flowtrack_raw_copy")
@DynamicUpdate
public class SingleRecord implements EntityModel {

	private static final long serialVersionUID = 282835223717958790L;

	@Id
	@Column(name = "trackid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long trackid;

	@Column(name = "eventtype")
	private int eventtype;

	@Column(name = "techtype")
	private int techtype;

	@Column(name = "zone")
	private String zone;

	@Column(name = "hashmac")
	private String hashMac;

	@Column(name = "battlevel")
	private String batteryLevel;

	@Column(name = "major")
	private String major;

	@Column(name = "minor")
	private String minor;

	@Column(name = "rssi")
	private String rssi;

	@Column(name = "insert_timestamp")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String insert_timestamp;

	@Column(name = "truncmac")
	private String truncmac;

	/**
	 * @return the eventtype
	 */
	public int getEventtype() {
		return eventtype;
	}

	/**
	 * @return the techtype
	 */
	public int getTechtype() {
		return techtype;
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @return the hashMac
	 */
	public String getHashMac() {
		return hashMac;
	}

	/**
	 * @return the batteryLevel
	 */
	public String getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * @return the major
	 */
	public String getMajor() {
		return major;
	}

	/**
	 * @return the minor
	 */
	public String getMinor() {
		return minor;
	}

	/**
	 * @return the rssi
	 */
	public String getRssi() {
		return rssi;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return insert_timestamp;
	}

	/**
	 * @return the truncmac
	 */
	public String getTruncmac() {
		return truncmac;
	}

	/**
	 * @param eventtype
	 *           the eventtype to set
	 */
	public void setEventtype(int eventtype) {
		this.eventtype = eventtype;
	}

	/**
	 * @param techtype
	 *           the techtype to set
	 */
	public void setTechtype(int techtype) {
		this.techtype = techtype;
	}

	/**
	 * @param zone
	 *           the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * @param hashMac
	 *           the hashMac to set
	 */
	public void setHashMac(String hashMac) {
		this.hashMac = hashMac;
	}

	/**
	 * @param batteryLevel
	 *           the batteryLevel to set
	 */
	public void setBatteryLevel(String batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	/**
	 * @param major
	 *           the major to set
	 */
	public void setMajor(String major) {
		this.major = major;
	}

	/**
	 * @param minor
	 *           the minor to set
	 */
	public void setMinor(String minor) {
		this.minor = minor;
	}

	/**
	 * @param rssi
	 *           the rssi to set
	 */
	public void setRssi(String rssi) {
		this.rssi = rssi;
	}

	/**
	 * @param timestamp
	 *           the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.insert_timestamp = timestamp;
	}

	/**
	 * @param truncmac
	 *           the truncmac to set
	 */
	public void setTruncmac(String truncmac) {
		this.truncmac = truncmac;
	}

	/**
	 * @return the trackid
	 */
	public long getTrackid() {
		return trackid;
	}

	/**
	 * @param trackid
	 *           the trackid to set
	 */
	public void setTrackid(long trackid) {
		this.trackid = trackid;
	}
}
