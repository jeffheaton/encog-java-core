/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.persist.persistors;

import org.encog.mathutil.matrices.Matrix;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.obj.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains some utilities for persisting objects.
 * 
 * @author jheaton
 */
public final class PersistorUtil {

	/**
	 * The rows in the matrix.
	 */
	public static final String ATTRIBUTE_MATRIX_ROWS = "rows";

	/**
	 * The columns in the matrix.
	 */
	public static final String ATTRIBUTE_MATRIX_COLS = "cols";

	/**
	 * A matrix row.
	 */
	public static final String ROW = "row";

	/**
	 * Write the beginning XML for an Encog object.
	 * 
	 * @param objectType
	 *            The object type to persist.
	 * @param out
	 *            The object that is being persisted.
	 * @param obj
	 *            The XML writer.
	 * @param top
	 *            Is this a top-level object, that needs a name and description?
	 */
	public static void beginEncogObject(final String objectType,
			final WriteXML out, final EncogPersistedObject obj,
			final boolean top) {
		if (top) {
			if (obj.getName() != null) {
				out.addAttribute("name", obj.getName());
			}
			
			if (obj.getDescription() != null) {
				out.addAttribute("description", obj.getDescription());
			} else {
				out.addAttribute("description", "");
			}

		}
		out.addAttribute("native", obj.getClass().getName());
		out.addAttribute("id", "1");
		out.beginTag(objectType);
	}

	/**
	 * Create a persistor object. These objects know how to persist certain
	 * types of classes.
	 * 
	 * @param className
	 *            The name of the class to create a persistor for.
	 * @return The persistor for the specified class.
	 */
	public static Persistor createPersistor(final String className) {
		Persistor persistor;
		
		try {
			// handle any hard coded ones
			if (className.equals("TrainingData")) {
				return new BasicNeuralDataSetPersistor();
			}

			// find using classes
			final String name = className + "Persistor";
			final Class< ? > c = Class.forName("org.encog.persist.persistors."
					+ name);
			persistor = (Persistor) c.newInstance();
			
			return persistor;
		} catch (final ClassNotFoundException e) {
			persistor = null;
		} catch (final InstantiationException e) {
			persistor = null;
		} catch (final IllegalAccessException e) {
			persistor = null;
		}
		
		// try another way
		if (persistor == null) {
			final Class< ? > clazz = ReflectionUtil
					.resolveEncogClass(className);
			
			if( clazz==null) {
				throw new PersistError("Unregistered class: " + className);
			}
			
			EncogPersistedObject temp;
			try {
				temp = (EncogPersistedObject) clazz.newInstance();
			} catch (final InstantiationException e) {
				throw new PersistError(e);
			} catch (final IllegalAccessException e) {
				throw new PersistError(e);
			}
			persistor = temp.createPersistor();
		}
		
		return persistor;
	}

	/**
	 * Load a matrix from the reader.
	 * 
	 * @param in
	 *            The XML reader.
	 * @return The loaded matrix.
	 */
	public static Matrix loadMatrix(final ReadXML in) {
		final int rows = in.getTag().getAttributeInt(
				PersistorUtil.ATTRIBUTE_MATRIX_ROWS);
		final int cols = in.getTag().getAttributeInt(
				PersistorUtil.ATTRIBUTE_MATRIX_COLS);
		final Matrix matrix = new Matrix(rows, cols);

		int row = 0;

		final String end = in.getTag().getName();
		while (in.readToTag()) {
			if (in.is(end, false)) {
				break;
			}
			if (in.is(PersistorUtil.ROW, true)) {
				final String str = in.readTextToTag();
				final double[] d = NumberList
						.fromList(CSVFormat.EG_FORMAT, str);
				for (int col = 0; col < d.length; col++) {
					matrix.set(row, col, d[col]);
				}
				row++;
			}
		}

		return matrix;
	}

	/**
	 * Save the specified matrix.
	 * 
	 * @param matrix
	 *            The matrix to save.
	 * @param out
	 *            The XML writer.
	 */
	public static void saveMatrix(final Matrix matrix, final WriteXML out) {
		out.addAttribute(PersistorUtil.ATTRIBUTE_MATRIX_ROWS, ""
				+ matrix.getRows());
		out.addAttribute(PersistorUtil.ATTRIBUTE_MATRIX_COLS, ""
				+ matrix.getCols());
		out.beginTag("Matrix");

		for (int row = 0; row < matrix.getRows(); row++) {
			final StringBuilder builder = new StringBuilder();

			for (int col = 0; col < matrix.getCols(); col++) {
				if (col > 0) {
					builder.append(',');
				}
				builder.append(matrix.get(row, col));
			}
			out.beginTag(PersistorUtil.ROW);
			out.addText(builder.toString());
			out.endTag();
		}

		out.endTag();
	}

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Private constructor.
	 */
	private PersistorUtil() {
	}

}

