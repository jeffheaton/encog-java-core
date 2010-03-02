/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.persist.persistors.generic;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Simple object to persist with the generic class.
 * 
 * @author jheaton
 */
public class SimpleObjectToPersist implements EncogPersistedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3193782478666198020L;

	/**
	 * The first test string.
	 */
	private String first;

	/**
	 * The second test string.
	 */
	private String second;

	/**
	 * A test number.
	 */
	private double number;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used.
	 */
	@Override
	public Object clone() {
		return null;
	}

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used for this simple test.
	 */
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The first test string.
	 */
	public String getFirst() {
		return this.first;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The second test string.
	 */
	public double getNumber() {
		return this.number;
	}

	/**
	 * @return The second test string.
	 */
	public String getSecond() {
		return this.second;
	}

	/**
	 * Set the description of this object.
	 * 
	 * @param theDescription
	 *            The description of this object.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Set the first test string.
	 * 
	 * @param first
	 *            The new value.
	 */
	public void setFirst(final String first) {
		this.first = first;
	}

	/**
	 * Set the name of this object.
	 * 
	 * @param theName
	 *            The name of this object.
	 */
	public void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * The number to test with.
	 * 
	 * @param number
	 *            The new value.
	 */
	public void setNumber(final double number) {
		this.number = number;
	}

	/**
	 * The second test string.
	 * 
	 * @param second
	 *            The new value.
	 */
	public void setSecond(final String second) {
		this.second = second;
	}

}
