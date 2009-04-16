/*
 * Encog Artificial Intelligence Framework v2.x
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
package org.encog.bot.browse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.FormElement;
import org.encog.bot.browse.range.Input;
import org.encog.bot.browse.range.Link;
import org.encog.util.http.FormUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class for web browsing.  This class allows you to navigate to a 
 * specific URL.  Once you navigate to one URL, you can naviage to any
 * URL contained on the page.
 * @author jheaton
 *
 */
public class Browser {

	protected WebPage currentPage;
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Navigate to a page based on a URL object. This will be an 
	 * HTTP GET operation.
	 * @param url The URL to navigate to.
	 */
	public void navigate(URL url)  {
		try
		{
			if( logger.isInfoEnabled())
			{
				logger.info("Navigating to page:" + url);
			}
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		navigate(url, is);
		is.close();
		}
		catch(IOException e)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Exception",e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate to a page and post the specified data.
	 * @param url The URL to post the data to.
	 * @param is The data to post to the page.
	 */
	public void navigate(URL url, InputStream is) {		
		try {
			if( logger.isInfoEnabled())
			{
				logger.info("POSTing to page:" + url);
			}
			LoadWebPage load = new LoadWebPage(url);
			currentPage = load.load(is);
		} catch (IOException e) {
			if(logger.isDebugEnabled())
			{
				logger.debug("Exception",e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate based on a string URL.
	 * @param url The URL to navigate to.
	 */
	public void navigate(String url)  {
		try
		{
		navigate(new URL(url));
		}
		catch(MalformedURLException e)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Exception",e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate based on a form.  Complete and post the form.
	 * @param form The form to be posted.
	 * @param submit The submit button on the form to simulate clicking.
	 */
	public void navigate(Form form, Input submit)  {
		
		try {
			
			if( logger.isInfoEnabled())
			{
				logger.info("Posting a form");
			}
			
		InputStream is;
		URLConnection connection = null;
		OutputStream os;
		URL targetURL;

		if (form.getMethod() == Form.Method.GET) {
			os = new ByteArrayOutputStream();
		} else {
			
				connection = form.getAction().getUrl().openConnection();
				os = connection.getOutputStream();
						
		}

		// add the parameters if present
		FormUtility formData = new FormUtility(os, null);
		for (DocumentRange dr : form.getElements()) {
			if (dr instanceof FormElement) {
				FormElement element = (FormElement) dr;
				if (element == submit || element.isAutoSend()) {
					String name = element.getName();
					String value = element.getValue();
					if (name != null) {
						if (value == null) {
							value = "";
						}
						formData.add(name, value);
					}
				}
			}
		}

		// now execute the command
		if (form.getMethod() == Form.Method.GET) {
			String action = form.getAction().getUrl().toString();
			os.close();
			action += "?";
			action += os.toString();
			targetURL = new URL(action);
			connection = targetURL.openConnection();
			is = connection.getInputStream();
		} else {
			targetURL = form.getAction().getUrl();
			os.close();
			is = connection.getInputStream();
		}

		navigate(targetURL, is);
		is.close();
		} catch (IOException e) {
			throw new BrowseError(e);
		}
	}

	public WebPage getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(WebPage currentPage) {
		this.currentPage = currentPage;
	}

	public void navigate(Link link)  {
		
			Address address = link.getTarget();
	
			if (address.getUrl() != null)
				navigate(address.getUrl());
			else
				navigate(address.getOriginal());


	}

	public void navigate(Form form)  {
		navigate(form, null);
	}

}
