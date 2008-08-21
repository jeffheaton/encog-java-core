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

public class TemporalPoint implements Comparable<TemporalPoint> {
	private int sequence;
	private double[] data;
			
	public TemporalPoint(int size) {
		this.data = new double[size];
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * @return the data
	 */
	public double[] getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(double[] data) {
		this.data = data;
	}
	
	public int compareTo(TemporalPoint that) {
		if( this.getSequence()==that.getSequence() ) {
			return 0;
		}
		else if( this.getSequence()<that.getSequence() )
			return -1;
		else
			return 1;
	}	
	
	public void setData(int index,double d)
	{
		this.data[index] = d;
	}
	
	public double getData(int index)
	{
		return this.data[index];
	}	
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[TemporalPoint:");
		builder.append("Seq:");
		builder.append(this.sequence);
		builder.append(",Data:");
		for(int i=0;i<this.data.length;i++)
		{
			if( i>0 )
				builder.append(',');
			builder.append(this.data[i]);
		}
		builder.append("]");
		return builder.toString();
	}
	
}
