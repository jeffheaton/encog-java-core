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
package org.encog.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.encog.EncogError;
import org.encog.bot.BotUtil;
import org.encog.util.logging.EncogLogging;

public class FileUtil {

	public static File addFilenameBase(File filename, String base) {
		String f = getFileName(filename);
		String ext = getFileExt(filename);

		int idx1 = f.lastIndexOf('_');
		int idx2 = f.lastIndexOf(File.separatorChar);

		boolean remove = false;

		if (idx1 != -1) {
			if (idx2 == -1) {
				remove = true;
			} else {
				remove = idx1 > idx2;
			}
		}

		if (remove) {
			f = f.substring(0, idx1);
		}

		return new File(f + base + "." + ext);
	}

	public static String getFileName(File file) {
		String fileName = file.toString();
		int mid = fileName.lastIndexOf(".");
		if (mid == -1) {
			return fileName;
		}
		return fileName.substring(0, mid);
	}

	public static String getFileExt(File file) {
		String fileName = file.toString();
		int mid = fileName.lastIndexOf(".");
		if (mid == -1)
			return "";
		return fileName.substring(mid + 1, fileName.length());
	}

	public static String readFileAsString(File filePath)
			throws java.io.IOException {
		
		StringBuffer fileData = null;
		BufferedReader reader = null;

		try {
			fileData = new StringBuffer(1000);
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			return fileData.toString();
		} finally {
			if( reader!=null ) {
				reader.close();
			}
		}
	}

	public static String readStreamAsString(InputStream is)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	public static String forceExtension(String name, String ext) {
		String b = getFileName(new File(name));
		return b + "." + ext;
	}

	public static void writeFileAsString(File path, String str)
			throws IOException {

		BufferedWriter o = new BufferedWriter(new FileWriter(path));
		o.write(str);
		o.close();
	}

	public static void copy(File source, File target) {
		
		FileOutputStream fos = null;
		InputStream is = null;
		
		try {
			fos = new FileOutputStream(target);
			is = new FileInputStream(source);

			copy(is, fos);

		} catch (final IOException e) {
			throw new EncogError(e);
		} finally {
			if( fos!=null ) {
				try {
					fos.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
			
			if( is!=null ) {
				try {
					is.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
		}
	}

	public static void copy(InputStream is, OutputStream os) {
		try {
			final byte[] buffer = new byte[BotUtil.BUFFER_SIZE];
			int length;
			do {
				length = is.read(buffer);

				if (length > 0) {
					os.write(buffer, 0, length);
				}
			} while (length > 0);
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

	public static void copyResource(String resource, File targetFile) {
		try {
			InputStream is = ResourceInputStream
					.openResourceInputStream(resource);
			OutputStream os = new FileOutputStream(targetFile);
			copy(is, os);
			is.close();
			os.close();
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

}
