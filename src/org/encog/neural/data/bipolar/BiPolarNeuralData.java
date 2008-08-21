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

package org.encog.neural.data.bipolar;

import org.encog.matrix.BiPolarUtil;
import org.encog.neural.data.NeuralData;

public class BiPolarNeuralData implements NeuralData {
	private boolean data[];
	
	public BiPolarNeuralData(int size)
	{
		data = new boolean[size];
	}
	
	public BiPolarNeuralData(boolean[] d)
	{
		data = new boolean[d.length];
		System.arraycopy(d, 0, data, 0, d.length);
	}
	
	public double[] getData() {
		return BiPolarUtil.bipolar2double(data);
	}

	public double getData(int index) {
		return BiPolarUtil.bipolar2double(data[index]);
	}

	public void setData(double[] data) {
		this.data = BiPolarUtil.double2bipolar(data);
		
	}

	public void setData(int index, double d) {
		this.data[index] = BiPolarUtil.double2bipolar(d);		
	}

	public int size() {
		return this.data.length;
	}
	
	
	public void setData(int index,boolean value)
	{
		data[index] = value;
	}

	public boolean getBoolean(int i) {
		return data[i];
	}

}
