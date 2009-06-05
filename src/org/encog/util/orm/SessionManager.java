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

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the manager class for Encog database object relation mapping(ORM).
 * This is based on Hibernate. The primary purpose for this class is to provide
 * a way for sessions to be created.
 * 
 * @author jheaton
 * 
 */
public class SessionManager {

	/**
	 * The session factory to use.
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * The annotation config.
	 */
	private AnnotationConfiguration config;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a new session manager.
	 */
	public SessionManager() {
		this(new AnnotationConfiguration().configure());
	}

	/**
	 * Construct a session manager using annotation config.
	 * @param config The annotation configuration.
	 */
	public SessionManager(final AnnotationConfiguration config) {
		init(config);
	}

	/**
	 * Construct a session manager using the specified HSQL 
	 * path.
	 * @param path HSQL path.
	 */
	public SessionManager(final String path) {
		this("org.hsqldb.jdbcDriver", "jdbc:hsqldb:file:" + path
				+ ";type=cached;shutdown=true", "sa", "",
				"org.hibernate.dialect.HSQLDialect");
	}

	/**
	 * Construct a session manager from the specified connect info.
	 * @param driver The JDBC driver.
	 * @param url The JDBC URL.
	 * @param uid The user id.
	 * @param pwd The password.
	 * @param dialect The Hibernate dialect to use.
	 */
	public SessionManager(final String driver, final String url,
			final String uid, final String pwd, final String dialect) {
		final AnnotationConfiguration config = new AnnotationConfiguration();
		config.setProperty("hibernate.connection.driver_class", driver);
		config.setProperty("hibernate.connection.url", url);
		config.setProperty("hibernate.connection.username", uid);
		config.setProperty("hibernate.connection.password", pwd);
		config.setProperty("hibernate.dialect", dialect);

		config.setProperty("hibernate.connection.pool_size", "1");
		config.setProperty("hibernate.current_session_context_class", "thread");
		config.setProperty("hibernate.cache.provider_class",
				"org.hibernate.cache.NoCacheProvider");
		config.setProperty("hibernate.show_sql", "false");
		init(config);
	}

	/**
	 * Export the DDL, this creates the tables needed.
	 */
	public synchronized void export() {
		final SchemaExport export = new SchemaExport(this.config);
		export.create(true, true);
	}

	/**
	 * Init the session manager using the specified annotation config.
	 * @param config The annotation config.
	 */
	private void init(final AnnotationConfiguration config) {
		try {
			this.config = config;

			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Fix.class);
			this.config
					.addAnnotatedClass(org.encog.nlp.lexicon.data.Alias.class);
			this.config
					.addAnnotatedClass(org.encog.nlp.lexicon.data.Word.class);
			this.config
					.addAnnotatedClass(org.encog.nlp.lexicon.data.Lemma.class);
			this.config.addAnnotatedClass(org.encog.nlp.lexicon.data.Fix.class);
			this.config
				.addAnnotatedClass(
						org.encog.nlp.lexicon.data.WordType.class);
			this.config
				.addAnnotatedClass(
						org.encog.nlp.lexicon.data.WordTypePossibility.class);
			this.config
					.addAnnotatedClass(org.encog.bot.spider.workload.WorkloadItem.class);

			this.sessionFactory = config.buildSessionFactory();
		} catch (final Throwable ex) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Initial SessionFactory creation failed."
						+ ex);
			}
			throw new ORMError(ex);
		}
	}

	/**
	 * Open an ORM session.
	 * @return An ORM session.
	 */
	public synchronized ORMSession openSession() {
		final org.hibernate.Session hibernateSession = this.sessionFactory
				.openSession(new DataObjectInterceptor());
		final ORMSession result = new ORMSession(hibernateSession);

		return result;

	}

}
