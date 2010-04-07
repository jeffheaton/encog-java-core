package org.encog.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudTask {
	
	private EncogCloud cloud;
	private String taskURL;
	
	public CloudTask(EncogCloud cloud)
	{
		this.cloud = cloud;
	}

	public EncogCloud getCloud() {
		return cloud;
	}
	
	public void setStatus(String status)
	{
		if( this.taskURL==null )
		{
			throw new EncogCloudError("Can't set status for inactive task.");
		}
		
		CloudRequest request = new CloudRequest();
		String url = this.taskURL + "update";
		
		Map<String,String> args = new HashMap<String,String>();
		args.put("status", status);	
		request.post(url, args);		
		
		
	}
	
	public void stop()
	{
		if( this.taskURL==null )
		{
			throw new EncogCloudError("Can't stop inactive task.");
		}
		
		String url = this.taskURL + "stop";
		CloudRequest request = new CloudRequest();
		request.get(url);
	}

	public void init(String name) {
		
		if( cloud.getSession()==null )
		{
			throw new EncogCloudError("Session must be established before a task is created.");
		}
		
		CloudRequest request = new CloudRequest();
		String url = this.cloud.getSession();
		url+="task/create";
		Map<String,String> args = new HashMap<String,String>();
		args.put("name", name);
		args.put("status", "Starting...");	
		request.post(url, args);		
		this.taskURL = cloud.getSession() +  "task/" +
			request.getResponseProperty("id") + "/";
		
	}
	
}
