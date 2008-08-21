/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
  * See the copyright.txt in the distribution for a full listing of 
  * individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.encog.neural.data.temporal;

import org.encog.neural.activation.ActivationFunction;

public class TemporalDataDescription {
	
	public enum Type
	{
		RAW,
		PERCENT_CHANGE,
		DELTA_CHANGE,
	}
		
	private double low;
	private double high;
	private boolean input;
	private boolean predict;
	private Type type;
	private int index;
	private ActivationFunction activationFunction;
	
	public TemporalDataDescription(ActivationFunction activationFunction,double low,double high,Type type, boolean input,boolean predict)
	{
		this.low = low;
		this.type = type;
		this.high = high;
		this.input = input;
		this.predict = predict;
		this.activationFunction = activationFunction;
	}
	
	public TemporalDataDescription(Type type, boolean input,boolean predict)
	{
		this(null,0,0,type,input,predict);
	}
	
	public TemporalDataDescription(ActivationFunction activationFunction,Type type, boolean input,boolean predict)
	{
		this(activationFunction,0,0,type,input,predict);
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the input
	 */
	public boolean isInput() {
		return input;
	}

	/**
	 * @return the predict
	 */
	public boolean isPredict() {
		return predict;
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

	/**
	 * @return the activationFunction
	 */
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}
	
	
	
}
