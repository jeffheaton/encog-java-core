/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.app.analyst.csv.process;

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
