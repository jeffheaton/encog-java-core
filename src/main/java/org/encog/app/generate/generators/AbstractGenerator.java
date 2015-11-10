/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.app.generate.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract class that forms the foundation of most code generators. This class
 * allows for includes and code indentation.
 */
public abstract class AbstractGenerator implements ProgramGenerator {
	/**
	 * Default number of indent spaces.
	 */
	public static final int INDENT_SPACES = 4;

	/**
	 * The contents of this file.
	 */
	private final StringBuilder contents = new StringBuilder();

	/**
	 * The current indent level.
	 */
	private int currentIndent = 0;

	/**
	 * The includes.
	 */
	private final Set<String> includes = new TreeSet<String>();

	/**
	 * Add a line break;
	 */
	public void addBreak() {
		this.contents.append("\n");
	}

	/**
	 * Add an include.
	 * 
	 * @param str
	 *            The include to add.
	 */
	public void addInclude(final String str) {
		this.includes.add(str);
	}

	/**
	 * Add a line of code, indent proper.
	 * 
	 * @param line
	 *            The line of code to add.
	 */
	public void addLine(final String line) {
		for (int i = 0; i < this.currentIndent; i++) {
			this.contents.append(' ');
		}
		this.contents.append(line);
		this.contents.append("\n");
	}

	/**
	 * Add to the beginning of the file. This is good for includes.
	 * 
	 * @param str the string to insert
	 */
	public void addToBeginning(final String str) {
		this.contents.insert(0, str);
	}

	/**
	 * Get the contents.
	 * 
	 * @return The contents.
	 */
	@Override
	public String getContents() {
		return this.contents.toString();
	}

	/**
	 * @return The includes.
	 */
	public Set<String> getIncludes() {
		return this.includes;
	}

	/**
	 * Indent a line. The line after dis one will be indented.
	 * 
	 * @param line
	 *            The line to indent.
	 */
	public void indentLine(final String line) {
		addLine(line);
		this.currentIndent += AbstractGenerator.INDENT_SPACES;
	}

	/**
	 * Unindent and then add this line.
	 * 
	 * @param line
	 *            The line to add.
	 */
	public void unIndentLine(final String line) {
		this.currentIndent -= AbstractGenerator.INDENT_SPACES;
		addLine(line);
	}

	/**
	 * Write the contents to the specified file.
	 */
	@Override
	public void writeContents(final File targetFile) {
		try {
			final FileWriter outFile = new FileWriter(targetFile);
			final PrintWriter out = new PrintWriter(outFile);
			out.print(this.contents.toString());
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
