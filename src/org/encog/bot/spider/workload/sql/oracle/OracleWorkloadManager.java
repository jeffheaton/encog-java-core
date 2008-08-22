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
package org.encog.bot.spider.workload.sql.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.encog.bot.spider.workload.sql.SQLHolder;
import org.encog.bot.spider.workload.sql.SQLWorkloadManager;
import org.encog.util.db.DBError;

/**
 * OracleWorkloadManager: This workload manager is compatible with the Oracle
 * database.
 */
public class OracleWorkloadManager extends SQLWorkloadManager {
	
	/**
	 * Get the Oracle SQL holder.
	 * @return A SQL holder.
	 */
	public SQLHolder createSQLHolder() {
		return new OracleHolder();
	}

	/**
	 * Return the size of the specified column.
	 * 
	 * @param table
	 *            The table that contains the column.
	 * @param column
	 *            The column to get the size for.
	 * @return The size of the column.
	 */
	public int getColumnSize(final String table, final String column) {
		try {
			final ResultSet rs = getConnection().getConnection().getMetaData()
					.getColumns(null, null, table, "%");
			while (rs.next()) {

				final String c = rs.getString("COLUMN_NAME");
				final int size = rs.getInt("COLUMN_SIZE");
				if (c.equalsIgnoreCase(column)) {
					return size;
				}
			}
			return -1;
		} catch (final SQLException e) {
			throw new DBError(e);
		}
	}
}
