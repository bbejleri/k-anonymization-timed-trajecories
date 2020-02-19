package com.bora.thesis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	 * transforms {@link SingleRecord} to {@link AnonymizedSingleRecord}
	 * 
	 * @param singleRecord
	 * @return {@link SingleRecord}
	 */
	public SingleRecordAnonymized fillValuesSingleRecordAnonymized(final SingleRecord singleRecord) {
		SingleRecordAnonymized databaseSingleRecordAnonymized = new SingleRecordAnonymized();
		final HashMap<List<String>, String> map = this.singleRecordService.getTemporalClassification();
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
		// TODO: Zone Name Generalized?
		databaseSingleRecordAnonymized.setZone(singleRecord.getZone());
		final String timestamp = singleRecord.getTimestamp().substring(11, 13);
		String anonymizedTimestamp = null;
		for (Map.Entry<List<String>, String> entry : map.entrySet()) {
			if (entry.getKey().contains(timestamp)) {
				anonymizedTimestamp = entry.getValue();
				break;
			}
		}
		databaseSingleRecordAnonymized.setInsert_timestamp(anonymizedTimestamp);
		return databaseSingleRecordAnonymized;
	}

	/**
	 * Save {@link SingleRecord}
	 */
	@Transactional
	public void save(final SingleRecordAnonymized record) {
		this.singleRecordAnonymizedRepository.save(record);
	}
}
