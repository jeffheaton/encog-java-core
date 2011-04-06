package org.encog.app.analyst.script.normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.app.quant.normalize.NormalizationStats;
import org.encog.engine.util.EngineArray;
import org.encog.mathutil.Equilateral;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class AnalystField {
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
	private List<ClassItem> classes = new ArrayList<ClassItem>();

	/**
	 * If equilateral classification is used, this is the Equilateral object.
	 */
	private Equilateral eq;

	/**
	 * Allows the index of a field to be looked up.
	 */
	private Map<String, Integer> lookup = new HashMap<String, Integer>();
	
	private EncogAnalyst analyst;

	/**
	 * Construct an object.
	 * @param action The desired action.
	 * @param name The name of this column.
	 */
	public AnalystField(NormalizationAction action, String name) {
		this(action, name, 0, 0, 0, 0);
	}

	/**
	 * Construct the field, with no defaults.
	 * @param action The normalization action to take.
	 * @param name The name of this field.
	 * @param ahigh The actual high.
	 * @param alow The actual low.
	 * @param nhigh The normalized high.
	 * @param nlow The normalized low.
	 */
	public AnalystField(NormalizationAction action, String name,
			double ahigh, double alow, double nhigh, double nlow) {
		this.action = action;
		this.actualHigh = ahigh;
		this.actualLow = alow;
		this.normalizedHigh = nhigh;
		this.normalizedLow = nlow;
		this.name = name;
	}

	/**
	 * Construct the object.
	 * @param normalizedHigh The normalized high.
	 * @param normalizedLow The normalized low.
	 */
	public AnalystField(double normalizedHigh, double normalizedLow) {
		this.normalizedHigh = normalizedHigh;
		this.normalizedLow = normalizedLow;
		this.actualHigh = Double.MIN_VALUE;
		this.actualLow = Double.MAX_VALUE;
		this.action = NormalizationAction.Normalize;
	}

	/**
	 * Construct the object with a range of 1 and -1.
	 */
	public AnalystField() {
		this(1, -1);
	}

	public AnalystField(String name, NormalizationAction action,
			double high, double low) {
		this.name = name;
		this.action = action;
		this.normalizedHigh = high;
		this.normalizedLow = low;
	}

	/**
	 * Make this a pass-through field.
	 */
	public void makePassThrough() {
		this.normalizedHigh = 0;
		this.normalizedLow = 0;
		this.actualHigh = 0;
		this.actualLow = 0;
		this.action = NormalizationAction.PassThrough;
	}

	/**
	 * Analyze the specified value.  Adjust min/max as needed.  Usually used only internally.
	 * @param d The value to analyze.
	 */
	public void analyze(double d) {
		this.actualHigh = Math.max(this.actualHigh, d);
		this.actualLow = Math.min(this.actualLow, d);
	}

	/**
	 * Normalize the specified value.
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	public double normalize(double value) {
		return ((value - actualLow) / (actualHigh - actualLow))
				* (normalizedHigh - normalizedLow) + normalizedLow;
	}

	/**
	 * Denormalize the specified value.
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	public double deNormalize(double value) {
		double result = ((actualLow - actualHigh) * value - normalizedHigh
				* actualLow + actualHigh * normalizedLow)
				/ (normalizedLow - normalizedHigh);
		return result;
	}

	/**
	 * Fix normalized fields that have a single value for the min/max.  Separate them by 2 units.
	 */
	public void fixSingleValue() {
		if (action == NormalizationAction.Normalize) {
			if (Math.abs(actualHigh - actualLow) < Encog.DEFAULT_DOUBLE_EQUAL) {
				actualHigh += 1;
				actualLow -= 1;
			}
		}
	}

	/**
	 * @return The actual high for the field.
	 */
	public double getActualHigh() {
		return actualHigh;
	}

	/**
	 * Set the actual high for the field.
	 * @param actualHigh The actual high for the field.
	 */
	public void setActualHigh(double actualHigh) {
		this.actualHigh = actualHigh;
	}

	/**
	 * @return The actual low for the field.
	 */
	public double getActualLow() {
		return actualLow;
	}

	/**
	 * Set the actual low for the field.
	 * @param actualLow The actual low for the field.
	 */
	public void setActualLow(double actualLow) {
		this.actualLow = actualLow;
	}

	/**
	 * @return The normalized high for the field.
	 */
	public double getNormalizedHigh() {
		return normalizedHigh;
	}

	/**
	 * Set the normalized high for the field.
	 * @param normalizedHigh The normalized high for the field.
	 */
	public void setNormalizedHigh(double normalizedHigh) {
		this.normalizedHigh = normalizedHigh;
	}

	/**
	 * @return The normalized low for the neural network.
	 */
	public double getNormalizedLow() {
		return normalizedLow;
	}

	/**
	 * Set the normalized low for the field.
	 * @param normalizedLow The normalized low for the field.
	 */
	public void setNormalizedLow(double normalizedLow) {
		this.normalizedLow = normalizedLow;
	}

	/**
	 * @return The action for the field.
	 */
	public NormalizationAction getAction() {
		return action;
	}

	/**
	 * Set the action for the field.
	 * @param action The action for the field.
	 */
	public void setAction(NormalizationAction action) {
		this.action = action;
	}

	/**
	 * @return The name of the field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the field.
	 * @param name The name of the field.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String encodeHeaders() {
		StringBuilder line = new StringBuilder();
		switch (this.action) {
		case SingleField:
			BasicFile.appendSeparator(line,CSVFormat.EG_FORMAT);
			line.append('\"');
			line.append(name);
			line.append('\"');
			break;
		case Equilateral:
			for (int i = 0; i < this.classes.size() - 1; i++) {
				BasicFile.appendSeparator(line,CSVFormat.EG_FORMAT);
				line.append('\"');
				line.append(name);
				line.append('-');
				line.append(i);
				line.append('\"');
			}
			break;
		case OneOf:
			for (int i = 0; i < this.classes.size(); i++) {
				BasicFile.appendSeparator(line,CSVFormat.EG_FORMAT);
				line.append('\"');
				line.append(name);
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
	 * @return Returns the number of columns needed for this classification.  The number
	 * of columns needed will vary, depending on the classification method used.
	 */
	public int getColumnsNeeded() {
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
	 * Init any internal structures.
	 * @param owner 
	 */
	public void init(EncogAnalyst analyst) {
		this.analyst = analyst;
		if (this.action == NormalizationAction.Equilateral) {
			if( this.classes.size()<3 ) {
				throw new QuantError("There must be at least three classes to make use of equilateral normalization.");
			}
						
			this.eq = new Equilateral(this.classes.size(), this.normalizedHigh,
					this.normalizedLow);
		}

		// build lookup map
		for (int i = 0; i < this.classes.size(); i++) {
			this.lookup
					.put(classes.get(i).getName(), classes.get(i).getIndex());
		}
	}

	/**
	 * Lookup the specified field.
	 * @param str The name of the field to lookup.
	 * @return The index of the field, or -1 if not found.
	 */
	public int lookup(String str) {
		if (!this.lookup.containsKey(str))
			return -1;
		return this.lookup.get(str);
	}

	/**
	 * Determine what class the specified data belongs to.
	 * @param data The data to analyze.
	 * @return The class the data belongs to.
	 */
	public ClassItem determineClass(double[] data) {
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
		}

		return this.classes.get(resultIndex);
	}

	/**
	 * Perform the encoding for "one of".
	 * @param classNumber The class number.
	 * @return The encoded columns.
	 */
	public String encodeOneOf(int classNumber) {
		StringBuilder result = new StringBuilder();
		CSVFormat outputFormat = this.analyst.getScript().determineOutputFormat();
		for (int i = 0; i < this.classes.size(); i++) {
			
			BasicFile.appendSeparator(result, outputFormat);

			if (i == classNumber) {
				result.append(this.normalizedHigh);
			} else {
				result.append(this.normalizedLow);
			}
		}
		return result.toString();
	}

	/**
	 * Perform an equilateral encode.
	 * @param classNumber The class number.
	 * @return The class to encode.
	 */
	public String encodeEquilateral(int classNumber) {
		StringBuilder result = new StringBuilder();
		double[] d = this.eq.encode(classNumber);
		CSVFormat outputFormat = this.analyst.getScript().determineOutputFormat();
		NumberList.toList(outputFormat, this.analyst.getScript().getPrecision(),
				result, d);
		return result.toString();
	}

	/**
	 * Encode a single field.
	 * @param classNumber The class number to encode.
	 * @return The encoded columns.
	 */
	public String encodeSingleField(int classNumber) {
		StringBuilder result = new StringBuilder();
		result.append(classNumber);
		return result.toString();
	}

	/**
	 * Encode the class.
	 * @param classNumber The class number.
	 * @return The encoded class.
	 */
	public String encode(int classNumber) {
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

	public Object encode(String str) {
		int classNumber = this.lookup(str);
		if (classNumber == -1) {
			try {
				classNumber = Integer.parseInt(str);
			} catch (NumberFormatException ex) {
				throw new QuantError("Can't determine class for: " + str);
			}
		}
		return encode(classNumber);
	}
	
	public void makeClass(NormalizationAction action, int classFrom, int classTo, int high,
			int low) {
		
		if( (action!=NormalizationAction.Equilateral) 
				&& (action!=NormalizationAction.OneOf)
				&& (action!=NormalizationAction.SingleField) ) {
				throw new QuantError("Unsupported normalization type");
			}
			
			this.action = action;
			this.classes.clear();
			this.normalizedHigh = high;
			this.normalizedLow = low;
			this.actualHigh = 0;
			this.actualLow = 0;
			
			int index = 0;
			for(int i = classFrom; i<classTo;i++) {
				this.classes.add(new ClassItem(""+i, index++));
			}
		
	}

	public void makeClass(NormalizationAction action, String[] cls, double high, double low) {
		if( (action!=NormalizationAction.Equilateral) 
			&& (action!=NormalizationAction.OneOf)
			&& (action!=NormalizationAction.SingleField) ) {
			throw new QuantError("Unsupported normalization type");
		}
		
		this.action = action;
		this.classes.clear();
		this.normalizedHigh = high;
		this.normalizedLow = low;
		this.actualHigh = 0;
		this.actualLow = 0;
		
		for(int i = 0; i<cls.length;i++) {
			this.classes.add(new ClassItem(cls[i], i));
		}
		
	}

	public List<ClassItem> getClasses() {
		return classes;
	}

	public Equilateral getEq() {
		return eq;
	}

	public boolean isClassify() {
		// TODO Auto-generated method stub
		return this.action==NormalizationAction.Equilateral 
			|| this.action==NormalizationAction.OneOf
			|| this.action==NormalizationAction.SingleField;
	}

	
	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
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
