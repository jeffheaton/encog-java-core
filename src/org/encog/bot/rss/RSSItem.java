/*
  * Encog Neural Network and Bot Library for Java v1.x
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
package org.encog.bot.rss;

import java.util.Date;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * RSSItem: This is the class that holds individual RSS items,
 * or stories, for the RSS class.
 */
public class RSSItem
{

  /*
   * The title of this item.
   */
  private String title;

  /*
   * The hyperlink to this item.
   */
  private String link;

  /*
   * The description of this item.
   */
  private String description;

  /*
   * The date this item was published.
   */
  private Date date;

  /**
   * Get the publication date.
   * @return The publication date.
   */
  public Date getDate()
  {
    return date;
  }

  /**
   * Set the publication date.
   * @param date The new publication date.
   */
  public void setDate(Date date)
  {
    this.date = date;
  }

  /**
   * Get the description.
   * @return The description.
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Get the description.
   * @param description The new description.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Get the hyperlink.
   * @return The hyperlink.
   */
  public String getLink()
  {
    return link;
  }

  /**
   * Set the hyperlink.
   * @param link The new hyperlink.
   */
  public void setLink(String link)
  {
    this.link = link;
  }

  /**
   * Get the item title.
   * @return The item title.
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * Set the item title.
   * @param title The new item title.
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * Load an item from the specified node.
   * @param node The Node to load the item from.
   */
  public void load(Node node)
  {
    NodeList nl = node.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++)
    {
      Node n = nl.item(i);
      String name = n.getNodeName();

      if (name.equalsIgnoreCase("title"))
        title = RSS.getXMLText(n);
      else if (name.equalsIgnoreCase("link"))
        link = RSS.getXMLText(n);
      else if (name.equalsIgnoreCase("description"))
        description = RSS.getXMLText(n);
      else if (name.equalsIgnoreCase("pubDate"))
      {
        String str = RSS.getXMLText(n);
        if (str != null)
          date = RSS.parseDate(str);
      }

    }
  }

  /**
   * Convert the object to a String.
   * @return The object as a String.
   */
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append('[');
    builder.append("title=\"");
    builder.append(title);
    builder.append("\",link=\"");
    builder.append(link);
    builder.append("\",date=\"");
    builder.append(date);
    builder.append("\"]");
    return builder.toString();
  }
}
