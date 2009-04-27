/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.util;

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
 * @author jheaton
 */
public final class Directory {

	public static final int BUFFER_SIZE = 1024;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private static Logger LOGGER = LoggerFactory.getLogger(Directory.class);
	
	/**
	 * Private constructor.
	 */
	private Directory() {		
	}
	
	/**
	 * Delete a directory and all children.
	 * @param path The path to delete.
	 * @return True if successful.
	 */
	public static boolean deleteDirectory(final File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	/**
	 * Copy the specified file.
	 * @param source The file to copy.
	 * @param target The target of the copy.
	 */
	public static void copyFile(File source,File target)
	{
		try
		{
			byte[] buffer = new byte[BUFFER_SIZE];
			
			// open the files before the copy
			FileInputStream in = new FileInputStream(source);
			FileOutputStream out = new FileOutputStream(target);
			
			// perform the copy
			int packetSize = 0;
			
			while(packetSize!=-1)
			{
				packetSize = in.read(buffer);
				if( packetSize!=-1 )
				{
					out.write(buffer,0,packetSize);
				}
			}
			
			// close the files after the copy
			in.close();
			out.close();
		}
		catch(IOException e)
		{
			throw new EncogError(e);
		}
	}
	
	public static String readTextFile(String fullPathFilename) {
		try
		{
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fullPathFilename));
				
		char[] chars = new char[1024];
		int numRead = 0;
		while( (numRead = reader.read(chars)) > -1){
			sb.append(new String(chars,0,numRead));	
		}

		reader.close();

		return sb.toString();
		}
		catch(IOException e)
		{
			LOGGER.error("Exception",e);
			throw new EncogError(e);
		}
	}
	
	public static String readStream(InputStream is) {
		try
		{
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				
		char[] chars = new char[1024];
		int numRead = 0;
		while( (numRead = reader.read(chars)) > -1){
			sb.append(new String(chars,0,numRead));	
		}

		reader.close();

		return sb.toString();
		}
		catch(IOException e)
		{
			LOGGER.error("Exception",e);
			throw new EncogError(e);
		}
	}

}
