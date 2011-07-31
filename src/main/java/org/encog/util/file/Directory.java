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
import org.encog.util.logging.EncogLogging;

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
	 * Copy the specified file.
	 * 
	 * @param source
	 *            The file to copy.
	 * @param target
	 *            The target of the copy.
	 */
	public static void copyFile(final File source, final File target) {
		
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try {
			final byte[] buffer = new byte[Directory.BUFFER_SIZE];

			
			in = new FileInputStream(source);
			out = new FileOutputStream(target);

			// perform the copy
			int packetSize = 0;

			while (packetSize != -1) {
				packetSize = in.read(buffer);
				if (packetSize != -1) {
					out.write(buffer, 0, packetSize);
				}
			}			
		} catch (final IOException e) {
			throw new EncogError(e);
		} finally {
			// close the files after the copy
			if( in!=null )
			{
				try {
					in.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
				in = null;
			}
			if( out!=null )
			{
				try {
					out.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
				out = null;
			}
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
					if (!element.delete()) {
						throw new EncogError("Failed to delete: "
								+ element.toString() + "\nFile may be in use.");
					}
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
		BufferedReader reader = null;
		
		try {
			final StringBuffer sb = new StringBuffer(1024);
			reader = new BufferedReader(
					new InputStreamReader(is));

			final char[] chars = new char[Directory.BUFFER_SIZE];
			int numRead = 0;
			while ((numRead = reader.read(chars)) > -1) {
				sb.append(new String(chars, 0, numRead));
			}
			reader.close();

			return sb.toString();
		} catch (final IOException e) {
			throw new EncogError(e);
		} finally {
			if( reader!=null) {
				try {
					reader.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
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
			throw new EncogError(e);
		}
	}

	/**
	 * Private constructor.
	 */
	private Directory() {
	}

}
