package org.encog.util.orm;


import org.hibernate.Query;
import org.hibernate.ScrollableResults;

public class ScrollingResult {
	
	private ScrollableResults scroll;
	private ORMSession session;
	private int countDown;
	
	public ScrollingResult(ORMSession session,Query q)
	{
		this.scroll = q.scroll();
		this.session = session;
		this.countDown = 0;
	}
	
	public DataObject next(int count)
	{	
		if( !scroll.next() )
		{
			scroll.close();
			return null;
		}
		
		countDown++;
		if( countDown>count )
		{
			//session.flush();
			//session.clear();
		}
		
		return (DataObject)scroll.get(0);		
	}
}
