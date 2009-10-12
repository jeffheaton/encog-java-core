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

/**
 * Implements the basic functionality that most extractors will need to
 * implement. Mostly this involves maintaining a collection of the extraction
 * listeners that will receive events as the extraction occurs.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicExtract implements Extract {

	/**
	 * The classes registered as listeners for the extraction.
	 */
	private final Collection<ExtractListener> listeners = new ArrayList<ExtractListener>();

	/**
	 * The logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add a listener for the extraction.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(final ExtractListener listener) {
		this.listeners.add(listener);

	}

	/**
	 * Distribute an object to the listeners.
	 * 
	 * @param object
	 *            The object to be distributed.
	 */
	public void distribute(final Object object) {
		for (final ExtractListener listener : getListeners()) {
			listener.foundData(object);
		}
	}

	/**
	 * Extract from the web page and return the results as a list.
	 * 
	 * @param page
	 *            The web page to extract from.
	 * @return The results of the extraction as a List.
	 */
	public List<Object> extractList(final WebPage page) {
		getListeners().clear();
		final ListExtractListener listener = new ListExtractListener();
		addListener(listener);
		extract(page);
		return listener.getList();
	}

	/**
	 * @return A list of listeners registered with this object.
	 */
	public Collection<ExtractListener> getListeners() {
		return this.listeners;
	}

	/**
	 * Remove the specified listener.
	 * 
	 * @param listener
	 *            The listener to rmove.
	 */
	public void removeListener(final ExtractListener listener) {
		this.listeners.remove(listener);

	}

}
