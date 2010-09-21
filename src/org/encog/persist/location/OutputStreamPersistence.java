/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class allows Encog persistence to use a location that is based on an
 * OutputStream.
 * 
 */
public class OutputStreamPersistence implements PersistenceLocation {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory
			.getLogger(OutputStreamPersistence.class);

	/**
	 * The output stream.
	 */
	private final OutputStream ostream;

	/**
	 * Construct the output stream location.
	 * 
	 * @param ostream
	 *            The OutputStream to base this on.
	 */
	public OutputStreamPersistence(final OutputStream ostream) {
		this.ostream = ostream;
	}

	/**
	 * Can't create an InputStream for this object type, throw an error.
	 * 
	 * @return Not used.
	 */
	public InputStream createInputStream() {
		final String str = "The InputStreamPersistence location does not support output streams.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * Provide the output stream to use.
	 * 
	 * @return The output stream.
	 */
	public OutputStream createOutputStream() {
		return this.ostream;
	}

	/**
	 * Delete operations are not supported for resource persistence.
	 */
	public void delete() {
		final String str = "The OutputStreamPersistence location does not support delete operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);

	}

	/**
	 * @return Because this is based on a stream, it always exists, so return true.
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * Renames are not allowed on this type of location.
	 * 
	 * @param toLocation
	 *            Not used.
	 */
	public void renameTo(final PersistenceLocation toLocation) {
		final String str = "The OutputStreamPersistence location does not support rename operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

}
