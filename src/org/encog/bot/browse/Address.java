package org.encog.bot.browse;

import java.net.MalformedURLException;
import java.net.URL;

public class Address {
	private String original;
	private URL url;
	
	public Address(URL base,String original)
	{
		this.original = original;
		try
		{
			url = new URL(base,original);
		}
		catch(MalformedURLException e)
		{
			// not important, original already set
		}
	}
	
	public Address(URL u)
	{
		this.url = u;
		this.original = u.toString();
	}
	
	public String getOriginal() {
		return original;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public String toString()
	{
		if( url!=null )
			return url.toString();
		else
			return original;
	}
	
	
}
