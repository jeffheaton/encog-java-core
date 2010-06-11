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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.PersistError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A persistence location based on a file.
 * 
 * @author jheaton
 * 
 */
public class FilePersistence implements PersistenceLocation {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory
			.getLogger(EncogPersistedCollection.class);

	/**
	 * The file to persist to/from.
	 */
	private final File file;

	/**
	 * Construct a persistance location based on a file.
	 * 
	 * @param file
	 *            The file to use.
	 */
	public FilePersistence(final File file) {
		this.file = file;
	}

	/**
	 * @return A new InputStream for this file.
	 */
	public InputStream createInputStream() {
		try {
			return new FileInputStream(this.file);
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}
			throw new PersistError(e);
		}
	}

	/**
	 * @return A new OutputStream for this file.
	 */
	public OutputStream createOutputStream() {
		try {
			return new FileOutputStream(this.file);
		} catch (final IOException e) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Exception", e);
			}
			throw new PersistError(e);
		}
	}

	/**
	 * Attempt to delete the file.
	 */
	public void delete() {
		if (!this.file.delete()) {
			final String str = "Failed to delete: " + this.file;
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PersistError(str);
		}

	}

	/**
	 * @return True if the file exists.
	 */
	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * @return The file this location is based on.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Rename this file to a different location.
	 * 
	 * @param toLocation
	 *            What to rename to.
	 */
	public void renameTo(final PersistenceLocation toLocation) {
		if (!(toLocation instanceof FilePersistence)) {
			final String str = "Can only rename from one FilePersistence location to another";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PersistError(str);
		}

		final File toFile = ((FilePersistence) toLocation).getFile();

		if (!this.file.renameTo(toFile)) {
			final String str = "Failure during merge, can't rename:\n"
					+ this.file + "to: " + toFile;

			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PersistError(str);
		}

	}

}
