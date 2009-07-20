/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks;

import org.encog.neural.data.NeuralData;

public class NeuralDataMapping {
	
	private NeuralData from;
	private NeuralData to;
	
	public NeuralDataMapping()
	{
		this.from = this.to = null;
	}
	
	public NeuralDataMapping(NeuralData from,NeuralData to)
	{
		this.from = from;
		this.to = to;
	}

	public NeuralData getFrom() {
		return from;
	}

	public void setFrom(NeuralData from) {
		this.from = from;
	}

	public NeuralData getTo() {
		return to;
	}

	public void setTo(NeuralData to) {
		this.to = to;
	}
	
	public static void copy(NeuralDataMapping source, NeuralDataMapping target)
	{
		for(int i=0;i<source.getFrom().size();i++)
		{
			target.getFrom().setData(i,source.getFrom().getData(i));
		}
		
		for(int i=0;i<source.getTo().size();i++)
		{
			target.getTo().setData(i,source.getTo().getData(i));
		}
	}
}
