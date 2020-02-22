package com.bora.thesis.repositories;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.bora.thesis.configs.SpringConfiguration;
import com.bora.thesis.dataaccess.SingleRecordAnonymized;

/**
 * @author: bora
 */
@Repository
public class SingleRecordAnonymizedRepository extends BasicRepository implements SpringConfiguration {

	/**
	 * get all records {@link List<SingleRecordAnonymized>}
	 * 
	 * @return {@link List<SingleRecordAnonymized>}
	 */
	public List<SingleRecordAnonymized> getAll() {
		TypedQuery<SingleRecordAnonymized> query = entityManager.createNamedQuery("SingleRecordAnonymized.getAll", SingleRecordAnonymized.class);
		List<SingleRecordAnonymized> list = query.getResultList();
		return CollectionUtils.isEmpty(list) ? null : list;
	}

	/**
	 * get record {@link SingleRecordAnonymized} by hashmac
	 * 
	 * @param hashmac
	 * @return {@link SingleRecordAnonymized}
	 */
	public List<SingleRecordAnonymized> getByMacAddress(final String hashmac) {
		TypedQuery<SingleRecordAnonymized> query = entityManager.createNamedQuery("SingleRecordAnonymized.getByMac", SingleRecordAnonymized.class);
		query.setParameter("hashmac", hashmac);
		final List<SingleRecordAnonymized> result = query.getResultList();
		return CollectionUtils.isEmpty(result) ? null : result;
	}

	/**
	 * save record {@link SingleRecordAnonymized}
	 * 
	 * @param record
	 */
	public void save(final SingleRecordAnonymized record) {
		entityManager.persist(record);
	}
}
