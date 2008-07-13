/*
  * Encog Neural Network and Bot Library for Java v0.5
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
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

import java.io.*;
import java.net.*;
import java.util.logging.*;

import org.encog.bot.html.HTMLTag;
import org.encog.bot.html.ParseHTML;
import org.encog.bot.html.URLUtility;
import org.encog.bot.spider.workload.WorkloadException;



/**
 * SpiderParseHTML: This class layers on top of the
 * ParseHTML class and allows the spider to extract what
 * link information it needs. A SpiderParseHTML class can be
 * used just like the ParseHTML class, with the spider
 * gaining its information in the background.
 */
public class SpiderParseHTML extends ParseHTML {
  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger("com.heatonresearch.httprecipes.spider.SpiderParseHTML");

  /**
   * The Spider that this page is being parsed for.
   */
  private Spider spider;

  /**
   * The URL that is being parsed.
   */
  private URL base;

  /**
   * The depth of the page being parsed.
   */
  private int depth;

  /**
   * The InputStream that is being parsed.
   */
  private SpiderInputStream stream;

  /**
   * Construct a SpiderParseHTML object. This object allows
   * you to parse HTML, while the spider collects link
   * information in the background.
   * 
   * @param base
   *          The URL that is being parsed, this is used for
   *          relative links.
   * @param is
   *          The InputStream being parsed.
   * @param spider
   *          The Spider that is parsing.
   * @throws WorkloadException
   *           An error occurred with the workload
   *           management.
   */
  public SpiderParseHTML(URL base, SpiderInputStream is, Spider spider)
      throws WorkloadException {
    super(is);
    this.stream = is;
    this.spider = spider;
    this.base = base;
    this.depth = spider.getWorkloadManager().getDepth(base);
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
   * Read a single character. This function will process any
   * tags that the spider needs for navigation, then pass
   * the character on to the caller. This allows the spider
   * to transparently gather its links.
   * 
   * @return The character read.
   * @throws IOException
   *           I/O error.
   */
  @Override
  public int read() throws IOException {
    int result = super.read();
    if (result == 0) {
      HTMLTag tag = getTag();
      if (tag.getName().equalsIgnoreCase("a")) {
        String href = tag.getAttributeValue("href");
        handleA(href);
      } else if (tag.getName().equalsIgnoreCase("img")) {
        String src = tag.getAttributeValue("src");
        addURL(src, SpiderReportable.URLType.IMAGE);
      } else if (tag.getName().equalsIgnoreCase("style")) {
        String src = tag.getAttributeValue("src");
        addURL(src, SpiderReportable.URLType.STYLE);
      } else if (tag.getName().equalsIgnoreCase("link")) {
        String href = tag.getAttributeValue("href");
        addURL(href, SpiderReportable.URLType.SCRIPT);
      } else if (tag.getName().equalsIgnoreCase("base")) {
        String href = tag.getAttributeValue("href");
        this.base = new URL(this.base, href);
      }

    }
    return result;
  }

  /**
   * Read all characters on the page. This will discard
   * these characters, but allow the spider to examine the
   * tags and find links.
   * 
   * @throws IOException
   *           I/O error.
   */
  public void readAll() throws IOException {
    while (read() != -1) {
      ;
    }
  }

  /**
   * Used internally, to add a URL to the spider's workload.
   * 
   * @param u
   *          The URL to add.
   * @param type
   *          What type of link this is.
   * @throws IOException
   *           Thrown if an I/O error occurs.
   */
  private void addURL(String u, SpiderReportable.URLType type)
      throws IOException {
    if (u == null) {
      return;
    }

    try {
      URL url = URLUtility.constructURL(this.base, u, true);
      url = this.spider.getWorkloadManager().convertURL(url.toString());

      if (url.getProtocol().equalsIgnoreCase("http")
          || url.getProtocol().equalsIgnoreCase("https")) {
        if (this.spider.getReport().spiderFoundURL(url, this.base, type)) {
          try {
            this.spider.addURL(url, this.base, this.depth + 1);
          } catch (WorkloadException e) {
            throw new IOException(e.getMessage());
          }
        }
      }
    }

    catch (MalformedURLException e) {
      logger.log(Level.INFO, "Malformed URL found:" + u);
    } catch (WorkloadException e) {
      logger.log(Level.INFO, "Invalid URL found:" + u);
    }
  }

  /**
   * This method is called when an anchor(A) tag is found.
   * 
   * @param href
   *          The link found.
   * @throws IOException
   *           I/O error.
   */
  private void handleA(String href) throws IOException {

    if (href != null) {
      href = href.trim();
    }

    if ((href != null) && !URLUtility.containsInvalidURLCharacters(href)) {
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
}
