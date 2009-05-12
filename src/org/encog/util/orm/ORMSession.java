/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.util.orm;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hibernate session. This class provides access to the Hibernate persisted
 * database.
 * 
 * @author jheaton
 * 
 */
public class ORMSession {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Hibernate session.
	 */
	private final org.hibernate.Session session;
	
	/**
	 * The Hibernate transaction.
	 */
	private org.hibernate.Transaction transaction;

	/**
	 * Construct an ORMSession based on a Hibernate session.
	 * @param session The Hibernate session.
	 */
	public ORMSession(final Session session) {
		this.session = session;
	}

	/**
	 * Begin the transaction.
	 */
	public void begin() {
		this.transaction = this.session.beginTransaction();
	}

	/**
	 * Clear the Hibernate session.
	 */
	public void clear() {
		this.session.clear();

	}

	/**
	 * Close the session.
	 */
	public void close() {
		try {
			this.session.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commit the transaction.
	 */
	public void commit() {
		this.transaction.commit();
	}

	/**
	 * Create a HQL query.
	 * @param sql The HQL query.
	 * @return A Hibernate query.
	 */
	public Query createQuery(final String sql) {
		return this.session.createQuery(sql);
	}

	/**
	 * Create a Hibernate SQL query.
	 * @param sql The SQL to query on.
	 * @return A Hibernate query.
	 */
	public Query createSQLQuery(final String sql) {
		return this.session.createSQLQuery(sql);
	}

	/**
	 * Delete the specified persisted object.
	 * @param obj The object to delete.
	 */
	public void delete(final Object obj) {
		this.session.delete(obj);
	}

	/**
	 * Evict the specified object from the cache.
	 * @param obj The object to evict.
	 */
	public void evict(final DataObject obj) {
		this.session.evict(obj);
	}

	/**
	 * Execute the specified SQL.
	 * @param sql The SQL to execute.
	 */
	public void executeSQL(final String sql) {
		createSQLQuery(sql).executeUpdate();
	}

	/**
	 * Flush this session.
	 */
	public void flush() {
		this.session.flush();
	}

	/**
	 * Rollback this transaction.
	 */
	public void rollback() {
		this.transaction.rollback();
	}

	/**
	 * Save the specified object.
	 * @param obj The persistant object to save.
	 */
	public void save(final DataObject obj) {
		obj.validate();
		this.session.save(obj);
	}

}
