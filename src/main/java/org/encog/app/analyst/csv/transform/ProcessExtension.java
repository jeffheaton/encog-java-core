package org.encog.app.analyst.csv.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.parse.expression.ExpressionHolder;
import org.encog.parse.expression.ExpressionTreeElement;
import org.encog.parse.expression.ExpressionTreeFunction;
import org.encog.parse.expression.extension.ExpressionExtension;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class ProcessExtension implements ExpressionExtension {

	private Map<String,Integer> map = new HashMap<String,Integer>();
	private int forwardWindowSize;
	private int backwardWindowSize;
	private int totalWindowSize;
	private List<LoadedRow> data = new ArrayList<LoadedRow>();
	private final CSVFormat format;
	
	ProcessExtension(CSVFormat theFormat) {
		this.format = theFormat;
	}
	
	@Override
	public ExpressionTreeFunction factorFunction(ExpressionHolder theOwner,
			String theName, List<ExpressionTreeElement> theArgs) {
		if (theName.equals("field")) {
			return new FunctionField(this, theOwner, theArgs);
		} if (theName.equals("fieldmax")) {
			return new FunctionFieldMax(this, theOwner, theArgs);
		} if (theName.equals("fieldmaxpip")) {
			return new FunctionFieldMaxPIP(this, theOwner, theArgs);
		} else {
			return null;
		}
	}

	public String getField(String fieldName, int fieldIndex) {
		if( !map.containsKey(fieldName) ) {
			throw new AnalystError("Unknown input field: " + fieldName);
		}
		
		int idx = map.get(fieldName);
		
		if( fieldIndex>=this.data.size() || fieldIndex<0) {
			throw new AnalystError("The specified temporal index " + fieldIndex + " is out of bounds.  You should probably increase the forward window size.");
		}
		
		return this.data.get(fieldIndex).getData()[idx];
	}

	public void loadRow(LoadedRow row) {
		data.add(0, row);
		if( data.size()>this.totalWindowSize) {
			data.remove(data.size()-1);
		}
	}

	public void init(ReadCSV csv, int theBackwardWindowSize, int theForwardWindowSize) {
		
		this.forwardWindowSize = theForwardWindowSize;
		this.backwardWindowSize = theBackwardWindowSize;
		this.totalWindowSize = this.forwardWindowSize + this.backwardWindowSize + 1;
		
		int i = 0;
		for(String name : csv.getColumnNames() ) {
			map.put(name,i++);
		}
	}

	public boolean isDataReady() {
		return this.data.size()>=this.totalWindowSize;
	}

	public int getForwardWindowSize() {
		return forwardWindowSize;
	}

	public int getBackwardWindowSize() {
		return backwardWindowSize;
	}

	public int getTotalWindowSize() {
		return totalWindowSize;
	}

	public CSVFormat getFormat() {
		return format;
	}
	
	

}
