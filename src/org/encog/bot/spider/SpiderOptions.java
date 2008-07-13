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
import java.lang.reflect.*;
import java.util.*;

/**
 * SpiderOptions: This class contains options for the
 * spider's execution.
 */
public class SpiderOptions {
  /**
   * Specifies that when the spider starts up it should
   * clear the workload.
   */
  public static final String STARTUP_CLEAR = "CLEAR";

  /**
   * Specifies that the spider should resume processing its
   * workload.
   */
  public static final String STARTUP_RESUME = "RESUME";

  /**
   * How many milliseconds to wait when downloading pages.
   */
  public int timeout = 60000;

  /**
   * The maximum depth to search pages. -1 specifies no
   * maximum depth.
   */
  public int maxDepth = -1;

  /**
   * What user agent should be reported to the web site.
   * This allows the web site to determine what browser is
   * being used.
   */
  public String userAgent = null;

  /**
   * The core thread pool size.
   */
  public int corePoolSize = 100;

  /**
   * The maximum thread pool size.
   */
  public int maximumPoolSize = 100;

  /**
   * How long to keep inactive threads alive. Measured in
   * seconds.
   */
  public long keepAliveTime = 60;

  /**
   * The URL to use for JDBC databases. Used to hold the
   * workload.
   */
  public String dbURL;

  /**
   * The user id to use for JDBC databases. Used to hold the
   * workload.
   */
  public String dbUID;

  /**
   * The password to use for JDBC databases. Used to hold
   * the workload.
   */
  public String dbPWD;

  /**
   * The class to use for JDBC connections. Used to hold the
   * workload.
   */
  public String dbClass;

  /**
   * What class to use as a workload manager.
   */
  public String workloadManager;

  /**
   * What to do when the spider starts up. This specifies if
   * the workload should be cleared, or resumed.
   */
  public String startup = STARTUP_CLEAR;

  /*
   * Specifies a class to be used a filter.§
   */
  public List<String> filter = new ArrayList<String>();

  /**
   * Load the spider settings from a configuration file.
   * 
   * @param inputFile
   *          The name of the configuration file.
   * @throws IOException
   *           Thrown if an I/O error occurs.
   * @throws SpiderException
   *           Thrown if there is an error mapping
   *           configuration items between the file and this
   *           object.
   */
  public void load(String inputFile) throws IOException, SpiderException {
    FileReader f = new FileReader(new File(inputFile));
    BufferedReader r = new BufferedReader(f);
    String line;
    while ((line = r.readLine()) != null) {
      try {
        parseLine(line);
      } catch (IllegalArgumentException e) {
        throw (new SpiderException(e));
      } catch (SecurityException e) {
        throw (new SpiderException(e));
      } catch (IllegalAccessException e) {
        throw (new SpiderException(e));
      } catch (NoSuchFieldException e) {
        throw (new SpiderException(e));
      }
    }
    r.close();
    f.close();
  }

  /**
   * Process each line of a configuration file.
   * 
   * @param line
   *          The line of text read from the configuration
   *          file.
   * @throws IllegalArgumentException
   *           Thrown if an invalid argument is specified.
   * @throws SecurityException
   *           Thrown if a security exception occurs.
   * @throws IllegalAccessException
   *           Thrown if a field cannot be accessed.
   * @throws NoSuchFieldException
   *           Thrown if an invalid field is specified.
   */
  @SuppressWarnings("unchecked")
  private void parseLine(String line) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    String name, value;
    int i = line.indexOf(':');
    if (i == -1) {
      return;
    }
    name = line.substring(0, i).trim();
    value = line.substring(i + 1).trim();

    if (value.trim().length() == 0) {
      value = null;
    }

    Field field = this.getClass().getField(name);
    if (field.getType() == String.class) {
      field.set(this, value);
    } else if (field.getType() == List.class) {
      List<String> list = (List<String>) field.get(this);
      list.add(value);
    } else {
      int x = Integer.parseInt(value);
      field.set(this, x);
    }
  }
}
