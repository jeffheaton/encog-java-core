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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * Used to write an Encog EG/EGA file. EG files are used to hold Encog objects.
 * EGA files are used to hold Encog Analyst scripts.
 * 
 */
public class EncogWriteHelper {
	
	/**
	 * The current large array that we are on.
	 */
	private int largeArrayNumber;

	/**
	 * A quote char.
	 */
	public static final char QUOTE = '\"';
	
	/**
	 * A comma char.
	 */
	public static final char COMMA = ',';

	/**
	 * The file to write to.
	 */
	private final PrintWriter out;
	
	/**
	 * The current line.
	 */
	private final StringBuilder line = new StringBuilder();
	
	/**
	 * The current section.
	 */
	private String currentSection;

	/**
	 * Construct the object.
	 * @param stream The stream to write to.
	 */
	public EncogWriteHelper(final OutputStream stream) {
		this.out = new PrintWriter(stream);
	}

	/**
	 * Add a boolean value as a column.  
	 * @param b The boolean value.
	 */
	public final void addColumn(final boolean b) {
		if (this.line.length() > 0) {
			this.line.append(EncogWriteHelper.COMMA);
		}

		this.line.append(b ? 1 : 0);
	}

	/**
	 * Add a column as a double.
	 * @param d The double to add.
	 */
	public final void addColumn(final double d) {
		if (this.line.length() > 0) {
			this.line.append(EncogWriteHelper.COMMA);
		}

		this.line.append(CSVFormat.ENGLISH.format(d, Encog.DEFAULT_PRECISION));
	}

	/**
	 * Add a column as an integer.
	 * @param i The integer to add.
	 */
	public final void addColumn(final int i) {
		if (this.line.length() > 0) {
			this.line.append(EncogWriteHelper.COMMA);
		}

		this.line.append(i);
	}

	/**
	 * Add a column as a string.
	 * @param str The string to add.
	 */
	public final void addColumn(final String str) {
		if (this.line.length() > 0) {
			this.line.append(EncogWriteHelper.COMMA);
		}

		this.line.append(EncogWriteHelper.QUOTE);
		this.line.append(str);
		this.line.append(EncogWriteHelper.QUOTE);
	}

	/**
	 * Add a list of string columns.
	 * @param cols The columns to add.
	 */
	public final void addColumns(final List<String> cols) {
		for (final String str : cols) {
			addColumn(str);
		}

	}

	/**
	 * Add a line.
	 * @param l The line to add.
	 */
	public final void addLine(final String l) {
		if (this.line.length() > 0) {
			writeLine();
		}
		this.out.println(l);
	}

	/**
	 * Add the specified properties.
	 * @param properties The properties.
	 */
	public final void addProperties(final Map<String, String> properties) {
		for (final String key : properties.keySet()) {
			final String value = properties.get(key);
			this.writeProperty(key, value);
		}
	}

	/**
	 * Add a new section.
	 * @param str The section to add.
	 */
	public final void addSection(final String str) {
		this.currentSection = str;
		this.out.println("[" + str + "]");
	}

	/**
	 * Add a new subsection.
	 * @param str The subsection.
	 */
	public final void addSubSection(final String str) {
		this.out.println("[" + this.currentSection + ":" + str + "]");
		this.largeArrayNumber = 0;
	}

	/**
	 * Flush the file.
	 */
	public final  void flush() {
		this.out.flush();
	}

	/**
	 * @return The current section.
	 */
	public final String getCurrentSection() {
		return this.currentSection;
	}

	/**
	 * Write the specified string.
	 * @param str The string to write.
	 */
	public final void write(final String str) {
		this.out.print(str);
	}

	/**
	 * Write the line.
	 */
	public final void writeLine() {
		this.out.println(this.line.toString());
		this.line.setLength(0);
	}

	/**
	 * Write a property as an activation function.
	 * @param name The name of the property.
	 * @param act The activation function.
	 */
	public final void writeProperty(final String name, 
			final ActivationFunction act) {
		final StringBuilder result = new StringBuilder();
		result.append(act.getClass().getSimpleName());

		for (int i = 0; i < act.getParams().length; i++) {
			result.append('|');
			result.append(CSVFormat.EG_FORMAT.format(act.getParams()[i],
					Encog.DEFAULT_PRECISION));
		}
		writeProperty(name, result.toString());
	}

	/**
	 * Write the property as a boolean.
	 * @param name The name of the property.
	 * @param value The boolean value.
	 */
	public final void writeProperty(final String name, final boolean value) {
		this.out.println(name + "=" + (value ? 't' : 'f'));
	}

	/**
	 * Write a property as a CSV format.
	 * @param name The name of the property.
	 * @param csvFormat The format.
	 */
	public final void writeProperty(final String name, 
				final CSVFormat csvFormat) {
		String fmt;
		if ((csvFormat == CSVFormat.ENGLISH)
				|| (csvFormat == CSVFormat.ENGLISH)
				|| (csvFormat == CSVFormat.DECIMAL_POINT)) {
			fmt = "decpnt";
		} else if (csvFormat == CSVFormat.DECIMAL_COMMA) {
			fmt = "deccomma";
		} else {
			fmt = "decpnt";
		}
		this.out.println(name + "=" + fmt);
	}

	/**
	 * Write the property as a double.
	 * @param name The name of the property.
	 * @param value The value.
	 */
	public final void writeProperty(final String name, final double value) {
		this.out.println(name + "="
				+ CSVFormat.EG_FORMAT.format(value, Encog.DEFAULT_PRECISION));
	}

	/**
	 * Write the property as a double array.
	 * @param name The name of the property.
	 * @param d The double value.
	 */
	public final void writeProperty(final String name, final double[] d) {
		
		if( d.length<2048 ) {
			this.out.print(name);
			this.out.print("=");
			boolean first = true;
			for (int i = 0; i < d.length; i++) {
				if (!first) {
					this.out.print(",");
				}
				this.out.print(CSVFormat.EG_FORMAT.format(d[i],
						Encog.DEFAULT_PRECISION));
				first = false;
			}
			this.out.println();
		} else {
			this.out.print(name);
			this.out.print("=##");
			this.out.println(largeArrayNumber++);
			this.out.print("##double#");
			this.out.println(d.length);
			
			int index = 0;
			
			while(index<d.length) {
				boolean first = true;
				for (int i = 0; (i < 2048) && (index<d.length); i++) {
					if (!first) {
						this.out.print(",");
					} else {
						this.out.print("   ");
					}
					this.out.print(CSVFormat.EG_FORMAT.format(d[index],
							Encog.DEFAULT_PRECISION));
					index++;
					first = false;
				}
				this.out.println();
			}
			this.out.println("##end");
		}
	}

	/**
	 * Write a property as an int value.
	 * @param name The name of the property.
	 * @param value The int value.
	 */
	public final void writeProperty(final String name, final int value) {
		this.out.println(name + "=" + value);
	}

	/**
	 * Write a property as an int array.
	 * @param name The name of the property.
	 * @param array The array.
	 */
	public final void writeProperty(final String name, final int[] array) {
		final StringBuilder result = new StringBuilder();
		NumberList.toListInt(CSVFormat.EG_FORMAT, result, array);
		writeProperty(name, result.toString());

	}

	/**
	 * Write a matrix as a property.
	 * @param name The property name.
	 * @param matrix The matrix.
	 */
	public final void writeProperty(final String name, final Matrix matrix) {
		final StringBuilder result = new StringBuilder();
		result.append(matrix.getRows());
		result.append(',');
		result.append(matrix.getCols());

		for (int row = 0; row < matrix.getRows(); row++) {
			for (int col = 0; col < matrix.getCols(); col++) {
				result.append(',');
				result.append(CSVFormat.EG_FORMAT.format(matrix.get(row, col),
						Encog.DEFAULT_PRECISION));
			}
		}

		writeProperty(name, result.toString());
	}

	/**
	 * Write the property a s string.
	 * @param name The name of the property.
	 * @param value The value.
	 */
	public final void writeProperty(final String name, final String value) {
		this.out.println(name + "=" + value);

	}
}
