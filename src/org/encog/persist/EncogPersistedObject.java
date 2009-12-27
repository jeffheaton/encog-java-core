/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.persist;

import java.io.Serializable;

/**
 * This interface flags an class as being able to be persisted into an Encog
 * collection.
 * 
 * @author jheaton
 * 
 */
public interface EncogPersistedObject extends Serializable {

	/**
	 * Create a persistor for this object.
	 * 
	 * @return A persistor for this object.
	 */
	Persistor createPersistor();

	/**
	 * @return The description of this object.
	 */
	String getDescription();

	/**
	 * @return The name of this object.
	 */
	String getName();

	/**
	 * Set the description of this object.
	 * 
	 * @param theDescription
	 *            The description.
	 */
	void setDescription(String theDescription);

	/**
	 * Set the name of this object.
	 * 
	 * @param theName
	 *            The name of this object.
	 */
	void setName(String theName);

}
