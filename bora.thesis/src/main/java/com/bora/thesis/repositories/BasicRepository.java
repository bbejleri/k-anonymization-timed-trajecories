package com.bora.thesis.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author: bora
 */
public abstract class BasicRepository {

	@PersistenceContext
	protected EntityManager entityManager;
}
