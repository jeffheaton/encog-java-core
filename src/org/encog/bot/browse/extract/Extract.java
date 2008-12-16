package org.encog.bot.browse.extract;

import java.util.Collection;
import java.util.List;

import org.encog.bot.browse.WebPage;

public interface Extract {
	public void extract(WebPage page);
	public List<Object> extractList(WebPage page);
	public void addListener(ExtractListener listener);
	public void removeListener(ExtractListener listener);
	public Collection<ExtractListener> getListeners();
}
