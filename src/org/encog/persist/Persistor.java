/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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

package org.encog.persist;

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;

/**
 * This interface defines a class that can load and save an
 * EncogPersistedObject.
 * 
 * @author jheaton
 * 
 */
public interface Persistor {

	/**
	 * Load from the specified node.
	 * 
	 * @param in
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	EncogPersistedObject load(ReadXML in);

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The object to save.
	 * @param out
	 *            The XML object.
	 */
	void save(EncogPersistedObject object, WriteXML out);
}
