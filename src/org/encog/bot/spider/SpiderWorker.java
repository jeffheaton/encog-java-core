package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.encog.util.concurrency.EncogTask;
import org.encog.util.orm.ORMSession;

public class SpiderWorker implements EncogTask {

	private Spider owner;
	private ORMSession session;
	private WorkloadItem work;
		
	public SpiderWorker(Spider owner, WorkloadItem work)
	{
		this.owner = owner;
		this.work = work;
	}
	
	@Override
	public void run() {
		
		URLConnection connection = null;
		InputStream is = null;
		
		this.session = this.owner.getSessionManager().openSession();
		
		try {
			// get the URL's contents
			
			URL url = new URL(this.work.getUrl());
			connection = url.openConnection();
			
			connection.setConnectTimeout(this.owner.getTimeout());
			connection.setReadTimeout(this.owner.getTimeout());
			if (this.owner.getUserAgent() != null) {
				connection.setRequestProperty("User-Agent", this.owner
						.getUserAgent());
			}

			// read the URL
			is = connection.getInputStream();

			// parse the URL
			final String contentType = connection.getContentType();
			if (contentType.toLowerCase().startsWith("text/html")) {
				final SpiderParseHTML parse = new SpiderParseHTML(
					this.work, 
					new SpiderInputStream(is, null), 
					this.owner);
				this.owner.getReport().spiderProcessURL(url, parse);
			} else {
				this.owner.getReport().spiderProcessURL(url, is);
			}
			
			this.work.setStatus(WorkloadStatus.PROCESSED);
			
		} catch (MalformedURLException e) {
			this.work.setStatus(WorkloadStatus.ERROR);
		} catch (IOException e) {
			this.work.setStatus(WorkloadStatus.ERROR);
		}
		
		this.session.close();		
	}
	
}
