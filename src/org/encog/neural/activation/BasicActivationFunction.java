/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.activation;

import org.encog.persist.EncogCollection;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.generic.GenericPersistor;

/**
 * Holds basic functionality that all activation functions will likely have use
 * of. Specifically it implements a name and description for the
 * EncogPersistedObject class.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicActivationFunction implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 672555213449163812L;

	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	private EncogCollection encogCollection;
	
	
	protected double[] params;
	
	/**
	 * @return The object cloned.
	 */
	@Override
	public abstract Object clone();

	/**
	 * @return Always returns null, descriptions and names are not used for
	 *         activation functions.
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * @return Always returns null, descriptions and names are not used for
	 *         activation functions.
	 */
	public String getName() {
		return null;
	}

	/**
	 * Ignore the description, it is not used for activation functions.
	 * 
	 * @param theDescription
	 *            Ignored.
	 */
	public void setDescription(final String theDescription) {

	}

	/**
	 * Ignore the name, it is not used for activation functions.
	 * 
	 * @param theName
	 *            Ignored.
	 */
	public void setName(final String theName) {

	}
	
	/**
	 * Create a Persistor for this activation function.
	 * 
	 * @return The persistor.
	 */
	public Persistor createPersistor() {
		return new GenericPersistor(this.getClass());
	}

	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}
	
	public double[] getParams()
	{
		return this.params;
	}
	
	public void setParam(int index, double value)
	{
		this.params[index] = value;
	}
	
	public abstract String[] getParamNames();

}
