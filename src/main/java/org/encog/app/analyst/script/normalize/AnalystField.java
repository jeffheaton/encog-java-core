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
package org.encog.app.analyst.script.normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.mathutil.Equilateral;
import org.encog.util.EngineArray;
import org.encog.util.arrayutil.ClassItem;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;

/**
 * Holds a field to be analyzed.
 *
 */
public class AnalystField {
	
	/**
	 * Minimum classes for encode using equilateral.
	 */
	public static final int MIN_EQ_CLASSES = 3;
	
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
	 * True, if this is an output field.
	 */
	private boolean output;
	
	/**
	 * The time slice number.
	 */
	private int timeSlice;

	/**
	 * Construct the object with a range of 1 and -1.
	 */
	public AnalystField() {
		this(1, -1);
	}

	/**
	 * Construct an analyst field.  Works like a C++ copy constructor.  
	 * @param field The field to clone.
	 */
	public AnalystField(final AnalystField field) {
		this.actualHigh = field.actualHigh;
		this.actualLow = field.actualLow;
		this.normalizedHigh = field.normalizedHigh;
		this.normalizedLow = field.normalizedLow;
		this.action = field.action;
		this.name = field.name;
		this.output = field.output;
		this.timeSlice = field.timeSlice;
		fixSingleValue();
	}

	/**
	 * Construct the object.
	 * 
	 * @param theNormalizedHigh
	 *            The normalized high.
	 * @param theNormalizedLow
	 *            The normalized low.
	 */
	public AnalystField(final double theNormalizedHigh, 
				final double theNormalizedLow) {
		this.normalizedHigh = theNormalizedHigh;
		this.normalizedLow = theNormalizedLow;
		this.actualHigh = Double.MIN_VALUE;
		this.actualLow = Double.MAX_VALUE;
		this.action = NormalizationAction.Normalize;
		fixSingleValue();
	}

	/**
	 * Construct an object.
	 * 
	 * @param theAction
	 *            The desired action.
	 * @param theName
	 *            The name of this column.
	 */
	public AnalystField(final NormalizationAction theAction, 
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
	public AnalystField(final NormalizationAction theAction, 
			final String theName,
			final double ahigh, final double alow, final double nhigh,
			final double nlow) {
		this.action = theAction;
		this.actualHigh = ahigh;
		this.actualLow = alow;
		this.normalizedHigh = nhigh;
		this.normalizedLow = nlow;
		this.name = theName;
		fixSingleValue();
	}

	/**
	 * Construct an analyst field to use.
	 * @param theName The name of the field.
	 * @param theAction The action to use.
	 * @param high The high value.
	 * @param low The low value.
	 */
	public AnalystField(final String theName, 
			final NormalizationAction theAction,
			final double high, final double low) {
		this.name = theName;
		this.action = theAction;
		this.normalizedHigh = high;
		this.normalizedLow = low;
		fixSingleValue();
	}

	/**
	 * Add headings for a raw file.
	 * @param line The line to write the raw headings to.
	 * @param prefix The prefix to place.
	 * @param format The format to use.
	 */
	public final void addRawHeadings(final StringBuilder line, 
			final String prefix,
			final CSVFormat format) {
		final int subFields = getColumnsNeeded();

		for (int i = 0; i < subFields; i++) {
			final String str = CSVHeaders.tagColumn(this.name, i,
					this.timeSlice, subFields > 1);
			BasicFile.appendSeparator(line, format);
			line.append('\"');
			if (prefix != null) {
				line.append(prefix);
			}
			line.append(str);
			line.append('\"');
		}
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
		
		// typically caused by a number that should not have been normalized
		// (i.e. normalization or actual range is infinitely small.
		if( Double.isNaN(result) ) {
			return ((this.normalizedHigh-this.normalizedLow)/2)+this.normalizedLow;
		}
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
				throw new AnalystError("Unknown action: " + this.action);
		}

		return this.classes.get(resultIndex);
	}

	/**
	 * Determine the class using part of an array.
	 * @param pos The position to begin.
	 * @param data The array to check.
	 * @return The class item.
	 */
	public final ClassItem determineClass(final int pos, final double[] data) {
		int resultIndex = 0;
		final double[] d = new double[getColumnsNeeded()];
		EngineArray.arrayCopy(data, pos, d, 0, d.length);

		switch (this.action) {
		case Equilateral:
			resultIndex = this.eq.decode(d);
			break;
		case OneOf:
			resultIndex = EngineArray.indexOfLargest(d);
			break;
		case SingleField:
			resultIndex = (int) d[0];
			break;
		default:
			throw new AnalystError("Invalid action: " + this.action);
		}

		if (resultIndex < 0) {
			return null;
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
	public final double[] encode(final int classNumber) {
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
	 * Encode the string to numeric form.
	 * @param str The string to encode.
	 * @return The numeric form.
	 */
	public final double[] encode(final String str) {
		int classNumber = lookup(str);
		if (classNumber == -1) {
			try {
				classNumber = Integer.parseInt(str);
			} catch (final NumberFormatException ex) {
				throw new QuantError("Can't determine class for: " + str);
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
	public final double[] encodeEquilateral(final int classNumber) {
		return this.eq.encode(classNumber);
	}

	/**
	 * Perform the encoding for "one of".
	 * 
	 * @param classNumber
	 *            The class number.
	 * @return The encoded columns.
	 */
	private double[] encodeOneOf(final int classNumber) {
		final double[] result = new double[getColumnsNeeded()];

		for (int i = 0; i < this.classes.size(); i++) {
			if (i == classNumber) {
				result[i] = this.normalizedHigh;
			} else {
				result[i] = this.normalizedLow;
			}
		}
		return result;
	}

	/**
	 * Encode a single field.
	 * 
	 * @param classNumber
	 *            The class number to encode.
	 * @return The encoded columns.
	 */
	private double[] encodeSingleField(final int classNumber) {
		final double[] d = new double[1];
		d[0] = classNumber;
		return d;
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
	 * @return The classes.
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
	 * @return The equilateral utility.
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
	 * @return the timeSlice
	 */
	public final int getTimeSlice() {
		return this.timeSlice;
	}

	/**
	 * Init any internal structures.
	 * 
	 */
	public final void init() {

		if (this.action == NormalizationAction.Equilateral) {
			if (this.classes.size() < MIN_EQ_CLASSES) {
				throw new QuantError(
					"There must be at least three classes to make " 
					+ "use of equilateral normalization.");
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
	 * @return True if this field is classification.
	 */
	public final boolean isClassify() {
		return (this.action == NormalizationAction.Equilateral)
				|| (this.action == NormalizationAction.OneOf)
				|| (this.action == NormalizationAction.SingleField);
	}

	/**
	 * @return Is this field ignored.
	 */
	public final boolean isIgnored() {
		return this.action == NormalizationAction.Ignore;
	}

	/**
	 * @return Is this field input.
	 */
	public final boolean isInput() {
		return !this.output;
	}

	/**
	 * @return Is this field output.
	 */
	public final boolean isOutput() {
		return this.output;
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
	 * Make the classes based on numbers.
	 * @param theAction The action.
	 * @param classFrom The starting class.
	 * @param classTo The ending class.
	 * @param high The high value.
	 * @param low The low value.
	 */
	public final void makeClass(final NormalizationAction theAction,
			final int classFrom, final int classTo, final int high,
			final int low) {

		if ((action != NormalizationAction.Equilateral)
				&& (action != NormalizationAction.OneOf)
				&& (action != NormalizationAction.SingleField)) {
			throw new QuantError("Unsupported normalization type");
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
	 * Make the classes using names.
	 * @param theAction The action to use.
	 * @param cls The class names.
	 * @param high The high value.
	 * @param low The low value.
	 */
	public final void makeClass(final NormalizationAction theAction, 
			final String[] cls,
			final double high, final double low) {
		if ((action != NormalizationAction.Equilateral)
				&& (action != NormalizationAction.OneOf)
				&& (action != NormalizationAction.SingleField)) {
			throw new QuantError("Unsupported normalization type");
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
		double result = ((value - this.actualLow) / (this.actualHigh - this.actualLow))
				* (this.normalizedHigh - this.normalizedLow)
				+ this.normalizedLow;
		
		// typically caused by a number that should not have been normalized
		// (i.e. normalization or actual range is infinitely small.
		if( Double.isNaN(result) ) {
			return ((this.normalizedHigh-this.normalizedLow)/2)+this.normalizedLow;
		}
		return result;
	}

	/**
	 * Set the theAction for the field.
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
	 *            The actual low for the field.
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

	/**
	 * Set if this is an output field.
	 * @param b True, if this is output.
	 */
	public final void setOutput(final boolean b) {
		this.output = b;
	}

	/**
	 * @param theTimeSlice
	 *            the timeSlice to set
	 */
	public final void setTimeSlice(final int theTimeSlice) {
		this.timeSlice = theTimeSlice;
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

	/**
	 * Determine the mode, this is the class item that has the most instances.
	 * @param analyst
	 * @return
	 */
	public int determineMode(EncogAnalyst analyst) {
		if( !this.isClassify() ) {
			throw new AnalystError("Can only calculate the mode for a class.");
		}
		
		DataField df = analyst.getScript().findDataField(this.name);
		AnalystClassItem m = null;
		int result = 0;
		int idx = 0;
		for( AnalystClassItem item: df.getClassMembers() )
		{
			if( m==null || m.getCount()<item.getCount() ) {
				m = item;
				result = idx;
			}
			idx++;
		}
		
		return result;
	}
}
