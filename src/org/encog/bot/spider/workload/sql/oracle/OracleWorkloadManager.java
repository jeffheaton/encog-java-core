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
package org.encog.bot.spider.workload.sql.oracle;

import java.sql.*;

import org.encog.bot.spider.workload.sql.SQLHolder;
import org.encog.bot.spider.workload.sql.SQLWorkloadManager;


/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * OracleWorkloadManager: This workload manager is compatible
 * with the Oracle database.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class OracleWorkloadManager extends SQLWorkloadManager
{
  public SQLHolder createSQLHolder()
  {
    return new OracleHolder();
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
    ResultSet rs = this.getConnection().getMetaData().getColumns(null, null,
        table, "%");
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
}
