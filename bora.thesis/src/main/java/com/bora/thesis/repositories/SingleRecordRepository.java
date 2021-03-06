package com.bora.thesis.repositories;

import java.text.ParseException;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.bora.thesis.configs.SpringConfiguration;
import com.bora.thesis.dataaccess.SingleRecord;

/**
 * @author: bora
 */
@Repository
public class SingleRecordRepository extends BasicRepository implements SpringConfiguration {

	public void delete(final SingleRecord record) {
		entityManager.remove(record);
	}

	public List<SingleRecord> getAll() {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getAll", SingleRecord.class);
		List<SingleRecord> list = query.getResultList();
		return CollectionUtils.isEmpty(list) ? null : list;
	}

	public List<SingleRecord> getByMacAddress(final String hashmac) {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getByMac", SingleRecord.class);
		query.setParameter("hashmac", hashmac);
		List<SingleRecord> list = query.getResultList();
		return CollectionUtils.isEmpty(list) ? null : list;
	}

	public SingleRecord getById(final Long trackid) {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getById", SingleRecord.class);
		query.setParameter("trackid", trackid);
		SingleRecord record = query.getSingleResult();
		return ObjectUtils.isEmpty(record) ? null : record;
	}

	public List<SingleRecord> getByZone(final String zone) {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getByZone", SingleRecord.class);
		query.setParameter("zone", zone);
		List<SingleRecord> list = query.getResultList();
		return CollectionUtils.isEmpty(list) ? null : list;
	}

	public SingleRecord getBySingleZone(final String zone) {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getBySingleZone", SingleRecord.class);
		query.setParameter("zone", zone);
		SingleRecord record = query.getSingleResult();
		return ObjectUtils.isEmpty(record) ? null : record;
	}

	public List<SingleRecord> findAllRecordsBetween(final String startDate, final String endDate) throws ParseException {
		TypedQuery<SingleRecord> query = entityManager.createNamedQuery("SingleRecord.getDateRange", SingleRecord.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		List<SingleRecord> list = query.getResultList();
		return ObjectUtils.isEmpty(list) ? null : list;
	}

	@Modifying
	public void updateTimestamp(final String timestamp, final Long id, SingleRecord singleRecordDatabase) {
		singleRecordDatabase.setTimestamp(timestamp);
		entityManager.merge(singleRecordDatabase);
	}
}
