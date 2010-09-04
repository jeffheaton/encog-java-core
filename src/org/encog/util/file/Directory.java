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

package org.encog.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.encog.EncogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Directory utilities.
 * 
 * @author jheaton
 */
public final class Directory {

	/**
	 * Default buffer size for read/write operations.
	 */
	public static final int BUFFER_SIZE = 1024;

	/**
	 * The logging object.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Directory.class);

	/**
	 * Copy the specified file.
	 * 
	 * @param source
	 *            The file to copy.
	 * @param target
	 *            The target of the copy.
	 */
	public static void copyFile(final File source, final File target) {
		try {
			final byte[] buffer = new byte[Directory.BUFFER_SIZE];

			// open the files before the copy
			final FileInputStream in = new FileInputStream(source);
			final FileOutputStream out = new FileOutputStream(target);

			// perform the copy
			int packetSize = 0;

			while (packetSize != -1) {
				packetSize = in.read(buffer);
				if (packetSize != -1) {
					out.write(buffer, 0, packetSize);
				}
			}

			// close the files after the copy
			in.close();
			out.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Delete a directory and all children.
	 * 
	 * @param path
	 *            The path to delete.
	 * @return True if successful.
	 */
	public static boolean deleteDirectory(final File path) {
		if (path.exists()) {
			final File[] files = path.listFiles();
			for (final File element : files) {
				if (element.isDirectory()) {
					Directory.deleteDirectory(element);
				} else {
					if( !element.delete() )
						throw new EncogError("Failed to delete: " + element.toString() + "\nFile may be in use." );
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Read the entire contents of a stream into a string.
	 * 
	 * @param is
	 *            The input stream to read from.
	 * @return The string that was read in.
	 */
	public static String readStream(final InputStream is) {
		try {
			final StringBuffer sb = new StringBuffer(1024);
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

			final char[] chars = new char[Directory.BUFFER_SIZE];
			int numRead = 0;
			while ((numRead = reader.read(chars)) > -1) {
				sb.append(new String(chars, 0, numRead));
			}
			reader.close();

			return sb.toString();
		} catch (final IOException e) {
			Directory.LOGGER.error("Exception", e);
			throw new EncogError(e);
		}
	}

	/**
	 * Read the entire contents of a stream into a string.
	 * 
	 * @param filename
	 *            The input stream to read from.
	 * @return The string that was read in.
	 */
	public static String readTextFile(final String filename) {
		try {
			final StringBuffer sb = new StringBuffer(1024);
			final BufferedReader reader = new BufferedReader(new FileReader(
					filename));

			final char[] chars = new char[Directory.BUFFER_SIZE];
			int numRead = 0;
			while ((numRead = reader.read(chars)) > -1) {
				sb.append(new String(chars, 0, numRead));
			}

			reader.close();

			return sb.toString();
		} catch (final IOException e) {
			Directory.LOGGER.error("Exception", e);
			throw new EncogError(e);
		}
	}

	/**
	 * Private constructor.
	 */
	private Directory() {
	}

}
