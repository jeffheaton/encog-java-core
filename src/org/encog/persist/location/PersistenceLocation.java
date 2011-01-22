/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
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
