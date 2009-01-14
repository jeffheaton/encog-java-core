package org.encog.bot.spider;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;

@Entity
@Table(name="spider_workload",
    uniqueConstraints = {@UniqueConstraint(columnNames={"url"})} )
public class WorkloadItem extends DataObject {
	private String url;
	private String host;
	private char status;
	private int depth;
	private WorkloadItem source;
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}
	
	

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	

	public WorkloadItem getSource() {
		return source;
	}

	public void setSource(WorkloadItem source) {
		this.source = source;
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append('[');
		result.append(this.status);
		result.append(',');
		result.append(this.url);
		result.append(']');
		return result.toString();
	}
}
