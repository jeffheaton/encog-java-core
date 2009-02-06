/*
 * Encog Artificial Intelligence Framework v1.x
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
