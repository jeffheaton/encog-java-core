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

import org.encog.bot.spider.workload.SpiderWorkload;
import org.encog.bot.spider.workload.WorkloadItem;
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
	
	private final SpiderWorkload workload;

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
		this.workload = new SpiderWorkload(manager);
		this.report = report;
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
	 * Process the specified URL.
	 * @param start The starting URL.
	 */
	public void process(final URL start) {
		WorkloadItem current = null;

		if (this.logger.isInfoEnabled()) {
			this.logger.info("Spider is starting with URL: " + start);
		}

		this.workload.addURL(start, null);

		while ((current = this.workload.obtainWork()) != null) {
			processWork(current);
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

	public SpiderWorkload getWorkload() {
		return workload;
	}
	
	


}
