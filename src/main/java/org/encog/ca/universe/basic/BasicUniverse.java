/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ca.universe.basic;

import java.io.Serializable;

import org.encog.ca.CellularAutomataError;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseCell;
import org.encog.ca.universe.UniverseCellFactory;
import org.encog.ml.BasicML;

public class BasicUniverse extends BasicML implements Universe, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ELEMENT_COUNT = "elementCount";
	private final UniverseCell[][] data;
	private final UniverseCellFactory cellFactory;
	
	public BasicUniverse(int height, int width, UniverseCellFactory theCellFactory)
	{
		this.data = new UniverseCell[height][width];
		this.cellFactory = theCellFactory;
		for(int row = 0; row<getRows(); row++) {
			for(int col=0; col<getColumns(); col++) {
				this.data[row][col] = this.cellFactory.factor();
			}
		}
	}
	
	public Object clone() {
		BasicUniverse result = new BasicUniverse(getRows(),getColumns(),this.cellFactory);
		result.copy(this);
		return result;
	}
	
	@Override
	public void copy(Universe source)
	{
		if( !(source instanceof BasicUniverse) ) {
			throw new CellularAutomataError("Can only copy another BasicUniverse");
		}
		for(int row = 0; row<getRows(); row++) {
			for(int col=0; col<getColumns(); col++) {
				this.data[row][col].copy(source.get(row, col));
			}
		}
	}
	
	@Override
	public double compare(Universe otherWorld) {
		if( !(otherWorld instanceof BasicUniverse) ) {
			throw new CellularAutomataError("Can only compare another BasicUniverse");
		}
		
		int result = 0;
		int total = 0;
		for(int row = 0; row<otherWorld.getRows(); row++) {
			for(int col=0; col<otherWorld.getColumns(); col++) {
				int d1 = Math.abs((int)(255*get(row,col).getAvg()));
				int d2 = Math.abs((int)(255*otherWorld.get(row,col).getAvg()));
				if( Math.abs(d1-d2)>10) {
					result++;
				}
				total++;
			}
		}
		
		return (double)result/(double)total;
	}
	
	
	public int getColumns() {
		return this.data[0].length;
	}
	
	public int getRows() {
		return this.data.length;
	}
	
	public void randomize() {
		for (int row = 0; row < getRows(); row++) {
			for (int col = 0; col < getColumns(); col++) {
				for (int i = 0; i < 3; i++) {
					this.data[row][col].randomize();
				}
			}
		}
	}


	public UniverseCell get(int row, int col) {
		return this.data[row][col];
	}

	public boolean isValid(int row, int col) {
		if( row<0 || col<0 || row>=getRows() || col>=getColumns()) {
			return false;
		}
		return true;
	}
	
	

	public UniverseCellFactory getCellFactory() {
		return cellFactory;
	}

	@Override
	public void updateProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double calculatePercentInvalid() {
		int result = 0;
		int total = 0;
		for(int row = 0; row<getRows(); row++) {			
			for(int col=0; col<getColumns(); col++) {
				UniverseCell cell = get(row,col);
				for(int i=0;i<cell.size();i++) {
					if( cell.get(i)<-1 || cell.get(i)>1 ) {
						result++;
					}
					total++;
				}				
			}
		}
		
		return (double)result/(double)total;
	}
}
