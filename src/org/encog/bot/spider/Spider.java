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
package org.encog.bot.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.encog.util.concurrency.EncogConcurrency;
import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A spider is a special sort of bot that crawls the pages on a web site. It
 * begins with one entry web page and then finds all of the links visiting those
 * pages as well. All data found is reported to the SpiderReportable interface.
 * 
 * The queue of pages to access must be stored in a database. This database is
 * accessed using the Hibernate ORM. For shorter spidering tasks an in-memory
 * database can be used such as HSQL in Java.
 * 
 * Spiders must typically wait for the pages that they are accessing to load.
 * Because if this it is very advantageous to use a spider in a multithreaded
 * way. To do this the spider uses the Encog threading framework, which in turn
 * makes use of whatever underlying thread pool is provided by either Java or
 * C#. For more information about multithreading, refer to the EncogConcurrency
 * class.
 * 
 * @author jheaton
 * 
 */
public class Spider {
	
	/**
	 * The default timeout.
	 */
	public static final int DEFAULT_TIMEOUT = 1000;

	/**
	 * The ORM session to use for storing the workload.
	 */
	private ORMSession session;
	
	/**
	 * The ORM manager to use for storing the workload.
	 */
	private final SessionManager manager;
	
	/**
	 * The object that we should report progress to.
	 */
	private final SpiderReportable report;
	
	/**
	 * The timeout value for HTTP connections, in miliseconds.
	 */
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * The default user agent.
	 */
	private String userAgent = "Mozilla/5.0";
	
	/**
	 * The maximum URL size.
	 */
	private final int maxURLSize = 255;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a new spider.
	 * @param manager The ORM manger to use.
	 * @param report The object to report progress to.
	 */
	public Spider(final SessionManager manager, final SpiderReportable report) {
		this.manager = manager;
		this.report = report;
	}

	/**
	 * Add a URL to the spider for processing.
	 * @param url The URL to add.
	 * @param source The source the URL came from.
	 */
	public void addURL(final URL url, final WorkloadItem source) {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Adding URL to spider queue: " + url);
		}

		// does the URL exist already?
		final Query q = this.session
				.createQuery(
					"SELECT count(*) FROM WorkloadItem WHERE url = :u");
		q.setString("u", url.toString());
		final Long i = (Long) q.uniqueResult();
		if (i == 0) {
			// add a new URL
			final WorkloadItem item = new WorkloadItem();
			item.setHost(url.getHost());
			item.setUrl(url.toString());
			item.setStatus(WorkloadStatus.QUEUED);
			item.setSource(source);
			if (source == null) {
				item.setDepth(0);
			} else {
				item.setDepth(source.getDepth() + 1);
			}
			this.session.save(item);
		}
	}

	/**
	 * Convert the specified String to a URL. If the string is too long or has
	 * other issues, throw a BotError.
	 * 
	 * @param aurl
	 *            A String to convert into a URL.
	 * @return The URL.
	 */
	public URL convertURL(final String aurl) {
		URL result = null;

		final String url = aurl.trim();
		if ((this.maxURLSize != -1) && (url.length() > this.maxURLSize)) {
			throw new SpiderError("URL size is too big, must be under "
					+ this.maxURLSize + " bytes.");
		}

		try {
			result = new URL(url);
		} catch (final MalformedURLException e) {
			throw new SpiderError(e);
		}
		return result;
	}

	/**
	 * @return The object that this spider reports progress to.
	 */
	public SpiderReportable getReport() {
		return this.report;
	}

	/**
	 * @return The ORM session manager for this spider.
	 */
	public SessionManager getSessionManager() {
		return this.manager;
	}

	/**
	 * The current HTTP timeout.
	 * @return The timeout value.
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * @return The browser string for this session.
	 */
	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * Obtain a workload for the spider.
	 * @return The workload that was just obtained.
	 */
	private WorkloadItem obtainWork() {

		boolean done = false;
		WorkloadItem result = null;

		while (!done) {
			final Query q = this.session
					.createQuery("From WorkloadItem Where status = :s");
			q.setCharacter("s", WorkloadStatus.QUEUED);
			q.setMaxResults(1);
			result = (WorkloadItem) q.uniqueResult();

			if (result == null) {
				final Query q2 = this.session
						.createQuery(
					"SELECT COUNT(*) FROM WorkloadItem Where status = :s");
				q2.setCharacter("s", WorkloadStatus.WORKING);
				final Long count = (Long) q2.uniqueResult();
				if (count > 0) {
					try {
						Thread.sleep(DEFAULT_TIMEOUT);
					} catch (final InterruptedException e) {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("Exception", e);
						}
					}
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}

		// mark it as working
		if (result != null) {
			result.setStatus(WorkloadStatus.WORKING);
			this.session.flush();
		}

		return result;
	}

	/**
	 * Process the specified URL.
	 * @param start The starting URL.
	 */
	public void process(final URL start) {
		WorkloadItem current = null;

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Spider is starting with URL: " + start);
		}

		this.session = this.manager.openSession();

		addURL(start, null);

		while ((current = obtainWork()) != null) {
			processWork(current);
			this.session.flush();
			this.session.clear();
		}

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Spider has stopped");
		}
	}

	/**
	 * Process a workload.
	 * @param current The workload to process.
	 */
	private void processWork(final WorkloadItem current) {
		final SpiderWorker worker = new SpiderWorker(this, current);
		EncogConcurrency.getInstance().processTask(worker);
	}

	/**
	 * St the HTTP timeout.
	 * @param timeout The timeout.
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Set the user agent. This what the spider sends to the websites
	 * to identify itself.
	 * @param userAgent The user agent.
	 */
	public void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

}
