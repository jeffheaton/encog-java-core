package org.encog.bot.spider.workload.data;

import java.net.URL;

import org.encog.util.orm.DataObject;

public class WorkloadLocation extends DataObject {

	private URL url;
	
	/**
	 * The depth of this URL.
	 */
	private int depth;

	/**
	 * The source of this URL.
	 */
	private URL source;

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * @return the source
	 */
	public URL getSource() {
		return this.source;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setDepth(final int depth) {
		this.depth = depth;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final URL source) {
		this.source = source;
	}	
	
	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
	
	

}
