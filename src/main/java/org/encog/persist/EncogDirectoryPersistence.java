/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.persist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.encog.Encog;
import org.encog.util.file.ResourceInputStream;
import org.encog.util.logging.EncogLogging;

/**
 * Handles Encog persistence for a directory. This is the usual mode where each
 * resource is stored in a separate EG file.
 * 
 */
public class EncogDirectoryPersistence {

	/**
	 * Load an EG object as a reousrce.
	 * @param res
	 * @return
	 */
	public static Object loadResourceObject(final String res) {
		InputStream is = null;
		try {
		 is = ResourceInputStream.openResourceInputStream(res);
		return loadObject(is);
		} finally {
			try {
				if( is!=null ) {
					is.close();
				}
			} catch(IOException ex) {
				
			}
		}
	}
	
	/**
	 * Load the specified object.
	 * @param file The file to load.
	 * @return The loaded object.
	 */
	public static Object loadObject(final File file) {
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			final Object result = EncogDirectoryPersistence.loadObject(fis);

			return result;
		} catch (final IOException ex) {
			throw new PersistError(ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					EncogLogging.log(e);
				}
			}
		}
	}

	/**
	 * Load an object from an input stream.
	 * @param is The input stream to read from.
	 * @return The loaded object.
	 */
	public static Object loadObject(final InputStream is) {

		final String header = EncogDirectoryPersistence.readLine(is);
		final String[] params = header.split(",");

		if (!"encog".equals(params[0])) {
			throw new PersistError("Not a valid EG file.");
		}

		final String name = params[1];

		final EncogPersistor p = PersistorRegistry.getInstance().getPersistor(
				name);

		if (p == null) {
			throw new PersistError("Do not know how to read the object: "
					+ name);
		}

		if (p.getFileVersion() < Integer.parseInt(params[4])) {
			throw new PersistError(
					"The file you are trying to read is from a later version of Encog.  Please upgrade Encog to read this file.");
		}

		return p.read(is);

	}

	/**
	 * Read a line from the input stream.
	 * @param is The input stream.
	 * @return The line read.
	 */
	private static String readLine(final InputStream is) {
		try {
			final StringBuilder result = new StringBuilder();

			char ch;

			do {
				final int b = is.read();
				if (b == -1) {
					return result.toString();
				}

				ch = (char) b;

				if ((ch != 13) && (ch != 10)) {
					result.append(ch);
				}

			} while (ch != 10);

			return result.toString();
		} catch (final IOException ex) {
			throw new PersistError(ex);
		}
	}

	/**
	 * Save the specified object.
	 * @param filename The filename to save to.
	 * @param obj The Object to save.
	 */
	public static void saveObject(final File filename, 
			final Object obj) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(filename);
			EncogDirectoryPersistence.saveObject(fos, obj);
		} catch (final IOException ex) {
			throw new PersistError(ex);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (final IOException e) {
				EncogLogging.log(e);
			}
		}
	}

	/**
	 * Save the specified object.
	 * @param os The output stream to write to.
	 * @param obj The object to save.
	 */
	public static void saveObject(final OutputStream os, final Object obj) {
		try {
			final EncogPersistor p = PersistorRegistry.getInstance()
					.getPersistor(obj.getClass());

			if (p == null) {
				throw new PersistError("Do not know how to persist object: "
						+ obj.getClass().getSimpleName());
			}

			os.flush();
			final PrintWriter pw = new PrintWriter(os);
			final Date now = new Date();
			pw.println("encog," + obj.getClass().getSimpleName() + ",java,"
					+ Encog.VERSION + "," + p.getFileVersion() + ","
					+ now.getTime());
			pw.flush();
			p.save(os, obj);
		} catch (final IOException ex) {
			throw new PersistError(ex);
		}
	}

	/**
	 * The directory that holds the EG files.
	 */
	private final File parent;

	/**
	 * Construct the object.
	 * @param parent The directory to use.
	 */
	public EncogDirectoryPersistence(final File parent) {
		this.parent = parent;
	}

	/**
	 * Get the type of an Encog object in an EG file, without the 
	 * need to read the entire file.
	 * @param name The filename to read.
	 * @return The type.
	 */
	public String getEncogType(final String name) {
		BufferedReader br = null;
		
		try {
			final File path = new File(this.parent, name);
			br = new BufferedReader(new FileReader(path));
			final String header = br.readLine();
			final String[] params = header.split(",");

			return params[1];
		} catch (final IOException ex) {
			throw new PersistError(ex);
		} finally {
			if( br!=null ) {
				try {
					br.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}				
			}
		}
	}

	/**
	 * @return The directory.
	 */
	public final File getParent() {
		return this.parent;
	}

	/**
	 * Load a file from the directory that this object refers to.
	 * @param name The name to load.
	 * @return The object.
	 */
	public final Object loadFromDirectory(final String name) {
		final File path = new File(this.parent, name);
		return EncogDirectoryPersistence.loadObject(path);
	}

	/**
	 * Save a file to the directory that this object refers to.
	 * @param name The name to load.
	 */
	public final void saveToDirectory(final String name, final Object obj) {
		final File path = new File(this.parent, name);
		EncogDirectoryPersistence.saveObject(path, obj);
	}

}
