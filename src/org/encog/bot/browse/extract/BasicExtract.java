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
package org.encog.bot.browse.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.bot.browse.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicExtract implements Extract {

	private Collection<ExtractListener> listeners = new ArrayList<ExtractListener>();
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void addListener(ExtractListener listener) {
		this.listeners.add(listener);
		
	}
	
	public void removeListener(ExtractListener listener) {
		this.listeners.remove(listener);
		
	}

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
