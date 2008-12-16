package org.encog.bot.browse.extract;

import java.util.ArrayList;
import java.util.List;

public class ListExtractListener implements ExtractListener {
	
	private List<Object> list = new ArrayList<Object>();
	
	public List<Object> getList()
	{
		return this.list;
	}
	
	public void foundData(Object object)
	{
		list.add(object);
	}
}
