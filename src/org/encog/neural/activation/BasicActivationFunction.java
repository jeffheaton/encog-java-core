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
package org.encog.neural.activation;

import org.encog.persist.Persistor;

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
	 * @return The object cloned.
	 */
	@Override
	public Object clone() {
		return null;
	}

	/**
	 * Create a Persistor for this activation function.
	 * @return The persistor.
	 */
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * @return Always returns null, descriptions and names are not used
	 * for activation functions.
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * @return Always returns null, descriptions and names are not used
	 * for activation functions.
	 */
	public String getName() {
		return null;
	}

	/**
	 * Ignore the description, it is not used for activation functions.
	 * @param theDescription Ignored.
	 */
	public void setDescription(final String theDescription) {

	}

	/**
	 * Ignore the name, it is not used for activation functions.
	 * @param theName Ignored.
	 */
	public void setName(final String theName) {

	}

}
