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

/**
 * SQLHolder: This class holds the SQL statements used
 * by the Heaton Research Spider.  This is the generic 
 * set that should work with most DBMS systems.  However
 * you will also find customized versions of this class
 * to support specific DBMS systems, when it is needed.
 */
public class SQLHolder
{

  public String getSQLClear()
  {
    return "DELETE FROM spider_workload";
  }

  public String getSQLClear2()
  {
    return "DELETE FROM spider_host WHERE status <> 'I'";
  }

  public String getSQLAdd()
  {
    return "INSERT INTO spider_workload(host,url,status,depth,url_hash,source_id) VALUES(?,?,?,?,?,?)";
  }

  public String getSQLAdd2()
  {
    return "INSERT INTO spider_host(host,status,urls_done,urls_error) VALUES(?,?,?,?)";
  }

  public String getSQLGetWork()
  {
    return "SELECT workload_id,URL FROM spider_workload WHERE status =  ? AND host = ?";
  }

  public String getSQLGetWork2()
  {
    return "UPDATE spider_workload SET status =  ? WHERE workload_id = ?";
  }

  public String getSQLWorkloadEmpty()
  {
    return "SELECT COUNT(*) FROM spider_workload WHERE status in ('P','W') AND host =  ?";
  }

  public String getSQLSetWorkloadStatus()
  {
    return "UPDATE spider_workload SET status =  ? WHERE workload_id =  ?";
  }

  public String getSQLSetWorkloadStatus2()
  {
    return "UPDATE spider_host SET urls_done =  urls_done + ?, urls_error =  urls_error + ? WHERE host =  ?";
  }

  public String getSQLGetDepth()
  {
    return "SELECT url,depth FROM spider_workload WHERE url_hash =  ?";
  }

  public String getSQLGetSource()
  {
    return "SELECT w.url,s.url FROM spider_workload w,spider_workload s WHERE w.source_id =  s.workload_id AND w.url_hash =  ?";
  }

  public String getSQLResume()
  {
    return "SELECT distinct host FROM spider_workload WHERE status =  'P'";
  }

  public String getSQLResume2()
  {
    return "UPDATE spider_workload SET status =  'W' WHERE status =  'P'";
  }

  public String getSQLGetWorkloadID()
  {
    return "SELECT workload_id,url FROM spider_workload WHERE url_hash =  ?";
  }

  public String getSQLGetHostID()
  {
    return "SELECT host_id,host FROM spider_host WHERE host =  ?";
  }

  public String getSQLGetNextHost()
  {
    return "SELECT host_id,host FROM spider_host WHERE status =  ?";
  }

  public String getSQLSetHostStatus()
  {
    return "UPDATE spider_host SET status =  ? WHERE host_id =  ?";
  }

  public String getSQLGetHost()
  {
    return "SELECT host FROM spider_host WHERE host_id =  ?";
  }

}
