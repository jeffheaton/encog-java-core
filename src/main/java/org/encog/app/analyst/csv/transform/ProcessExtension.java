package org.encog.app.analyst.csv.transform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		// TODO Auto-generated method stub
		return null;
	}

	public void processLine(LoadedRow row) {
		// TODO Auto-generated method stub
		
	}

	public void init(ReadCSV csv, int theBackwardWindowSize, int theForwardWindowSize) {
		
		this.forwardWindowSize = theForwardWindowSize;
		this.backwardWindowSize = theBackwardWindowSize;
		
		int i = 0;
		for(String name : csv.getColumnNames() ) {
			map.put(name,i);
		}
	}

}
