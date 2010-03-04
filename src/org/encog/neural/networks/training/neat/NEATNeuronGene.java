/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.networks.training.neat;

import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.solve.genetic.genes.BasicGene;
import org.encog.solve.genetic.genes.Gene;

public class NEATNeuronGene extends BasicGene {

	  private int id;
	  private NEATNeuronType neuronType;
	  private boolean recurrent;
	  private double activationResponse;
	  private double splitY; 
	  private double splitX;
	  	
	public NEATNeuronGene(
			NEATNeuronType type,
            int id,
            double splitY,
            double splitX,
            boolean recurrent,
            double act)
	{
		this.neuronType = type;
		this.id = id;
		this.splitX = splitX;
		this.splitY = splitY;
		this.recurrent = recurrent;
		this.activationResponse = act;
	}

	public NEATNeuronGene(NEATNeuronType type,
            int         id,
            double      splitY,
            double      splitX)
	{
		this(type,id,splitY,splitX,false,1.0);
	}
  
	

	public int getId() {
		return id;
	}

	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public double getActivationResponse() {
		return activationResponse;
	}

	public double getSplitY() {
		return splitY;
	}

	public double getSplitX() {
		return splitX;
	}

	public void setNeuronType(NEATNeuronType neuronType) {
		this.neuronType = neuronType;
	}

	public void setActivationResponse(double activationResponse) {
		this.activationResponse = activationResponse;
	}

	public void setSplitY(double splitY) {
		this.splitY = splitY;
	}

	public void setSplitX(double splitX) {
		this.splitX = splitX;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRecurrent(boolean recurrent) {
		this.recurrent = recurrent;
	}

	@Override
	public void copy(Gene gene) {
		NEATNeuronGene other = (NEATNeuronGene)gene;
		this.activationResponse = other.activationResponse;
		this.id = other.id;
		this.neuronType = other.neuronType;
		this.recurrent = other.recurrent;
		this.splitX = other.splitX;
		this.splitY = other.splitY;
		
	}

	public int compareTo(Gene o) {
		return 0;
	}
	
}
