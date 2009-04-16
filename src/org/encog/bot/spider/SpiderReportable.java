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
package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This interface defines a class that the spider can report
 * its findings to.
 */
public interface SpiderReportable {

	/**
	 * The types of link that can be encountered.
	 */
	public enum URLType {
		/**
		 * An HTML hyperlink.
		 */
		HYPERLINK,
		
		/**
		 * An HTML image.
		 */
		IMAGE, 
		
		/**
		 * An HTML Javascript block.
		 */
		SCRIPT, 
		
		/**
		 * An HTML style sheet.
		 */
		STYLE
	}


	/**
	 * Called when the spider is starting up. This method provides the
	 * SpiderReportable class with the spider object.
	 * 
	 * @param spider
	 *            The spider that will be working with this object.
	 */
	void init(Spider spider);

	/**
	 * Called when the spider encounters a URL.
	 * 
	 * @param url
	 *            The URL that the spider found.
	 * @param base
	 *            The base of the page that the URL was found on.
	 * @param type
	 *            The type of link this URL is.
	 * @return True if the spider should scan for links on this page.
	 */
	boolean spiderFoundURL(URL url, URL base, URLType type);

	/**
	 * Called when the spider is about to process a NON-HTML URL.
	 * 
	 * @param url
	 *            The URL that the spider found.
	 * @param stream
	 *            An InputStream to read the page contents from.
	 * @throws IOException
	 *             Thrown if an IO error occurs while processing the page.
	 */
	void spiderProcessURL(URL url, InputStream stream)
			throws IOException;

	/**
	 * Called when the spider is ready to process an HTML URL.
	 * 
	 * @param url
	 *            The URL that the spider is about to process.
	 * @param parse
	 *            An object that will allow you you to parse the HTML on this
	 *            page.
	 * @throws IOException
	 *             Thrown if an IO error occurs while processing the page.
	 */
	void spiderProcessURL(URL url, SpiderParseHTML parse)
			throws IOException;

	/**
	 * Called when the spider tries to process a URL but gets an error.
	 * 
	 * @param url
	 *            The URL that generated an error.
	 */
	void spiderURLError(URL url);

}