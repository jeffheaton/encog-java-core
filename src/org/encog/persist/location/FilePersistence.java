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
 * A persistance location based on a file.
 * 
 * @author jheaton
 * 
 */
public class FilePersistence implements PersistenceLocation {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public void renameTo(final PersistenceLocation toLocation) {
		if (!(toLocation instanceof FilePersistence)) {
			final String str = 
				"Can only rename from one FilePersistence location to another";
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
