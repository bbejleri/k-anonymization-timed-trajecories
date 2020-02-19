package com.bora.thesis.repositories;

import org.springframework.stereotype.Repository;

import com.bora.thesis.configs.SpringConfiguration;
import com.bora.thesis.dataaccess.SingleRecordAnonymized;

/**
 * @author: bora
 */
@Repository
public class SingleRecordAnonymizedRepository extends BasicRepository implements SpringConfiguration {

	public void save(final SingleRecordAnonymized record) {
		entityManager.persist(record);
	}
}
