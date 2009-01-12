package org.encog.bot.spider.workload.data;

import javax.persistence.Entity;

import org.encog.util.orm.DataObject;

@Entity(name = "workload_host")
public class WorkloadHost extends DataObject {

	private String host;
	private WorkloadStatus status;
	
	@Override
	public void validate() {
		// TODO Auto-generated method stub		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public WorkloadStatus getStatus() {
		return status;
	}

	public void setStatus(WorkloadStatus status) {
		this.status = status;
	}
	
}
