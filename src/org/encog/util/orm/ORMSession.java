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
package org.encog.util.orm;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Hibernate session.  This class provides access to the Hibernate persisted
 * database.
 * @author jheaton
 *
 */
public class ORMSession {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private org.hibernate.Session session;
	private org.hibernate.Transaction transaction;
	
	public ORMSession(Session session)
	{
		this.session = session;
	}
	
	public void begin()
	{
		transaction = session.beginTransaction();
	}
	
	public void commit()
	{
		transaction.commit();		
	}
	
	public void rollback()
	{
		transaction.rollback();
	}
	
	public void save(DataObject obj)
	{
		obj.validate();
		session.save(obj);
	}
	
	public void delete(Object obj)
	{
		session.delete(obj);
	}
	
	public Query createQuery(String sql)
	{
		return session.createQuery(sql);
	}
	
	public Query createSQLQuery(String sql)
	{
		return session.createSQLQuery(sql);
	}
		
	public void close() 
	{
		try
		{
			session.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void clear()
	{
		session.clear();

	}
	
	public void evict(DataObject obj)
	{
		session.evict(obj);
	}
	
	public void flush()
	{
		session.flush();
	}

	public void executeSQL(String sql) {
		createSQLQuery(sql).executeUpdate();
	}

}
