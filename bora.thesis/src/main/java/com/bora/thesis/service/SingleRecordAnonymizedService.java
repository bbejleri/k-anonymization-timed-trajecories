package com.bora.thesis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.dataaccess.SingleRecordAnonymized;
import com.bora.thesis.repositories.SingleRecordAnonymizedRepository;

/**
 * @author: bora
 */
@Service
public class SingleRecordAnonymizedService {

	@Autowired
	private SingleRecordAnonymizedRepository singleRecordAnonymizedRepository;

	@Autowired
	private SingleRecordService singleRecordService;

	/**
	 * get {@link List<SingleRecordAnonymized>}
	 * 
	 * @return {@link List<SingleRecordAnonymized>}
	 */
	public List<SingleRecordAnonymized> getList() {
		return this.singleRecordAnonymizedRepository.getAll();
	}

	/**
	 * get {@link SingleRecordAnonymized} by mac
	 * 
	 * @param hashMac
	 * @return {@link SingleRecordAnonymized}
	 */
	public List<SingleRecordAnonymized> getByMacAddress(String hashMac) {
		return this.singleRecordAnonymizedRepository.getByMacAddress(hashMac);
	}

	/**
	 * transforms {@link SingleRecord} to {@link AnonymizedSingleRecord}
	 * 
	 * @param singleRecord
	 * @return {@link SingleRecord}
	 */
	public SingleRecordAnonymized fillValuesSingleRecordAnonymized(final SingleRecord singleRecord) {
		SingleRecordAnonymized databaseSingleRecordAnonymized = new SingleRecordAnonymized();
		final HashMap<List<String>, String> timestampmap = this.singleRecordService.getTemporalClassification();
		final HashMap<List<String>, String> zonemap = this.singleRecordService.getGeneralizedZoneNames();
		databaseSingleRecordAnonymized.setBatteryLevel(singleRecord.getBatteryLevel());
		databaseSingleRecordAnonymized.setEventtype(singleRecord.getEventtype());
		databaseSingleRecordAnonymized.setHashMac(singleRecord.getHashMac());
		databaseSingleRecordAnonymized.setMajor(singleRecord.getMajor());
		databaseSingleRecordAnonymized.setMinor(singleRecord.getMinor());
		databaseSingleRecordAnonymized.setRssi(singleRecord.getRssi());
		databaseSingleRecordAnonymized.setTechtype(singleRecord.getTechtype());
		// TODO: Something about the vendor - cron?
		databaseSingleRecordAnonymized.setTruncmac(singleRecord.getTruncmac());
		databaseSingleRecordAnonymized.setEpocutc(singleRecord.getEpocutc());
		databaseSingleRecordAnonymized.setZone(singleRecord.getZone());
		final String timestamp = singleRecord.getTimestamp().substring(11, 13);
		String anonymizedTimestamp = null;
		for (Map.Entry<List<String>, String> entry : timestampmap.entrySet()) {
			if (entry.getKey().contains(timestamp)) {
				anonymizedTimestamp = entry.getValue();
				break;
			}
		}
		databaseSingleRecordAnonymized.setInsert_timestamp(anonymizedTimestamp);
		return databaseSingleRecordAnonymized;
	}

	public boolean isAnonymized() {
		final boolean isAnonymized = CollectionUtils.isEmpty(this.getList()) ? Boolean.FALSE : Boolean.TRUE;
		return isAnonymized;
	}

	/**
	 * Save {@link SingleRecord}
	 */
	@Transactional
	public void save(final SingleRecordAnonymized record) {
		this.singleRecordAnonymizedRepository.save(record);
	}
}
