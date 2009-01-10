package org.encog.bot.spider.workload.data;

import org.encog.util.orm.DataObject;

public class WorkloadHost extends DataObject {

	private String host;
	
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
	
	

}
