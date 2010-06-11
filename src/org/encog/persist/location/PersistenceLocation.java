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

package org.encog.persist.location;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A persistence location specifies how the persistence collection is stored.
 * 
 * @author jheaton
 * 
 */
public interface PersistenceLocation {

	/**
	 * @return A new input stream to read data from.
	 */
	InputStream createInputStream();

	/**
	 * @return A new output stream to write data to.
	 */
	OutputStream createOutputStream();

	/**
	 * Delete the location, if possible.
	 */
	void delete();

	/**
	 * @return True if this location exists.
	 */
	boolean exists();

	/**
	 * Attempt to rename this location. Mainly for file locations.
	 * 
	 * @param toLocation
	 *            The new name.
	 */
	void renameTo(PersistenceLocation toLocation);
}
