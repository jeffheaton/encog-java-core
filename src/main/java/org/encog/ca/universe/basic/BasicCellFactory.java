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
