package org.encog.cloud;

import java.util.HashMap;
import java.util.Map;

public class EncogCloud {
	
	public final String DEFAULT_SERVER = "http://cloud.encog.com/";
	private String session;
	private String server;
	
	public String constructService(String service)
	{
		return this.server + service;
	}
	
	public EncogCloud(String server)
	{
		this.server = server;
		if( !this.server.endsWith("/"))
			this.server+='/';		
	}
	
	public CloudTask beginTask(String name)
	{
		CloudTask result = new CloudTask(this);
		result.init(name);
		
		return result;
	}
	
	public void connect(String uid, String pwd)
	{
		CloudRequest request = new CloudRequest();
		Map<String,String> args = new HashMap<String,String>();
		args.put("uid", uid);
		args.put("pwd", pwd);
		request.performURLPOST(false, constructService("login"), args);
		if( !"success".equals(request.getStatus()))
		{
			throw new EncogCloudError(request.getMessage());
		}
		this.session = request.getSession();
	}
	
	public void logout()
	{
		CloudRequest request = new CloudRequest();
		request.performURLGET(false, this.session + "logout");
		this.session = null;
	}
	
	public void validateSession(boolean failOnError)
	{
		int max;
		
		if( failOnError )
			max = 1;
		else
			max = 5;
		
		for(int i=0;i<max;i++) {
			CloudRequest request = new CloudRequest();
			request.performURLGET(false, this.session);
			if( "success".equals(request.getStatus()))
				return;
		}
		
		if( failOnError )
		{
			throw new EncogCloudError("Connection lost");
		}
	}
	
	public boolean isConnected()
	{
		return session!=null;
	}
	
	public String getSession()
	{
		return session;
	}
}
