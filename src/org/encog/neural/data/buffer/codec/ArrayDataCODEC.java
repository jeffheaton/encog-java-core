/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.neural.data.buffer.codec;

import org.encog.engine.util.EngineArray;

public class ArrayDataCODEC implements DataSetCODEC {

	private int index;
	private int inputSize;
	private int idealSize;
	private double[][] input;
	private double[][] ideal;
	
	public ArrayDataCODEC(double[][] input, double[][] ideal)
	{
		this.input = input;
		this.ideal = ideal;
		this.inputSize = input[0].length;
		this.idealSize = ideal[0].length;
		this.index = 0;
	}
	
	public ArrayDataCODEC() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public int getInputSize() {
		return inputSize;
	}
	
	@Override
	public int getIdealSize() {
		return idealSize;
	}

	@Override
	public boolean read(double[] input, double[] ideal) {
		if( index>=this.input.length )
			return false;
		else
		{
			EngineArray.arrayCopy(this.input[index], input);
			EngineArray.arrayCopy(this.ideal[index], ideal);
			index++;
			return true;
		}
	}

	
	@Override
	public void write(double[] input, double[] ideal) {
		EngineArray.arrayCopy(input,this.input[index]);
		EngineArray.arrayCopy(ideal,this.ideal[index]);
		index++;
	}

	@Override
	public void prepareWrite(int recordCount, int inputSize, int idealSize) {
		this.input = new double[recordCount][inputSize];
		this.ideal = new double[recordCount][idealSize];
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.index = 0;
	}

	public double[][] getInput() {
		return input;
	}

	public double[][] getIdeal() {
		return ideal;
	}

	@Override
	public void prepareRead() {
		// TODO Auto-generated method stub
		
	}

	
	
}
