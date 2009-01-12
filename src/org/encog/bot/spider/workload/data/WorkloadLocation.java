package org.encog.bot.spider.workload.data;

import javax.persistence.Entity;

import org.encog.util.orm.DataObject;

@Entity(name = "workload_location")
public class WorkloadLocation extends DataObject {

	private String url;
	private WorkloadHost host;
	private long hash;
	
	/**
	 * The depth of this URL.
	 */
	private int depth;

	/**
	 * The source of this URL.
	 */
	private String source;
	
	private WorkloadStatus status;

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
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
	public void setSource(final String source) {
		this.source = source;
	}	
	
	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public WorkloadStatus getStatus() {
		return status;
	}

	public void setStatus(WorkloadStatus status) {
		this.status = status;
	}

	public WorkloadHost getHost() {
		return host;
	}

	public void setHost(WorkloadHost host) {
		this.host = host;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}
	
	

}
