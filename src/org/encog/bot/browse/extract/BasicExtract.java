package org.encog.bot.browse.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.bot.browse.WebPage;

public abstract class BasicExtract implements Extract {

	private Collection<ExtractListener> listeners = new ArrayList<ExtractListener>();
	
	@Override
	public void addListener(ExtractListener listener) {
		this.listeners.add(listener);
		
	}
	
	@Override
	public void removeListener(ExtractListener listener) {
		this.listeners.remove(listener);
		
	}

	@Override
	public Collection<ExtractListener> getListeners() {
		return this.listeners;
	}
	
	public List<Object> extractList(WebPage page)
	{
		getListeners().clear();
		ListExtractListener listener = new ListExtractListener();
		this.addListener(listener);
		extract(page);
		return listener.getList();
	}
	
	public void distribute(Object object)
	{
		for(ExtractListener listener: getListeners())
		{
			listener.foundData(object);
		}
	}

}
