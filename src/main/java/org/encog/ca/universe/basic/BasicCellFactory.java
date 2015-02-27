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

import org.encog.ca.universe.UniverseCell;
import org.encog.ca.universe.UniverseCellFactory;

public class BasicCellFactory implements UniverseCellFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int size;
	private final double min;
	private final double max;
	private final int elementCount;
	
	public BasicCellFactory(int theSize, double theMin, double theMax)
	{
		this.size = theSize;
		this.min = theMin;
		this.max = theMax;
		this.elementCount = -1;
	}
	
	public BasicCellFactory(int theSize, int theCount)
	{
		this.size = theSize;
		this.min = 0;
		this.max = 0;
		this.elementCount = theCount;
	}
	
	public boolean isContinuous() {
		return this.elementCount == -1;
	}
	
	public boolean isDiscrete() {
		return this.elementCount != -1;
	}
	
	@Override
	public UniverseCell factor() {
		if( isDiscrete() ) {
			return new BasicDiscreteCell(this.size, this.elementCount);
		} else {
			return new BasicContinuousCell(this.size, this.min, this.max);
		}		
	}

	public int size() {
		return size;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public int getElementCount() {
		return elementCount;
	}
	
	

}
