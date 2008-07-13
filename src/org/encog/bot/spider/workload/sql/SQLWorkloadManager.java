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
package org.encog.bot.spider.workload.sql;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.encog.bot.spider.Spider;
import org.encog.bot.spider.workload.WorkloadException;
import org.encog.bot.spider.workload.WorkloadManager;


/**
 * SQLWorkloadManager: This workload manager stores the URL
 * lists in an SQL database. This workload manager uses two
 * tables, which can be created as follows:
 *
 * CREATE TABLE 'spider_host' (
 * 'host_id' int(10) unsigned NOT NULL auto_increment,
 * 'host' varchar(255) NOT NULL default '',
 * 'status' varchar(1) NOT NULL default '',
 * 'urls_done' int(11) NOT NULL,
 * 'urls_error' int(11) NOT NULL,
 * PRIMARY KEY  ('host_id')
 * )
 *
 * CREATE TABLE 'spider_workload' (
 * 'workload_id' int(10) unsigned NOT NULL auto_increment,
 * 'host' int(10) unsigned NOT NULL,
 * 'url' varchar(2083) NOT NULL default '',
 * 'status' varchar(1) NOT NULL default '',
 * 'depth' int(10) unsigned NOT NULL,
 * 'url_hash' int(11) NOT NULL,
 * 'source_id' int(11) NOT NULL,
 * PRIMARY KEY  ('workload_id'),
 * KEY 'status' ('status'),
 * KEY 'url_hash' ('url_hash'),
 * KEY 'host' ('host')
 * )
 */
public class SQLWorkloadManager implements WorkloadManager
{
  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger("com.heatonresearch.httprecipes.spider.workload.sql.SQLWorkloadManager");

  private SQLHolder holder = new SQLHolder();

  private RepeatableStatement stmtClear;

  private RepeatableStatement stmtClear2;

  private RepeatableStatement stmtAdd;

  private RepeatableStatement stmtAdd2;

  private RepeatableStatement stmtGetWork;

  private RepeatableStatement stmtGetWork2;

  private RepeatableStatement stmtWorkloadEmpty;

  private RepeatableStatement stmtSetWorkloadStatus;

  private RepeatableStatement stmtSetWorkloadStatus2;

  private RepeatableStatement stmtGetDepth;

  private RepeatableStatement stmtGetSource;

  private RepeatableStatement stmtResume;

  private RepeatableStatement stmtResume2;

  private RepeatableStatement stmtGetWorkloadID;

  private RepeatableStatement stmtGetHostID;

  private RepeatableStatement stmtGetNextHost;

  private RepeatableStatement stmtSetHostStatus;

  private RepeatableStatement stmtGetHost;

  /**
   * The driver for the JDBC connection.
   */
  private String driver;

  /**
   * The URL for the JDBC connection.
   */
  private String url;

  /**
   * The UID for the JDBC connection.
   */
  private String uid;

  /**
   * The PWD for the JDBC connection.
   */
  private String pwd;

  /**
   * Only one thread at a time is allowed to add to the
   * workload.
   */
  private Semaphore addLock;

  /**
   * Is there any work?
   */
  private CountDownLatch workLatch;

  /**
   * The maximum size a URL can be.
   */
  private int maxURLSize;

  /**
   * The maximum size that a host can be.
   */
  private int maxHostSize;

  /**
   * All of the RepeatableStatement objects.
   */
  private List<RepeatableStatement> statements = new ArrayList<RepeatableStatement>();

  /**
   * Used to obtain the next URL.
   */
  private RepeatableStatement.Results workResultSet = null;

  /**
   * Used to obtain the next host.
   */
  private RepeatableStatement.Results hostResultSet = null;

  /**
   * A connection to a JDBC database.
   */
  private Connection connection;

  /**
   * The current host.
   */
  private String currentHost;

  /**
   * The ID of the current host.
   */
  private int currentHostID = -1;

  /**
   * Add the specified URL to the workload.
   *
   * @param url
   *          The URL to be added.
   * @param source
   *          The page that contains this URL.
   * @param depth
   *          The depth of this URL.
   * @return True if the URL was added, false otherwise.
   * @throws WorkloadException
   */
  public boolean add(URL url, URL source, int depth) throws WorkloadException
  {
    boolean result = false;
    try
    {
      this.addLock.acquire();
      if (!contains(url))
      {
        String strURL = truncate(url.toString(), this.maxURLSize);
        String strHost = truncate(url.getHost(), this.maxHostSize)
            .toLowerCase();
        result = true;

        // get the host
        int hostID = getHostID(url, false);

        if (hostID == -1)
        {
          this.stmtAdd2.execute(strHost, Status.STATUS_WAITING, 0, 0);
          hostID = getHostID(url, true);
        }

        // need to set the current host for the first time?
        if (this.currentHostID == -1)
        {
          this.currentHostID = hostID;
          this.currentHost = strHost;
          this.stmtSetHostStatus.execute(Status.STATUS_PROCESSING,
              this.currentHostID);
        }

        // now add workload element
        if (source != null)
        {
          int sourceID = getWorkloadID(source, true);
          this.stmtAdd.execute(hostID, strURL, Status.STATUS_WAITING, depth,
              computeHash(url), sourceID);
        } else
        {
          this.stmtAdd.execute(hostID, strURL, Status.STATUS_WAITING, depth,
              computeHash(url), 0);
        }

        this.workLatch.countDown();
      }

    } catch (InterruptedException e)
    {
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      this.addLock.release();
    }
    return result;
  }

  /**
   * Clear the workload.
   *
   * @throws WorkloadException
   *           An error prevented the workload from being
   *           cleared.
   */
  public void clear() throws WorkloadException
  {
    this.stmtClear.execute();
    this.stmtClear2.execute();
  }

  public void close() throws SQLException, WorkloadException
  {
    if (this.workResultSet != null)
    {
      try
      {
        this.workResultSet.close();
      } catch (Exception e)
      {
        logger.log(Level.SEVERE,
            "Error trying to close workload result set, ignoring...");
      }
      this.workResultSet = null;
    }

    for (RepeatableStatement statement : this.statements)
    {
      statement.close();
    }

    if (this.connection != null)
    {
      this.connection.close();
    }

  }

  /**
   * Determine if the workload contains the specified URL.
   *
   * @param url The URL to search the workload for.
   * @return True of the workload contains the specified URL.
   * @throws WorkloadException
   */
  public boolean contains(URL url) throws WorkloadException
  {
    try
    {
      return (getWorkloadID(url, false) != -1);
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    }
  }

  /**
   * Convert the specified String to a URL. If the string is
   * too long or has other issues, throw a
   * WorkloadException.
   *
   * @param url
   *          A String to convert into a URL.
   * @return The URL.
   * @throws WorkloadException
   *           Thrown if, The String could not be 
   *           converted.
   */
  public URL convertURL(String url) throws WorkloadException
  {
    URL result = null;

    url = url.trim();
    if (url.length() > this.maxURLSize)
    {
      throw new WorkloadException("URL size is too big, must be under "
          + this.maxURLSize + " bytes.");
    }

    try
    {
      result = new URL(url);
    } catch (MalformedURLException e)
    {
      throw new WorkloadException(e);
    }
    return result;
  }

  /**
   * @return the connection
   */
  public Connection getConnection()
  {
    return this.connection;
  }

  /**
   * Get the current host.
   *
   * @return The current host.
   */
  public String getCurrentHost()
  {
    return this.currentHost;
  }

  /**
   * Get the depth of the specified URL.
   *
   * @param url
   *          The URL to get the depth of.
   * @return The depth of the specified URL.
   * @throws WorkloadException
   *           Thrown if the depth could not be found.
   */
  public int getDepth(URL url) throws WorkloadException
  {
    RepeatableStatement.Results rs = null;
    try
    {
      rs = this.stmtGetDepth.executeQuery(computeHash(url));
      while (rs.getResultSet().next())
      {
        String u = rs.getResultSet().getString(1);
        if (u.equals(url.toString()))
        {
          return rs.getResultSet().getInt(2);
        }
      }
      return 1;
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }
  }

  /**
   * Get the source page that contains the specified URL.
   *
   * @param url
   *          The URL to seek the source for.
   * @return The source of the specified URL.
   * @throws WorkloadException
   *           Thrown if the source of the specified URL
   *           could not be found.
   */
  public URL getSource(URL url) throws WorkloadException
  {
    RepeatableStatement.Results rs = null;
    try
    {
      rs = this.stmtGetSource.executeQuery(computeHash(url));
      while (rs.getResultSet().next())
      {
        String u = rs.getResultSet().getString(1);
        if (u.equals(url.toString()))
        {
          return (new URL(rs.getResultSet().getString(2)));
        }
      }
      return null;
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } catch (MalformedURLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }
  }

  /**
   * Get a new URL to work on. Wait if there are no URL's
   * currently available. Return null if done with the
   * current host. The URL being returned will be marked as
   * in progress.
   *
   * @return The next URL to work on,
   * @throws WorkloadException
   *           Thrown if the next URL could not be obtained.
   */
  public URL getWork() throws WorkloadException
  {
    URL url = null;
    do
    {
      url = getWorkInternal();
      if (url == null)
      {
        if (workloadEmpty())
        {
          break;
        }

      }
    } while (url == null);

    return url;
  }

  /**
   * Setup this workload manager for the specified spider.
   *
   * @param spider
   *          The spider using this workload manager.
   * @throws WorkloadException
   *           Thrown if there is an error setting up the
   *           workload manager.
   */
  public void init(Spider spider) throws WorkloadException
  {
    this.addLock = new Semaphore(1);
    this.workLatch = new CountDownLatch(1);
    this.workLatch.countDown();

    this.driver = spider.getOptions().dbClass;
    this.url = spider.getOptions().dbURL;
    this.uid = spider.getOptions().dbUID;
    this.pwd = spider.getOptions().dbPWD;

    this.statements.add(this.stmtClear = new RepeatableStatement(this.holder
        .getSQLClear()));
    this.statements.add(this.stmtClear2 = new RepeatableStatement(this.holder
        .getSQLClear2()));
    this.statements.add(this.stmtAdd = new RepeatableStatement(this.holder
        .getSQLAdd()));
    this.statements.add(this.stmtAdd2 = new RepeatableStatement(this.holder
        .getSQLAdd2()));
    this.statements.add(this.stmtGetWork = new RepeatableStatement(this.holder
        .getSQLGetWork()));
    this.statements.add(this.stmtGetWork2 = new RepeatableStatement(this.holder
        .getSQLGetWork2()));
    this.statements.add(this.stmtWorkloadEmpty = new RepeatableStatement(
        this.holder.getSQLWorkloadEmpty()));
    this.statements.add(this.stmtSetWorkloadStatus = new RepeatableStatement(
        this.holder.getSQLSetWorkloadStatus()));
    this.statements.add(this.stmtSetWorkloadStatus2 = new RepeatableStatement(
        this.holder.getSQLSetWorkloadStatus2()));
    this.statements.add(this.stmtGetDepth = new RepeatableStatement(this.holder
        .getSQLGetDepth()));
    this.statements.add(this.stmtGetSource = new RepeatableStatement(
        this.holder.getSQLGetSource()));
    this.statements.add(this.stmtResume = new RepeatableStatement(this.holder
        .getSQLResume()));
    this.statements.add(this.stmtResume2 = new RepeatableStatement(this.holder
        .getSQLResume2()));
    this.statements.add(this.stmtGetWorkloadID = new RepeatableStatement(
        this.holder.getSQLGetWorkloadID()));
    this.statements.add(this.stmtGetHostID = new RepeatableStatement(
        this.holder.getSQLGetHostID()));
    this.statements.add(this.stmtGetNextHost = new RepeatableStatement(
        this.holder.getSQLGetNextHost()));
    this.statements.add(this.stmtSetHostStatus = new RepeatableStatement(
        this.holder.getSQLSetHostStatus()));
    this.statements.add(this.stmtGetHost = new RepeatableStatement(this.holder
        .getSQLGetHost()));

    try
    {
      open();

      this.maxURLSize = getColumnSize("spider_workload", "url");
      this.maxHostSize = getColumnSize("spider_host", "host");
    } catch (InstantiationException e)
    {
      throw (new WorkloadException(e));
    } catch (IllegalAccessException e)
    {
      throw (new WorkloadException(e));
    } catch (ClassNotFoundException e)
    {
      throw (new WorkloadException(e));
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    }
  }

  /**
   * Mark the specified URL as error.
   *
   * @param url
   *          The URL that had an error.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markError(URL url) throws WorkloadException
  {
    try
    {
      setStatus(url, Status.STATUS_ERROR);
      this.workLatch.countDown();
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    }
  }

  /**
   * Mark the specified URL as successfully processed.
   *
   * @param url
   *          The URL to mark as processed.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markProcessed(URL url) throws WorkloadException
  {
    try
    {
      setStatus(url, Status.STATUS_DONE);
      this.workLatch.countDown();
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    }
  }

  /**
   * Move on to process the next host. This should only be
   * called after getWork returns null.
   *
   * @return The name of the next host.
   * @throws WorkloadException
   *           Thrown if the workload manager was unable to
   *           move to the next host.
   */
  public String nextHost() throws WorkloadException
  {
    if (this.currentHostID == -1)
    {
      throw new WorkloadException(
          "Attempting to obtain host before adding first URL.");
    } else
    {
      markHostProcessed(this.currentHost);
    }

    try
    {
      boolean requery = false;

      if (this.hostResultSet == null)
      {
        requery = true;
      } else
      {
        if (!this.hostResultSet.getResultSet().next())
        {
          requery = true;
        }
      }

      if (requery)
      {
        if (this.hostResultSet != null)
        {
          this.hostResultSet.close();
        }

        this.hostResultSet = this.stmtGetNextHost
            .executeQuery(Status.STATUS_WAITING);

        if (!this.hostResultSet.getResultSet().next())
        {
          return null;
        }
      }

      this.currentHostID = this.hostResultSet.getResultSet().getInt(1);
      this.currentHost = this.hostResultSet.getResultSet().getString(2);
      this.stmtSetHostStatus.execute(Status.STATUS_PROCESSING,
          this.currentHostID);
      logger.log(Level.INFO, "Moving to new host: " + this.currentHost);
      return this.currentHost;

    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    }

  }

  /**
   * Setup the workload so that it can be resumed from where
   * the last spider left the workload.
   *
   * @throws WorkloadException
   *           Thrown if we were unable to resume the
   *           processing.
   */
  public void resume() throws WorkloadException
  {
    RepeatableStatement.Results rs = null;

    try
    {
      rs = this.stmtResume.executeQuery();

      if (!rs.getResultSet().next())
      {
        throw (new WorkloadException(
            "Can't resume, unable to determine current host."));
      }

      this.currentHostID = rs.getResultSet().getInt(1);
      this.currentHost = getHost(this.currentHostID);
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }

    this.stmtResume2.execute();

  }

  /**
   * If there is currently no work available, then wait
   * until a new URL has been added to the workload.
   *
   * @param time
   *          The amount of time to wait.
   * @param unit
   *          What time unit is being used.
   */
  public void waitForWork(int time, TimeUnit unit)
  {
    try
    {
      this.workLatch.await(time, unit);
    } catch (InterruptedException e)
    {
      logger.info("Workload latch timed out.");
    }

  }

  /**
   * Return true if there are no more workload units.
   *
   * @return Returns true if there are no more workload
   *         units.
   * @throws WorkloadException
   *           Thrown if there was an error determining if
   *           the workload is empty.
   */
  public boolean workloadEmpty() throws WorkloadException
  {
    RepeatableStatement.Results rs = null;

    try
    {
      rs = this.stmtWorkloadEmpty.executeQuery(this.currentHostID);
      if (!rs.getResultSet().next())
      {
        return true;
      }
      return (rs.getResultSet().getInt(1) < 1);
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }
  }

  /**
   * Compute a hash for a URL.
   *
   * @param url
   *          The URL to compute the hash for.
   * @return The hash code.
   */
  private int computeHash(URL url)
  {
    String str = url.toString().trim();

    int result = str.hashCode();
    result = (result % 0xffff);
    return result;
  }

  /**
   * Return the size of the specified column.
   *
   * @param table
   *          The table that contains the column.
   * @param column
   *          The column to get the size for.
   * @return The size of the column.
   * @throws SQLException
   *           For SQL errors.
   */
  public int getColumnSize(String table, String column) throws SQLException
  {
    ResultSet rs = this.connection.getMetaData().getColumns(null, null, table,
        null);
    while (rs.next())
    {

      String c = rs.getString("COLUMN_NAME");
      int size = rs.getInt("COLUMN_SIZE");
      if (c.equalsIgnoreCase(column))
      {
        return size;
      }
    }
    return -1;
  }

  /**
   * Get the host name associated with the specified host
   * id.
   *
   * @param hostID
   *          The host id to look up.
   * @return The name of the host.
   * @throws WorkloadException
   *           Thrown if unable to obtain the host name.
   */
  private String getHost(int hostID) throws WorkloadException
  {
    RepeatableStatement.Results rs = null;

    try
    {
      rs = this.stmtGetHost.executeQuery(hostID);
      if (!rs.getResultSet().next())
      {
        throw new WorkloadException("Can't find previously created host.");
      }
      return rs.getResultSet().getString(1);
    } catch (SQLException e)
    {
      throw new WorkloadException(e);
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }
  }

  /**
   * Get the id for the specified host name.
   *
   * @param host
   *          The host to lookup.
   * @param require
   *          Should an exception be thrown if the host is
   *          not located.
   * @return The id of the specified host name.
   * @throws WorkloadException
   *           Thrown if the host id is not found, and is
   *           required.
   * @throws SQLException
   *           Thrown if a SQL error occurs.
   */
  private int getHostID(String host, boolean require) throws WorkloadException,
      SQLException
  {
    RepeatableStatement.Results rs = null;

    // is this the current host?
    if (this.currentHostID != -1)
    {
      if (this.currentHost.equalsIgnoreCase(host))
      {
        return this.currentHostID;
      }
    }

    // use the database to find it
    try
    {
      rs = this.stmtGetHostID.executeQuery(host);
      if (rs.getResultSet().next())
      {
        return rs.getResultSet().getInt(1);
      }
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }

    if (require)
    {
      StringBuilder str = new StringBuilder();
      str.append("Failed to find previously visited Host,");
      str.append("Host=\"");
      str.append(this.url.toString());
      str.append("\".");
      throw (new WorkloadException(str.toString()));
    } else
    {
      return -1;
    }
  }

  /**
   * Get the ID for the given host. The host name is
   * extracted from the specified URL.
   *
   * @param url
   *          The URL that specifies the host name to
   *          lookup.
   * @param require
   *          Should an exception be thrown if the host is
   *          not located.
   * @return The id of the specified host name.
   * @throws WorkloadException
   *           Thrown if the host id is not found, and is
   *           required.
   * @throws SQLException
   *           Thrown if a SQL error occurs.
   */
  private int getHostID(URL url, boolean require) throws SQLException,
      WorkloadException
  {
    String host = url.getHost().toLowerCase();
    return getHostID(host, require);
  }

  /**
   * Called internally to get a work unit. This function
   * does not wait for work, rather it simply returns null.
   *
   * @return The next URL to process.
   * @throws WorkloadException
   *           Thrown if unable to obtain a URL.
   */
  private URL getWorkInternal() throws WorkloadException
  {
    if (this.currentHostID == -1)
    {
      throw new WorkloadException(
          "Attempting to obtain work before adding first URL.");
    }

    try
    {
      boolean requery = false;

      if (this.workResultSet == null)
      {
        requery = true;
      } else
      {
        if (!this.workResultSet.getResultSet().next())
        {
          requery = true;
        }
      }

      if (requery)
      {
        if (this.workResultSet != null)
        {
          this.workResultSet.close();
        }

        this.workResultSet = this.stmtGetWork.executeQuery(
            Status.STATUS_WAITING, this.currentHostID);

        if (!this.workResultSet.getResultSet().next())
        {
          return null;
        }
      }

      int id = this.workResultSet.getResultSet().getInt(1);
      String url = this.workResultSet.getResultSet().getString(2);

      this.stmtGetWork2.execute(Status.STATUS_PROCESSING, id);
      return new URL(url);

    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } catch (MalformedURLException e)
    {
      throw (new WorkloadException(e));
    }
  }

  /**
   * Get the workload ID, given a URL.
   *
   * @param url
   *          The URL to look up.
   * @param require
   *          Should an exception be thrown if the workload
   *          is not located.
   * @return The ID of the workload.
   * @throws WorkloadException
   *           Thrown if the host id is not found, and is
   *           required.
   * @throws SQLException
   *           Thrown if a SQL error occurs.
   */
  private int getWorkloadID(URL url, boolean require) throws SQLException,
      WorkloadException
  {
    int hash = 0;
    RepeatableStatement.Results rs = null;
    try
    {
      hash = computeHash(url);
      rs = this.stmtGetWorkloadID.executeQuery(hash);
      while (rs.getResultSet().next())
      {
        if (rs.getResultSet().getString(2).equals(url.toString()))
        {
          return rs.getResultSet().getInt(1);
        }
      }
    } finally
    {
      if (rs != null)
      {
        rs.close();
      }
    }

    if (require)
    {
      StringBuilder str = new StringBuilder();
      str.append("Failed to find previously visited URL, hash=\"");
      str.append(hash);
      str.append("\", URL=\"");
      str.append(url.toString());
      str.append("\".");
      throw (new WorkloadException(str.toString()));
    } else
    {
      return -1;
    }
  }

  /**
   * Mark the specified host as processed.
   *
   * @param host
   *          The host to mark.
   * @throws WorkloadException
   *           Thrown if the host cannot be marked.
   */
  private void markHostProcessed(String host) throws WorkloadException
  {
    try
    {
      int hostID = this.getHostID(host, true);
      this.stmtSetHostStatus.execute(Status.STATUS_DONE, hostID);
    } catch (SQLException e)
    {
      throw new WorkloadException(e);
    }
  }

  /**
   * Open a database connection.
   *
   * @throws InstantiationException
   *           Thrown if the database driver could not be
   *           opened.
   * @throws IllegalAccessException
   *           Thrown if the database driver can not be
   *           acccessed.
   * @throws ClassNotFoundException
   *           Thrown if the wrong type of class is
   *           returned.
   * @throws WorkloadException
   *           Thrown if the database cannot be opened.
   * @throws SQLException
   *           Thrown if a SQL error occurs.
   */
  private void open() throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, SQLException, WorkloadException
  {
    Class.forName(this.driver).newInstance();
    this.connection = DriverManager.getConnection(this.url, this.uid, this.pwd);
    for (RepeatableStatement statement : this.statements)
    {
      statement.create(this);
    }
  }

  /**
   * Set the status for the specified URL.
   *
   * @param url
   *          The URL to set the status for.
   * @param status
   *          What to set the status to.
   * @throws WorkloadException
   *           Thrown if the status cannot be set.
   * @throws SQLException
   *           Thrown if a SQL error occurs.
   */
  private void setStatus(URL url, String status) throws SQLException,
      WorkloadException
  {
    int id = getWorkloadID(url, true);
    this.stmtSetWorkloadStatus.execute("" + status, id);
    if (status.equalsIgnoreCase(Status.STATUS_ERROR))
    {
      this.stmtSetWorkloadStatus2.execute(0, 1, url.getHost().toLowerCase());
    } else if (status.equalsIgnoreCase(Status.STATUS_DONE))
    {
      this.stmtSetWorkloadStatus2.execute(1, 0, url.getHost().toLowerCase());
    }

  }

  /**
   * Truncate a string to the specified length.
   *
   * @param str
   *          The string to truncate.
   * @param length
   *          The length to truncate the string to.
   * @return The truncated string.
   */
  private String truncate(String str, int length)
  {
    if (str.length() < length)
    {
      return str;
    } else
    {
      return str.substring(0, length);
    }
  }

  /**
   * Try to open the database connection.
   *
   * @throws WorkloadException
   *           Thrown if the open fails.
   */
  void tryOpen() throws WorkloadException
  {
    Exception ex = null;

    logger.log(Level.SEVERE,
        "Lost connection to database, trying to reconnect.");

    for (int i = 1; i < 120; i++)
    {
      try
      {
        close();
      } catch (Exception e1)
      {
        logger.log(Level.SEVERE,
            "Failed while trying to close lost connection, ignoring...", e1);
      }

      ex = null;

      try
      {
        logger.log(Level.SEVERE, "Attempting database reconnect");
        open();
        logger.log(Level.SEVERE, "Database connection reestablished");
        break;
      } catch (Exception e)
      {
        ex = e;
        logger.log(Level.SEVERE, "Reconnect failed", ex);
      }

      if (ex != null)
      {
        try
        {
          logger.log(Level.SEVERE, "Reconnect attempt " + i
              + " failed.  Waiting to try again.");
          Thread.sleep(30000);
        } catch (InterruptedException e)
        {
        }
      }
    }

    if (ex != null)
    {
      throw (new WorkloadException(ex));
    }

  }

  public SQLHolder createSQLHolder()
  {
    return new SQLHolder();
  }

}
