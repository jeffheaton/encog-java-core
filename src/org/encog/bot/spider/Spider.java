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
 * pages as well.  All data found is reported to the SpiderReportable interface.
 * 
 * The queue of pages to access must be stored in a database.  This database
 * is accessed using the Hibernate ORM.  For shorter spidering tasks an in-memory
 * database can be used such as HSQL in Java.
 * 
 * Spiders must typically wait for the pages that they are accessing to load.
 * Because if this it is very advantageous to use a spider in a multithreaded
 * way.   To do this the spider uses the Encog threading framework, which 
 * in turn makes use of whatever underlying thread pool is provided by either
 * Java or C#.  For more information about multithreading, refer to the
 * EncogConcurrency class.
 * 
 * @author jheaton
 *
 */
public class Spider {

	private ORMSession session;
	private SessionManager manager;
	private SpiderReportable report;
	private int timeout = 1000;
	private String userAgent = "Mozilla/5.0";
	private int maxURLSize = 255;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Spider(SessionManager manager, SpiderReportable report) {
		this.manager = manager;
		this.report = report;
	}

	private WorkloadItem obtainWork() {
		
		boolean done = false;
		WorkloadItem result = null;
		
		while(!done)
		{
			Query q = this.session.createQuery("From WorkloadItem Where status = :s");
			q.setCharacter("s", WorkloadStatus.QUEUED);
			q.setMaxResults(1);
			result = (WorkloadItem) q.uniqueResult();
			
			if( result==null )
			{
				Query q2 = this.session.createQuery("SELECT COUNT(*) FROM WorkloadItem Where status = :s");
				q2.setCharacter("s", WorkloadStatus.WORKING);
				Long count = (Long)q2.uniqueResult();
				if( count>0 )
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						if(logger.isDebugEnabled())
						{
							logger.debug("Exception",e);
						}
					}
				}
				else
					done = true;
			}
			else
				done = true;			
		}
		
		// mark it as working
		if( result!=null)
		{
			result.setStatus(WorkloadStatus.WORKING);
			this.session.flush();
		}
		
		return result;
	}

	private void processWork(WorkloadItem current) {
		SpiderWorker worker = new SpiderWorker(this, current);
		EncogConcurrency.getInstance().processTask(worker);
	}

	public SessionManager getSessionManager() {
		return this.manager;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public SpiderReportable getReport() {
		return report;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public void process(URL start) {
		WorkloadItem current = null;

		if( logger.isInfoEnabled())
		{
			logger.info("Spider is starting with URL: " + start);
		}
		
		this.session = this.manager.openSession();

		addURL(start, null);

		while ((current = obtainWork()) != null) {
			processWork(current);
			session.flush();
			session.clear();
		}
		
		if( logger.isInfoEnabled())
		{
			logger.info("Spider has stopped");
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
		if (this.maxURLSize != -1 && url.length() > this.maxURLSize) {
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

	public void addURL(URL url, WorkloadItem source) {

		if(logger.isDebugEnabled())
		{
			logger.debug("Adding URL to spider queue: " + url);
		}
		
		// does the URL exist already?
		Query q = this.session
				.createQuery("SELECT count(*) FROM WorkloadItem WHERE url = :u");
		q.setString("u", url.toString());
		Long i = (Long) q.uniqueResult();
		if (i == 0) {
			// add a new URL
			WorkloadItem item = new WorkloadItem();
			item.setHost(url.getHost());
			item.setUrl(url.toString());
			item.setStatus(WorkloadStatus.QUEUED);
			item.setSource(source);
			if (source == null)
				item.setDepth(0);
			else
				item.setDepth(source.getDepth() + 1);
			this.session.save(item);
		}
	}

}
