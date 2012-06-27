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
package org.encog.ml.bayesian.bif;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.encog.util.csv.CSVFormat;

/**
 * Holds a BIF definition.
 */
public class BIFDefinition {
	private String forDefinition;
	private final List<String> givenDefinitions = new ArrayList<String>();
	private double[] table;
	
	/**
	 * @return the forDefinition
	 */
	public String getForDefinition() {
		return forDefinition;
	}
	/**
	 * @param forDefinition the forDefinition to set
	 */
	public void setForDefinition(String forDefinition) {
		this.forDefinition = forDefinition;
	}
	/**
	 * @return the probability
	 */
	public double[] getTable() {
		return table;
	}
	/**
	 * @param probability the probability to set
	 */
	public void setTable(String s) {
		
		// parse a space separated list of numbers
		StringTokenizer tok = new StringTokenizer(s);
		List<Double> list = new ArrayList<Double>();
		while(tok.hasMoreTokens()) {
			String str = tok.nextToken();
			// support both radix formats
			if( str.indexOf("," )!=-1 ) {
				list.add(CSVFormat.DECIMAL_COMMA.parse(str));
			} else {
				list.add(CSVFormat.DECIMAL_POINT.parse(str));
			}
		}
		
		// now copy to regular array
		this.table = new double[list.size()];
		for(int i=0;i<this.table.length;i++) {
			this.table[i] = list.get(i);
		}
	}
	/**
	 * @return the givenDefinitions
	 */
	public List<String> getGivenDefinitions() {
		return givenDefinitions;
	}
	public void addGiven(String s) {
		this.givenDefinitions.add(s);
		
	}
}
