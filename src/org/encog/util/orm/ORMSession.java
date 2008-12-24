package org.encog.util.orm;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class ORMSession {
	
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
