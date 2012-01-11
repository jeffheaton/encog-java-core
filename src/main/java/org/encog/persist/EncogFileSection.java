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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.encog.app.analyst.AnalystError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.matrices.Matrix;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * This class is used internally to parse Encog files. A file section is part of
 * a name-value pair file.
 * 
 */
public class EncogFileSection {

	private List<double[]> largeArrays = new ArrayList<double[]>();
	
	/**
	 * Parse an activation function from a string.
	 * @param params The params.
	 * @param name The name of the param to parse.
	 * @return The parsed activation function.
	 */
	public static ActivationFunction parseActivationFunction(
			final Map<String, String> params, final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}

			ActivationFunction af = null;
			final String[] cols = value.split("\\|");

			final String afName = "org.encog.engine.network.activation." + cols[0];
			try {
				final Class<?> clazz = Class.forName(afName);
				af = (ActivationFunction) clazz.newInstance();
			} catch (final ClassNotFoundException e) {
				throw new PersistError(e);
			} catch (final InstantiationException e) {
				throw new PersistError(e);
			} catch (final IllegalAccessException e) {
				throw new PersistError(e);
			}

			for (int i = 0; i < af.getParamNames().length; i++) {
				af.setParam(i, CSVFormat.EG_FORMAT.parse(cols[i + 1]));
			}

			return af;

		} catch (final Exception ex) {
			throw new PersistError(ex);
		}
	}

	/**
	 * Parse a boolean from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed boolean value.
	 */
	public static boolean parseBoolean(final Map<String, String> params,
			final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}

			return value.trim().toLowerCase().charAt(0) == 't';

		} catch (final NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", "
					+ "invalid integer: " + value);
		}
	}

	/**
	 * Parse a double from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed double value.
	 */
	public static double parseDouble(final Map<String, String> params,
			final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}

			return CSVFormat.EG_FORMAT.parse(value);

		} catch (final NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", "
					+ "invalid integer: " + value);
		}
	}

	/**
	 * Parse a double array from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed double array value.
	 */
	public double[] parseDoubleArray(final Map<String, String> params,
			final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}
			
			if( value.startsWith("##") ) {
				int i = Integer.parseInt(value.substring(2));
				return this.largeArrays.get(i);
			} else {
				return NumberList.fromList(CSVFormat.EG_FORMAT, value);
			}

		} catch (final NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", "
					+ "invalid integer: " + value);
		}
	}

	/**
	 * Parse an int from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed int value.
	 */
	public static int parseInt(final Map<String, String> params,
			final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}

			return Integer.parseInt(value);

		} catch (final NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", "
					+ "invalid integer: " + value);
		}
	}

	/**
	 * Parse an int array from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed int array value.
	 */
	public static int[] parseIntArray(final Map<String, String> params,
			final String name) {
		String value = null;
		try {
			value = params.get(name);
			if (value == null) {
				throw new PersistError("Missing property: " + name);
			}

			return NumberList.fromListInt(CSVFormat.EG_FORMAT, value);

		} catch (final NumberFormatException ex) {
			throw new PersistError("Field: " + name + ", "
					+ "invalid integer: " + value);
		}
	}

	/**
	 * Parse a matrix from a name-value collection of params.
	 * @param params The name-value pairs.
	 * @param name The name to parse.
	 * @return The parsed matrix value.
	 */
	public static Matrix parseMatrix(final Map<String, String> params,
			final String name) {

		if (!params.containsKey(name)) {
			throw new PersistError("Missing property: " + name);
		}

		final String line = params.get(name);

		final double[] d = NumberList.fromList(CSVFormat.EG_FORMAT, line);
		final int rows = (int) d[0];
		final int cols = (int) d[1];

		final Matrix result = new Matrix(rows, cols);

		int index = 2;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				result.set(r, c, d[index++]);
			}
		}

		return result;
	}

	/**
	 * Split a delimited string into columns.
	 * @param line THe string to split.
	 * @return The string split.
	 */
	public static List<String> splitColumns(final String line) {
		final List<String> result = new ArrayList<String>();
		final StringTokenizer tok = new StringTokenizer(line, ",");
		while (tok.hasMoreTokens()) {
			String str = tok.nextToken().trim();
			if ((str.length() > 0) && (str.charAt(0) == '\"')) {
				str = str.substring(1);
				if (str.endsWith("\"")) {
					str = str.substring(0, str.length() - 1);
				}
			}
			result.add(str);
		}
		return result;
	}

	/**
	 * The name of this section.
	 */
	private final String sectionName;

	/**
	 * The name of this subsection.
	 */
	private final String subSectionName;

	/**
	 * The lines in this section/subsection.
	 */
	private final List<String> lines = new ArrayList<String>();

	/**
	 * Construct the object.
	 * @param theSectionName The section name.
	 * @param theSubSectionName The sub section name.
	 */
	public EncogFileSection(final String theSectionName,
			final String theSubSectionName) {
		super();
		this.sectionName = theSectionName;
		this.subSectionName = theSubSectionName;
	}

	/**
	 * @return The lines.
	 */
	public final List<String> getLines() {
		return this.lines;
	}

	/**
	 * @return All lines separated by a delimiter.
	 */
	public final String getLinesAsString() {
		final StringBuilder result = new StringBuilder();
		for (final String line : this.lines) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * @return The section name.
	 */
	public final String getSectionName() {
		return this.sectionName;
	}

	/**
	 * @return The section name.
	 */
	public final String getSubSectionName() {
		return this.subSectionName;
	}

	/**
	 * @return The params.
	 */
	public final Map<String, String> parseParams() {
		final Map<String, String> result = new HashMap<String, String>();

		for (String line : this.lines) {
			line = line.trim();
			if (line.length() > 0) {
				final int idx = line.indexOf('=');
				if (idx == -1) {
					throw new AnalystError("Invalid setup item: " + line);
				}
				final String name = line.substring(0, idx).trim();
				final String value = line.substring(idx + 1).trim();

				result.put(name, value);
			}
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" sectionName=");
		result.append(this.sectionName);
		result.append(", subSectionName=");
		result.append(this.subSectionName);
		result.append("]");
		return result.toString();
	}
	
	

	/**
	 * @return the largeArrays
	 */
	public List<double[]> getLargeArrays() {
		return largeArrays;
	}

	/**
	 * @param largeArrays the largeArrays to set
	 */
	public void setLargeArrays(List<double[]> largeArrays) {
		this.largeArrays = largeArrays;
	}
}
