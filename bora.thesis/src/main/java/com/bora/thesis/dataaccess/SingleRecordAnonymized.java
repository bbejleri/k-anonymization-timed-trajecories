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

import com.bora.thesis.configs.EntityModel;

/**
 * @author: bora
 */
@Entity(name = "flowtrack_anon")
@NamedQueries({ @NamedQuery(name = "SingleRecordAnonymized.getAll", query = "SELECT a FROM flowtrack_anon a"),
		@NamedQuery(name = "SingleRecordAnonymized.getByMac", query = "SELECT s FROM flowtrack_anon s WHERE s.hashMac = :hashmac") })
@Table(name = "flowtrack_anon")
@DynamicUpdate
public class SingleRecordAnonymized implements EntityModel {

	private static final long serialVersionUID = -9102390057621684045L;

	@Id
	@Column(name = "trackid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long trackid;

	@Column(name = "eventtype")
	private int eventtype;

	@Column(name = "epocutc")
	private int epocutc;

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
	private String insert_timestamp;

	@Column(name = "truncmac")
	private String truncmac;

	/**
	 * @return the trackid
	 */
	public long getTrackid() {
		return trackid;
	}

	/**
	 * @return the eventtype
	 */
	public int getEventtype() {
		return eventtype;
	}

	/**
	 * @return the epocutc
	 */
	public int getEpocutc() {
		return epocutc;
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
	 * @return the insert_timestamp
	 */
	public String getInsert_timestamp() {
		return insert_timestamp;
	}

	/**
	 * @return the truncmac
	 */
	public String getTruncmac() {
		return truncmac;
	}

	/**
	 * @param trackid
	 *           the trackid to set
	 */
	public void setTrackid(long trackid) {
		this.trackid = trackid;
	}

	/**
	 * @param eventtype
	 *           the eventtype to set
	 */
	public void setEventtype(int eventtype) {
		this.eventtype = eventtype;
	}

	/**
	 * @param epocutc
	 *           the epocutc to set
	 */
	public void setEpocutc(int epocutc) {
		this.epocutc = epocutc;
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
	 * @param insert_timestamp
	 *           the insert_timestamp to set
	 */
	public void setInsert_timestamp(String insert_timestamp) {
		this.insert_timestamp = insert_timestamp;
	}

	/**
	 * @param truncmac
	 *           the truncmac to set
	 */
	public void setTruncmac(String truncmac) {
		this.truncmac = truncmac;
	}
}
