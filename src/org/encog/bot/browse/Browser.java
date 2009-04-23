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
 * The main class for web browsing. This class allows you to navigate to a
 * specific URL. Once you navigate to one URL, you can naviage to any URL
 * contained on the page.
 * 
 * @author jheaton
 * 
 */
public class Browser {

	/**
	 * The page that is currently being browsed.
	 */
	private WebPage currentPage;

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @return The page currently being browsed.
	 */
	public WebPage getCurrentPage() {
		return this.currentPage;
	}

	/**
	 * Navigate to the specified form by performing a submit of that form.
	 * @param form The form to be submitted.
	 */
	public void navigate(final Form form) {
		navigate(form, null);
	}

	/**
	 * Navigate based on a form. Complete and post the form.
	 * 
	 * @param form
	 *            The form to be posted.
	 * @param submit
	 *            The submit button on the form to simulate clicking.
	 */
	public void navigate(final Form form, final Input submit) {

		try {

			if (this.logger.isInfoEnabled()) {
				this.logger.info("Posting a form");
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
			final FormUtility formData = new FormUtility(os, null);
			for (final DocumentRange dr : form.getElements()) {
				if (dr instanceof FormElement) {
					final FormElement element = (FormElement) dr;
					if ((element == submit) || element.isAutoSend()) {
						final String name = element.getName();
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
		} catch (final IOException e) {
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate to a new page based on a link.
	 * @param link The link to navigate to.
	 */
	public void navigate(final Link link) {

		final Address address = link.getTarget();

		if (address.getUrl() != null) {
			navigate(address.getUrl());
		} else {
			navigate(address.getOriginal());
		}

	}

	/**
	 * Navigate based on a string URL.
	 * 
	 * @param url
	 *            The URL to navigate to.
	 */
	public void navigate(final String url) {
		try {
			navigate(new URL(url));
		} catch (final MalformedURLException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Exception", e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate to a page based on a URL object. This will be an HTTP GET
	 * operation.
	 * 
	 * @param url
	 *            The URL to navigate to.
	 */
	public void navigate(final URL url) {
		try {
			if (this.logger.isInfoEnabled()) {
				this.logger.info("Navigating to page:" + url);
			}
			final URLConnection connection = url.openConnection();
			final InputStream is = connection.getInputStream();
			navigate(url, is);
			is.close();
		} catch (final IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Exception", e);
			}
			throw new BrowseError(e);
		}
	}

	/**
	 * Navigate to a page and post the specified data.
	 * 
	 * @param url
	 *            The URL to post the data to.
	 * @param is
	 *            The data to post to the page.
	 */
	public void navigate(final URL url, final InputStream is) {		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("POSTing to page:" + url);
		}
		final LoadWebPage load = new LoadWebPage(url);
		this.currentPage = load.load(is);
	}

	/**
	 * Set the current page.
	 * @param currentPage The current page.
	 */
	public void setCurrentPage(final WebPage currentPage) {
		this.currentPage = currentPage;
	}

}