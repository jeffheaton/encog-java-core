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
package org.encog.neural.data.basic;

import org.encog.neural.data.NeuralData;

public class BasicNeuralData implements NeuralData {
	private double[] data;
	
	public BasicNeuralData(int size) {
		data = new double[size];
	}
	
	public BasicNeuralData(double d[])
	{
		this(d.length);
		System.arraycopy(d, 0, data, 0, d.length);
	}

	public void setData(double[] data)
	{
		this.data = data;
	}
	
	public void setData(int index,double d)
	{
		this.data[index] = d;
	}
	
	public double[] getData()
	{
		return data;
	}
	
	public double getData(int index)
	{
		return data[index];
	}

	public int size() {
		return data.length;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[BasicNeuralData:");
		for(int i=0;i<this.data.length;i++)
		{
			if( i!=0 )
				builder.append(',');
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}
}
