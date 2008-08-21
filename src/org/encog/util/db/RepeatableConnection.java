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
package org.encog.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepeatableConnection {
	
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
	   * A connection to a JDBC database.
	   */
	  private Connection connection;
	  
	  /**
	   * All of the RepeatableStatement objects.
	   */
	  private List<RepeatableStatement> statements = new ArrayList<RepeatableStatement>();

	  
	  /**
	   * The logger.
	   */
	  private static Logger logger = Logger
	      .getLogger("org.encog.util.db.RepeatableConnection");

	
	  public RepeatableConnection(String driver,String url,String uid,String pwd)
	  {
		  this.driver = driver;
		  this.url = url;
		  this.uid = uid;
		  this.pwd = pwd;
	  }
	  
	
	  /**
	   * Try to open the database connection.
	   *
	   * @throws WorkloadException
	   *           Thrown if the open fails.
	   */
	  void tryOpen() 
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
	      throw (new DBError(ex));
	    }

	  }
	  
	  public void close() 
	  {
	    for (RepeatableStatement statement : this.statements)
	    {
	      statement.close();
	    }

	    if (this.connection != null)
	    {
	    	try
	    	{
	      this.connection.close();
	    	}
	    	catch(SQLException e)
	    	{
	    		throw new DBError(e);
	    	}
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
	  public void open()
	  {
		  try
		  {
	    Class.forName(this.driver).newInstance();
	    this.connection = DriverManager.getConnection(this.url, this.uid, this.pwd);
	    for (RepeatableStatement statement : this.statements)
	    {
	      statement.init(this);
	    }
		  }
		  catch(SQLException e)
		  {
			  throw new DBError(e);
		  } catch (InstantiationException e) {
			  throw new DBError(e);
		} catch (IllegalAccessException e) {
			throw new DBError(e);
		} catch (ClassNotFoundException e) {
			throw new DBError(e);
		}
	  }
	  
	  /**
	   * @return the connection
	   */
	  public Connection getConnection()
	  {
	    return this.connection;
	  }


	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}


	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}


	/**
	 * @return the statements
	 */
	public List<RepeatableStatement> getStatements() {
		return statements;
	}
	  
	public RepeatableStatement createStatement(String sql)
	{
		RepeatableStatement result;
		this.statements.add(result = new RepeatableStatement(sql));	
		return result;
	}
}
