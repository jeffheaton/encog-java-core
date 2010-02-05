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

package org.encog.persist.location;

import java.io.InputStream;
import java.io.OutputStream;

import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A location that allows Encog objects to be read from a resource. This
 * location only supports read operations, so the Encog resource is usually
 * created first as a file and then embedded in the application as a resource.
 * 
 * @author jheaton
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

}
