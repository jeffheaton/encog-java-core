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
import org.encog.util.csv.ReadCSV;

public class ProcessExtension implements ExpressionExtension {

	private Map<String,Integer> map = new HashMap<String,Integer>();
	private int forwardWindowSize;
	private int backwardWindowSize;
	private int totalWindowSize;
	private List<LoadedRow> data = new ArrayList<LoadedRow>();
	
	@Override
	public ExpressionTreeFunction factorFunction(ExpressionHolder theOwner,
			String theName, List<ExpressionTreeElement> theArgs) {
		if (theName.equals("field")) {
			return new FunctionField(this, theOwner, theArgs);
		} else {
			return null;
		}
	}

	public String getField(String fieldName, int fieldIndex) {
		if( !map.containsKey(fieldName) ) {
			throw new AnalystError("Unknown input field: " + fieldName);
		}
		
		int idx = map.get(fieldName);
		return this.data.get(fieldIndex).getData()[idx];
	}

	public void processLine(LoadedRow row) {
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

}
