package org.encog.ml.data.versatile.columns;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.util.Format;

/**
 * Defines a column definition.
 */
public class ColumnDefinition {
	/**
	 * The name of the column.
	 */
	private String name;
	
	/**
	 * Tye type of column.
	 */
	private ColumnType dataType;
	
	/**
	 * The observed low in a dataset.
	 */
	private double low;
	
	/**
	 * The observed high in a dataset.
	 */
	private double high;
	
	/**
	 * The observed mean in a dataset.
	 */
	private double mean;
	
	/**
	 * The observed standard deviation in a dataset.
	 */
	private double sd;
	
	/**
	 * The observed count for a catagorical column.
	 */
	private int count;
	
	/**
	 * The index of this column in the dataset.
	 */
	private int index;
	
	/**
	 * The classes of a catagorical.
	 */
	private final List<String> classes = new ArrayList<String>();
	
	/**
	 * The normalization helper.
	 */
	private NormalizationHelper owner;
	
	/**
	 * The column definition.
	 * @param theName The name of the column.
	 * @param theDataType The type of the column.
	 */
	public ColumnDefinition(String theName, ColumnType theDataType) {
		this.name = theName;
		this.dataType = theDataType;
		this.count = -1;
		this.low = this.high = this.mean = this.sd = Double.NaN;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dataType
	 */
	public ColumnType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(ColumnType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @param low the low to set
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @param high the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return the mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * @param mean the mean to set
	 */
	public void setMean(double mean) {
		this.mean = mean;
	}

	/**
	 * @return the sd
	 */
	public double getSd() {
		return sd;
	}

	/**
	 * @param sd the sd to set
	 */
	public void setSd(double sd) {
		this.sd = sd;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Analyze the specified value.
	 * @param value The value to analyze.
	 */
	public void analyze(String value) {
		switch(this.dataType) {
			case continuous:
				analyzeContinuous(value);
				break;
			case ordinal:
				analyzeOrdinal(value);
				break;
			case nominal:
				analyzeNominal(value);
				break;
		}
	}

	/**
	 * Analyze a nominal value.
	 * @param value The value to analyze.
	 */
	private void analyzeNominal(String value) {
		if(!this.classes.contains(value)) {
			this.classes.add(value);
		}
		
	}

	/**
	 * Analyze a nominal value.
	 * @param value The value to analyze.
	 */
	private void analyzeOrdinal(String value) {
		if(!this.classes.contains(value)) {
			throw(new EncogError("You must predefine any ordinal values (in order). Undefined ordinal value: " + value));
		}
	}

	/**
	 * Analyze a nominal value.
	 * @param value The value to analyze.
	 */
	private void analyzeContinuous(String value) {
		double d = this.owner.getFormat().parse(value);
		if( this.count<0) {
			this.low = d;
			this.high = d;
			this.mean = d;
			this.sd = 0;
			this.count = 1;
		} else {
			this.mean = this.mean + d;
			this.low = Math.min(this.low, d);
			this.high = Math.max(this.high, d);
			this.count++;
		}
	}
	
	
	
	/**
	 * @return the classes
	 */
	public List<String> getClasses() {
		return classes;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append("ColumnDefinition:");
		result.append(this.name);
		result.append("(");
		result.append(this.dataType.toString());
		result.append(")");
		if( this.dataType==ColumnType.continuous) {
			result.append(";low=");
			result.append(Format.formatDouble(this.low,6));
			result.append(",high=");
			result.append(Format.formatDouble(this.high,6));
			result.append(",mean=");
			result.append(Format.formatDouble(this.mean,6));
			result.append(",sd=");
			result.append(Format.formatDouble(this.sd,6));
		} else {
			result.append(";");
			result.append(this.classes.toString());
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * Define a class for a catagorical value.
	 * @param str The class to add.
	 */
	public void defineClass(String str) {
		this.classes.add(str);
	}	
	
	/**
	 * Define an array of classes for a catagorical value.
	 * @param str The classes to add.
	 */
	public void defineClass(String[] str) {
		for(String s: str) {
			defineClass(s);
		}
	}

	/**
	 * Set the owner of this class.
	 * @param theOwner The owner of this class.
	 */
	public void setOwner(NormalizationHelper theOwner) {
		this.owner = theOwner;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
