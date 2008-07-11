/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.bot.html;

import java.io.*;
import java.net.*;

/**
 * The Heaton Research Spider Copyright 2007 by Heaton
 * Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * URLUtility: A set of useful utilities for processing
 * URL's.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class URLUtility
{
  public static final String indexFile = "index.html";

  /**
   * Construct a URL from its basic parts.
   * @param base The base URL.
   * @param url The URL that was found on the base URL's page.
   * @param stripRef True if the URL's reference should be stripped.
   * @return The new URL, built upon the base URL.
   * @throws IOException Thrown if any IO error occurs.
   */
  public static URL constructURL(URL base, String url, boolean stripRef)
      throws IOException
  {
    URL result = new URL(base, url);
    String file = result.getFile();
    String protocol = result.getProtocol();
    String host = result.getHost();
    int port = result.getPort();
    String ref = result.getRef();
    StringBuilder sb = new StringBuilder(file);
    int index = sb.indexOf(" ");
    while (index != -1)
    {
      if (index != -1)
      {
        sb.replace(index, index + 1, "%20");
      }
      index = sb.indexOf(" ");
    }

    file = sb.toString();
    if ((ref != null) && !stripRef)
    {
      result = new URL(protocol, host, port, file + "#" + ref);
    } else
    {
      result = new URL(protocol, host, port, file);
    }
    return result;
  }

  /**
   * Returns true if the URL contains any invalid characters.
   * @param url The URL to be checked.
   * @return True if the URL contains invalid characters.
   */
  public static boolean containsInvalidURLCharacters(String url)
  {
    for (int i = 0; i < url.length(); i++)
    {
      char ch = url.charAt(i);
      if (ch > 255)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Convert a filename for local storage. Also create the
   * directory tree.
   * 
   * @param base
   *          The local path that forms the base of the
   *          downloaded web tree.
   * @param path
   *          The URL path.
   * @return The resulting local path to store to.
   */
  public static String convertFilename(String base, URL url, boolean mkdir)
  {
    StringBuilder result = new StringBuilder(base);
    int index1;
    int index2;

    // append the host name
    StringBuilder path = new StringBuilder(url.getHost().replace('.', '_'));
    if (url.getFile().length() == 0)
      path.append('/');
    else
      path.append(url.getFile());

    // add ending slash if needed
    if (result.charAt(result.length() - 1) != File.separatorChar)
    {
      result.append(File.separator);
    }

    // see if an ending / is missing from a directory only

    int lastSlash = path.lastIndexOf("" + File.separatorChar);
    int lastDot = path.lastIndexOf(".");

    if (path.charAt(path.length() - 1) != '/')
    {
      if (lastSlash > lastDot)
      {
        path.append(File.pathSeparatorChar);
        path.append(indexFile);
      }
    }

    // determine actual filename
    lastSlash = path.lastIndexOf("/");

    String filename = "";
    if (lastSlash != -1)
    {
      filename = path.substring(1 + lastSlash);
      path = path.replace(1 + lastSlash, path.length(), "");

      if (filename.equals(""))
      {
        filename = indexFile;
      }
    }
    // create the directory structure, if needed

    index1 = 0;
    do
    {
      index2 = path.indexOf("/", index1);

      if (index2 != -1)
      {
        String dirpart = path.substring(index1, index2);
        result.append(dirpart);
        result.append(File.separator);

        if (mkdir)
        {
          File f = new File(result.toString());
          f.mkdir();
        }

        index1 = index2 + 1;

      }
    } while (index2 != -1);

    // attach name
    result.append(filename.replace('?', '_'));

    return result.toString();
  }
}
