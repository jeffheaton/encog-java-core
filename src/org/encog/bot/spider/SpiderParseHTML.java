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
import java.net.MalformedURLException;
import java.net.URL;

import org.encog.parse.tags.Tag;
import org.encog.parse.tags.read.ReadHTML;
import org.encog.util.http.URLUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpiderParseHTML: This class layers on top of the ParseHTML class and allows
 * the spider to extract what link information it needs. A SpiderParseHTML class
 * can be used just like the ParseHTML class, with the spider gaining its
 * information in the background.
 */
public class SpiderParseHTML extends ReadHTML {
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Spider that this page is being parsed for.
	 */
	private final Spider spider;

	/**
	 * The URL that is being parsed.
	 */
	private URL base;
	
	private WorkloadItem source;

	/**
	 * The InputStream that is being parsed.
	 */
	private final SpiderInputStream stream;

	/**
	 * Construct a SpiderParseHTML object. This object allows you to parse HTML,
	 * while the spider collects link information in the background.
	 * 
	 * @param base
	 *            The URL that is being parsed, this is used for relative links.
	 * @param is
	 *            The InputStream being parsed.
	 * @param spider
	 *            The Spider that is parsing.
	 */
	public SpiderParseHTML(WorkloadItem source,
			final SpiderInputStream is,
			final Spider spider) {
		super(is);
		this.stream = is;
		this.spider = spider;		
		this.source = source;
		
		try {
			this.base = new URL(source.getUrl());
		} catch (MalformedURLException e) {
			if( logger.isDebugEnabled())
			{
				logger.debug("Exception",e);
			}
			throw new SpiderError(e);
		}
	}

	/**
	 * Used internally, to add a URL to the spider's workload.
	 * 
	 * @param u
	 *            The URL to add.
	 * @param type
	 *            What type of link this is.
	 */
	private void addURL(final String u, final SpiderReportable.URLType type)
			 {
		if (u == null) {
			return;
		}

		try {
			URL url = URLUtility.constructURL(this.base, u, true);
			url = this.spider.convertURL(url.toString());

			if (url.getProtocol().equalsIgnoreCase("http")
					|| url.getProtocol().equalsIgnoreCase("https")) {
				if (this.spider.getReport()
						.spiderFoundURL(url, this.base, type)) {
					
						this.spider.addURL(url, source);
					
				}
			}
		}

		catch (final MalformedURLException e) {
			if( logger.isDebugEnabled() )
			{
				if( logger.isDebugEnabled())
				{
					logger.debug("Malformed URL found",e);
				}
				throw new SpiderError(e);
			}
			
		} catch (final SpiderError e) {
			if( logger.isDebugEnabled() )
			{
				if( logger.isDebugEnabled())
				{
					logger.debug("Invalid URL found:",e);
				}
				throw new SpiderError(e);
			}
		} catch (IOException e) {
			if( logger.isDebugEnabled())
			{
				logger.debug("Invalid URL found:",e);
			}
			throw new SpiderError(e);
		}
	}

	/**
	 * Get the InputStream being parsed.
	 * 
	 * @return The InputStream being parsed.
	 */
	public SpiderInputStream getStream() {
		return this.stream;
	}

	/**
	 * This method is called when an anchor(A) tag is found.
	 * 
	 * @param ahref
	 *            The link found.
	 */
	private void handleA(final String ahref)  {
		String href = ahref;

		if (href != null) {
			href = href.trim();
		}

		if (href != null && !URLUtility.containsInvalidURLCharacters(href)) {
			if (!href.toLowerCase().startsWith("javascript:")
					&& !href.toLowerCase().startsWith("rstp:")
					&& !href.toLowerCase().startsWith("rtsp:")
					&& !href.toLowerCase().startsWith("news:")
					&& !href.toLowerCase().startsWith("irc:")
					&& !href.toLowerCase().startsWith("mailto:")) {
				addURL(href, SpiderReportable.URLType.HYPERLINK);
			}
		}
	}

	/**
	 * Read a single character. This function will process any tags that the
	 * spider needs for navigation, then pass the character on to the caller.
	 * This allows the spider to transparently gather its links.
	 * 
	 * @return The character read.
	 */
	@Override
	public int read()  {
		final int result = super.read();
		if (result == 0) {
			final Tag tag = getTag();
			if (tag.getName().equalsIgnoreCase("a")) {
				final String href = tag.getAttributeValue("href");
				handleA(href);
			} else if (tag.getName().equalsIgnoreCase("img")) {
				final String src = tag.getAttributeValue("src");
				addURL(src, SpiderReportable.URLType.IMAGE);
			} else if (tag.getName().equalsIgnoreCase("style")) {
				final String src = tag.getAttributeValue("src");
				addURL(src, SpiderReportable.URLType.STYLE);
			} else if (tag.getName().equalsIgnoreCase("link")) {
				final String href = tag.getAttributeValue("href");
				addURL(href, SpiderReportable.URLType.SCRIPT);
			} else if (tag.getName().equalsIgnoreCase("base")) {
				final String href = tag.getAttributeValue("href");
				try {
					this.base = new URL(this.base, href);
				} catch (MalformedURLException e) {
					if( logger.isDebugEnabled())
					{
						logger.debug("Invalid URL found:",e);
					}
					throw new SpiderError(e);
				}
			}

		}
		return result;
	}

	/**
	 * Read all characters on the page. This will discard these characters, but
	 * allow the spider to examine the tags and find links.
	 * 
	 */
	public void readAll() {
		int i = 0;
		do {
			i = read();
		} while (i != -1);

	}
}
