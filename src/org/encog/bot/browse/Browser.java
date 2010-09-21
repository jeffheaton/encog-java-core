/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
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
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @return The page currently being browsed.
	 */
	public WebPage getCurrentPage() {
		return this.currentPage;
	}

	/**
	 * Navigate to the specified form by performing a submit of that form.
	 * 
	 * @param form
	 *            The form to be submitted.
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
	 * 
	 * @param link
	 *            The link to navigate to.
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
	 * 
	 * @param currentPage
	 *            The current page.
	 */
	public void setCurrentPage(final WebPage currentPage) {
		this.currentPage = currentPage;
	}

}
