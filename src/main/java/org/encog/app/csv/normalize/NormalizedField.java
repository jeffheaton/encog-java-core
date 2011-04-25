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
package org.encog.app.csv.normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.csv.EncogCSVError;
import org.encog.mathutil.Equilateral;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * This object holds the normalization stats for a column. This includes the
 * actual and desired high-low range for this column.
 */
public class NormalizedField {
	
	/**
	 * The actual high from the sample data.
	 */
	private double actualHigh;

	/**
	 * The actual low from the sample data.
	 */
	private double actualLow;

	/**
	 * The desired normalized high.
	 */
	private double normalizedHigh;

	/**
	 * The desired normalized low from the sample data.
	 */
	private double normalizedLow;

	/**
	 * The action that should be taken on this column.
	 */
	private NormalizationAction action;

	/**
	 * The name of this column.
	 */
	private String name;

	/**
	 * The list of classes.
	 */
	private final List<ClassItem> classes = new ArrayList<ClassItem>();

	/**
	 * If equilateral classification is used, this is the Equilateral object.
	 */
	private Equilateral eq;

	/**
	 * Allows the index of a field to be looked up.
	 */
	private final Map<String, Integer> lookup = new HashMap<String, Integer>();

	/**
	 * The owner.
	 */
	private NormalizationStats owner;

	/**
	 * Construct the object with a range of 1 and -1.
	 */
	public NormalizedField() {
		this(1, -1);
	}

	/**
	 * Construct the object.
	 * 
	 * @param theNormalizedHigh
	 *            The normalized high.
	 * @param theNormalizedLow
	 *            The normalized low.
	 */
	public NormalizedField(final double theNormalizedHigh,
			final double theNormalizedLow) {
		this.normalizedHigh = theNormalizedHigh;
		this.normalizedLow = theNormalizedLow;
		this.actualHigh = Double.MIN_VALUE;
		this.actualLow = Double.MAX_VALUE;
		this.action = NormalizationAction.Normalize;
	}

	/**
	 * Construct an object.
	 * 
	 * @param theAction
	 *            The desired action.
	 * @param theName
	 *            The name of this column.
	 */
	public NormalizedField(final NormalizationAction theAction, 
			final String theName) {
		this(theAction, theName, 0, 0, 0, 0);
	}

	/**
	 * Construct the field, with no defaults.
	 * 
	 * @param theAction
	 *            The normalization action to take.
	 * @param theName
	 *            The name of this field.
	 * @param ahigh
	 *            The actual high.
	 * @param alow
	 *            The actual low.
	 * @param nhigh
	 *            The normalized high.
	 * @param nlow
	 *            The normalized low.
	 */
	public NormalizedField(final NormalizationAction theAction, 
			final String theName,
			final double ahigh, final double alow, final double nhigh,
			final double nlow) {
		this.action = theAction;
		this.actualHigh = ahigh;
		this.actualLow = alow;
		this.normalizedHigh = nhigh;
		this.normalizedLow = nlow;
		this.name = theName;
	}

	/**
	 * Construct the object.
	 * @param theName The name of the field.
	 * @param theAction The action of the field.
	 * @param high The high end of the range for the field.
	 * @param low The low end of the range for the field.
	 */
	public NormalizedField(final String theName, 
			final NormalizationAction theAction,
			final double high, final double low) {
		this.name = theName;
		this.action = theAction;
		this.normalizedHigh = high;
		this.normalizedLow = low;
	}

	/**
	 * Analyze the specified value. Adjust min/max as needed. Usually used only
	 * internally.
	 * 
	 * @param d
	 *            The value to analyze.
	 */
	public final void analyze(final double d) {
		this.actualHigh = Math.max(this.actualHigh, d);
		this.actualLow = Math.min(this.actualLow, d);
	}

	/**
	 * Denormalize the specified value.
	 * 
	 * @param value
	 *            The value to normalize.
	 * @return The normalized value.
	 */
	public final double deNormalize(final double value) {
		final double result = ((this.actualLow - this.actualHigh) * value
				- this.normalizedHigh * this.actualLow + this.actualHigh
				* this.normalizedLow)
				/ (this.normalizedLow - this.normalizedHigh);
		return result;
	}

	/**
	 * Determine what class the specified data belongs to.
	 * 
	 * @param data
	 *            The data to analyze.
	 * @return The class the data belongs to.
	 */
	public final ClassItem determineClass(final double[] data) {
		int resultIndex = 0;

		switch (this.action) {
		case Equilateral:
			resultIndex = this.eq.decode(data);
			break;
		case OneOf:
			resultIndex = EngineArray.indexOfLargest(data);
			break;
		case SingleField:
			resultIndex = (int) data[0];
			break;
		default: 
			throw new EncogCSVError("Unknown action: " + action);
		}

		return this.classes.get(resultIndex);
	}

	/**
	 * Encode the class.
	 * 
	 * @param classNumber
	 *            The class number.
	 * @return The encoded class.
	 */
	public final String encode(final int classNumber) {
		switch (this.action) {
		case OneOf:
			return encodeOneOf(classNumber);
		case Equilateral:
			return encodeEquilateral(classNumber);
		case SingleField:
			return encodeSingleField(classNumber);
		default:
			return null;
		}
	}

	/**
	 * Encode the class string.
	 * @param str The class string.
	 * @return A string containing a numeric list of the encode.
	 */
	public final Object encode(final String str) {
		int classNumber = lookup(str);
		if (classNumber == -1) {
			try {
				classNumber = Integer.parseInt(str);
			} catch (final NumberFormatException ex) {
				throw new EncogCSVError("Can't determine class for: " + str);
			}
		}
		return encode(classNumber);
	}

	/**
	 * Perform an equilateral encode.
	 * 
	 * @param classNumber
	 *            The class number.
	 * @return The class to encode.
	 */
	public final String encodeEquilateral(final int classNumber) {
		final StringBuilder result = new StringBuilder();
		final double[] d = this.eq.encode(classNumber);
		NumberList.toList(this.owner.getFormat(), this.owner.getPrecision(),
				result, d);
		return result.toString();
	}

	/**
	 * Encode the headers used by this field.
	 * @return A string containing a comma separated list with the headers.
	 */
	public final String encodeHeaders() {
		final StringBuilder line = new StringBuilder();
		switch (this.action) {
		case SingleField:
			BasicFile.appendSeparator(line, CSVFormat.EG_FORMAT);
			line.append('\"');
			line.append(this.name);
			line.append('\"');
			break;
		case Equilateral:
			for (int i = 0; i < this.classes.size() - 1; i++) {
				BasicFile.appendSeparator(line, CSVFormat.EG_FORMAT);
				line.append('\"');
				line.append(this.name);
				line.append('-');
				line.append(i);
				line.append('\"');
			}
			break;
		case OneOf:
			for (int i = 0; i < this.classes.size(); i++) {
				BasicFile.appendSeparator(line, CSVFormat.EG_FORMAT);
				line.append('\"');
				line.append(this.name);
				line.append('-');
				line.append(i);
				line.append('\"');
			}
			break;
		default:
			return null;
		}
		return line.toString();
	}

	/**
	 * Perform the encoding for "one of".
	 * 
	 * @param classNumber
	 *            The class number.
	 * @return The encoded columns.
	 */
	public final String encodeOneOf(final int classNumber) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < this.classes.size(); i++) {
			if (i > 0) {
				result.append(this.owner.getFormat().getSeparator());
			}

			if (i == classNumber) {
				result.append(this.normalizedHigh);
			} else {
				result.append(this.normalizedLow);
			}
		}
		return result.toString();
	}

	/**
	 * Encode a single field.
	 * 
	 * @param classNumber
	 *            The class number to encode.
	 * @return The encoded columns.
	 */
	public final String encodeSingleField(final int classNumber) {
		final StringBuilder result = new StringBuilder();
		result.append(classNumber);
		return result.toString();
	}

	/**
	 * Fix normalized fields that have a single value for the min/max. Separate
	 * them by 2 units.
	 */
	public final void fixSingleValue() {
		if (this.action == NormalizationAction.Normalize) {
			if (Math.abs(this.actualHigh - this.actualLow) 
					< Encog.DEFAULT_DOUBLE_EQUAL) {
				this.actualHigh += 1;
				this.actualLow -= 1;
			}
		}
	}

	/**
	 * @return The action for the field.
	 */
	public final NormalizationAction getAction() {
		return this.action;
	}

	/**
	 * @return The actual high for the field.
	 */
	public final double getActualHigh() {
		return this.actualHigh;
	}

	/**
	 * @return The actual low for the field.
	 */
	public final double getActualLow() {
		return this.actualLow;
	}

	/**
	 * @return A list of any classes in this field.
	 */
	public final List<ClassItem> getClasses() {
		return this.classes;
	}

	/**
	 * @return Returns the number of columns needed for this classification. The
	 *         number of columns needed will vary, depending on the
	 *         classification method used.
	 */
	public final int getColumnsNeeded() {
		switch (this.action) {
		case Ignore:
			return 0;
		case Equilateral:
			return this.classes.size() - 1;
		case OneOf:
			return this.classes.size();
		default:
			return 1;
		}

	}

	/**
	 * @return The equilateral object used by this class, null if none.
	 */
	public final Equilateral getEq() {
		return this.eq;
	}

	/**
	 * @return The name of the field.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return The normalized high for the field.
	 */
	public final double getNormalizedHigh() {
		return this.normalizedHigh;
	}

	/**
	 * @return The normalized low for the neural network.
	 */
	public final double getNormalizedLow() {
		return this.normalizedLow;
	}

	/**
	 * Init any internal structures.
	 * 
	 * @param theOwner The owner.
	 */
	public final void init(final NormalizationStats theOwner) {

		this.owner = theOwner;
		if (this.action == NormalizationAction.Equilateral) {
			if (this.classes.size() < Equilateral.MIN_EQ) {
				throw new EncogCSVError(
						"There must be at least three classes " 
						+ "to make use of equilateral normalization.");
			}

			this.eq = new Equilateral(this.classes.size(), this.normalizedHigh,
					this.normalizedLow);
		}

		// build lookup map
		for (int i = 0; i < this.classes.size(); i++) {
			this.lookup.put(this.classes.get(i).getName(), this.classes.get(i)
					.getIndex());
		}
	}

	/**
	 * @return Is this field a classify field.
	 */
	public final boolean isClassify() {
		// TODO Auto-generated method stub
		return (this.action == NormalizationAction.Equilateral)
				|| (this.action == NormalizationAction.OneOf)
				|| (this.action == NormalizationAction.SingleField);
	}

	/**
	 * Lookup the specified field.
	 * 
	 * @param str
	 *            The name of the field to lookup.
	 * @return The index of the field, or -1 if not found.
	 */
	public final int lookup(final String str) {
		if (!this.lookup.containsKey(str)) {
			return -1;
		}
		return this.lookup.get(str);
	}

	/**
	 * Make a field to hold a class.  Use a numeric range for class items.
	 * @param theAction The action to take.
	 * @param classFrom The beginning class item.
	 * @param classTo The ending class item.
	 * @param high The output high value.
	 * @param low The output low value.
	 */
	public final void makeClass(final NormalizationAction theAction,
			final int classFrom, final int classTo, final int high,
			final int low) {

		if ((theAction != NormalizationAction.Equilateral)
				&& (theAction != NormalizationAction.OneOf)
				&& (theAction != NormalizationAction.SingleField)) {
			throw new EncogCSVError("Unsupported normalization type");
		}

		this.action = theAction;
		this.classes.clear();
		this.normalizedHigh = high;
		this.normalizedLow = low;
		this.actualHigh = 0;
		this.actualLow = 0;

		int index = 0;
		for (int i = classFrom; i < classTo; i++) {
			this.classes.add(new ClassItem("" + i, index++));
		}

	}

	/**
	 * Create a field that will be used to hold a class.
	 * @param theAction The action for this field.
	 * @param cls The class items.
	 * @param high The output high value.
	 * @param low The output low value.
	 */
	public final void makeClass(final NormalizationAction theAction, 
			final String[] cls,
			final double high, final double low) {
		if ((theAction != NormalizationAction.Equilateral)
				&& (theAction != NormalizationAction.OneOf)
				&& (theAction != NormalizationAction.SingleField)) {
			throw new EncogCSVError("Unsupported normalization type");
		}

		this.action = theAction;
		this.classes.clear();
		this.normalizedHigh = high;
		this.normalizedLow = low;
		this.actualHigh = 0;
		this.actualLow = 0;

		for (int i = 0; i < cls.length; i++) {
			this.classes.add(new ClassItem(cls[i], i));
		}

	}

	/**
	 * Make this a pass-through field.
	 */
	public final void makePassThrough() {
		this.normalizedHigh = 0;
		this.normalizedLow = 0;
		this.actualHigh = 0;
		this.actualLow = 0;
		this.action = NormalizationAction.PassThrough;
	}

	/**
	 * Normalize the specified value.
	 * 
	 * @param value
	 *            The value to normalize.
	 * @return The normalized value.
	 */
	public final double normalize(final double value) {
		return ((value - this.actualLow) / (this.actualHigh - this.actualLow))
				* (this.normalizedHigh - this.normalizedLow)
				+ this.normalizedLow;
	}

	/**
	 * Set the action for the field.
	 * 
	 * @param theAction
	 *            The action for the field.
	 */
	public final void setAction(final NormalizationAction theAction) {
		this.action = theAction;
	}

	/**
	 * Set the actual high for the field.
	 * 
	 * @param theActualHigh
	 *            The actual high for the field.
	 */
	public final void setActualHigh(final double theActualHigh) {
		this.actualHigh = theActualHigh;
	}

	/**
	 * Set the actual low for the field.
	 * 
	 * @param theActualLow
	 *            The theActual low for the field.
	 */
	public final void setActualLow(final double theActualLow) {
		this.actualLow = theActualLow;
	}

	/**
	 * Set the name of the field.
	 * 
	 * @param theName
	 *            The name of the field.
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * Set the normalized high for the field.
	 * 
	 * @param theNormalizedHigh
	 *            The normalized high for the field.
	 */
	public final void setNormalizedHigh(final double theNormalizedHigh) {
		this.normalizedHigh = theNormalizedHigh;
	}

	/**
	 * Set the normalized low for the field.
	 * 
	 * @param theNormalizedLow
	 *            The normalized low for the field.
	 */
	public final void setNormalizedLow(final double theNormalizedLow) {
		this.normalizedLow = theNormalizedLow;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", actualHigh=");
		result.append(this.actualHigh);
		result.append(", actualLow=");
		result.append(this.actualLow);

		result.append("]");
		return result.toString();
	}

}
