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

package org.encog.util.obj;

import java.io.File;

/**
 * Utility to automatically generate the Encog class list file. The Encog
 * classlist file is embedded in the JAR or DLL and provides a quick lookup from
 * the short class name to the fully qualified class name.
 * 
 * This file is needed because reflection can't scan a package for contained
 * classes. So this utility scans source directories and the resulting file is
 * embedded in Encog.
 */
public class GenerateClasslist {

	/**
	 * Run the utility.
	 * 
	 * @param args
	 *            The directory to scan, i.e.
	 *            C:\\shared\\encog-workspace\\encog-core\\src.
	 */
	public static void main(final String[] args) {
		final GenerateClasslist gen = new GenerateClasslist();
		gen.beginScan(new File(args[0]));
	}

	/**
	 * The base path.
	 */
	private String base;

	/**
	 * Scan a directory.
	 * 
	 * @param dir
	 *            The directory to scan.
	 */
	public void beginScan(final File dir) {
		this.base = dir.getAbsolutePath();
		scan(dir);
	}

	/**
	 * Process a Java file.
	 * 
	 * @param file
	 *            The file to process.
	 */
	private void processJAVA(final File file) {
		String name = file.getName();

		// strip off the .java
		int idx = name.indexOf('.');
		name = name.substring(0, idx);

		// strip the name for the directory
		String path = file.getAbsolutePath();
		idx = path.lastIndexOf(File.separatorChar);
		path = path.substring(0, idx);

		// strip off base
		path = path.substring(this.base.length() + 1);

		// insert .'s
		final StringBuilder temp = new StringBuilder(path);
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == File.separatorChar) {
				temp.setCharAt(i, '.');
			}
		}

		System.out.println(temp + "." + name);
	}

	/**
	 * Process a C# file.
	 * 
	 * @param file
	 *            The file to process.
	 */
	private void processCS(final File file) {
		String name = file.getName();

		// strip off the .cs
		int idx = name.indexOf('.');
		name = name.substring(0, idx);

		// strip the name for the directory
		String path = file.getAbsolutePath();
		idx = path.lastIndexOf(File.separatorChar);
		path = path.substring(0, idx);

		// strip off base
		if (path.length() > this.base.length()) {
			path = path.substring(this.base.length() + 1);
		}
		else
			path="";

		// insert .'s
		final StringBuilder temp = new StringBuilder(path);
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == File.separatorChar) {
				temp.setCharAt(i, '.');
			}
		}

		System.out.println("Encog." + temp + "." + name);
	}

	/**
	 * Scan the specified directory.
	 * 
	 * @param dir
	 *            The directory to scan.
	 */
	public void scan(final File dir) {
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (file.isFile()) {
				final String strFile = file.toString();
				if (strFile.endsWith(".java")) {
					processJAVA(file);
				} else if (strFile.endsWith(".cs")) {
					processCS(file);
				}
			} else if (file.isDirectory()) {
				scan(file);
			}
		}
	}
}
