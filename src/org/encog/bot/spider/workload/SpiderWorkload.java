package org.encog.bot.spider.workload;

import java.net.URL;

import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiderWorkload {
	
	private final int DEFAULT_TIMEOUT = 1000;
	
	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * The ORM manager to use for storing the workload.
	 */
	private final SessionManager manager;
	
	
	public SpiderWorkload(SessionManager manager)
	{
		this.manager = manager;
	}
	
	private synchronized WorkloadItem queryWorkload()
	{
		final ORMSession session = this.manager.openSession();
		final Query q = session.createQuery("From WorkloadItem Where status = :s");
		q.setCharacter("s", WorkloadStatus.QUEUED);
		q.setMaxResults(1);
		WorkloadItem result = (WorkloadItem) q.uniqueResult();
		session.flush();
		session.close();
		return result;
	}
	
	private long countEligibleWorkloadItems()
	{
		final ORMSession session = this.manager.openSession();
		final Query q = session.createQuery(
			"SELECT count(*) FROM WorkloadItem WHERE status = :s1 or status= :s2");
		q.setString("s1", ""+WorkloadStatus.QUEUED);
		q.setString("s2", ""+WorkloadStatus.WORKING);
		final Long i = (Long) q.uniqueResult();
		session.close();
		return i.longValue();
	}
	
	private void pause()
	{
		try {
			Thread.sleep(DEFAULT_TIMEOUT);
		} catch (final InterruptedException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Exception", e);
			}
		}
	}
	
	/**
	 * Obtain a workload for the spider.
	 * @return The workload that was just obtained.
	 */
	public WorkloadItem obtainWork() {

		boolean done = false;
		WorkloadItem result = null;

		// wait for work
		while (!done) {
			result = queryWorkload();

			if (result == null) {	
				if( this.countEligibleWorkloadItems()<1)
					return null;// we are done
				pause();
			} else {
				done = true;
			}
		}
		
		// update status
		if (result != null) {
			updateStatus(result,WorkloadStatus.WORKING);
		}
		
		

		return result;
	}
	

	public synchronized void updateStatus(WorkloadItem item,char status) {
		final ORMSession session = this.manager.openSession();
			
		String hql = "update WorkloadItem set status = :s where id = :i";
        Query query = session.createQuery(hql);
        query.setString("s",""+status);
        query.setLong("i",item.getId());
        query.executeUpdate();
        session.flush();
        session.close();
        System.out.println("Status Update("+status+"): " + item.getUrl());
	}
	
	private boolean urlExists(ORMSession session, URL url)
	{
		final Query q = session.createQuery(
			"SELECT count(*) FROM WorkloadItem WHERE url = :u");
		q.setString("u", url.toString());
		final Long i = (Long) q.uniqueResult();
		return (i != 0);
	}
	
	/**
	 * Add a URL to the spider for processing.
	 * @param url The URL to add.
	 * @param source The source the URL came from.
	 */
	public synchronized void addURL(final URL url, final WorkloadItem source) {

		
		final ORMSession session = this.manager.openSession();
		
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Adding URL to spider queue: " + url);
		}

		if( !urlExists(session, url) )
		{
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
			session.save(item);
			System.out.println("Add:" + url);
		}
		
		session.flush();
		session.clear();
	}

}
