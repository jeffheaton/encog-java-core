/*
 * Encog(tm) Core v2.6 - Java Version
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.encog.EncogError;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A location that allows Encog objects to be read from a resource. This
 * location only supports read operations, so the Encog resource is usually
 * created first as a file and then embedded in the application as a resource.
 * 
 * An example of the format for a file stored this way is:
 * 
 * org/encog/data/classes.txt
 * 
 * 
 */
public class ResourcePersistence implements PersistenceLocation {

	/**
	 * The name of the resource to read from.
	 */
	private final String resource;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory
			.getLogger(EncogPersistedCollection.class);

	/**
	 * Construct a location to read from the specified resource.
	 * 
	 * An example of the format for a file stored this way is:
	 * 
 	 * org/encog/data/classes.txt
	 * 
	 * @param resource
	 *            The resource to read from.
	 */
	public ResourcePersistence(final String resource) {
		this.resource = resource;
	}

	/**
	 * Create an input stream to read from the resource.
	 * 
	 * @return An input stream.
	 */
	public InputStream createInputStream() {

		final ClassLoader loader = this.getClass().getClassLoader();
		final InputStream is = loader.getResourceAsStream(this.resource);

		if (is == null) {
			final String str = "Can't read resource: " + this.resource;
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PersistError(str);
		}
		return is;
	}

	/**
	 * Write operations are not supported for resource persistence.
	 * 
	 * @return Nothing.
	 */
	public OutputStream createOutputStream() {
		final String str = "The ResourcePersistence location does not support write operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * Delete operations are not supported for resource persistence.
	 */
	public void delete() {
		final String str = "The ResourcePersistence location does not support delete operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);

	}

	/**
	 * Exist is not supported for resource persistence.
	 * 
	 * @return Nothing.
	 */
	public boolean exists() {
		final String str = "The ResourcePersistence location does not support exists.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * Rename is not supported for resource persistence.
	 * 
	 * @param toLocation
	 *            Not used.
	 */
	public void renameTo(final PersistenceLocation toLocation) {
		final String str = "The ResourcePersistence location does not support rename operations.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * Load the resource as a string.
	 * 
	 * @return The resource as a string.
	 */
	public String loadString() {
		InputStream is = null;
		try {
			is = createInputStream();
			StringBuilder result = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
			return result.toString();
		} catch (IOException e) {
			throw new PersistError(e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				throw new EncogError(e);
			}
		}
	}

}
