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

package org.encog.neural.networks.synapse.neat;

import java.util.ArrayList;
import java.util.List;

public class NEATNeuron {

	  private final List<NEATLink> inboundLinks = new ArrayList<NEATLink>();
	  private final List<NEATLink> outputboundLinks = new ArrayList<NEATLink>();
	  private final double sumActivation;
	  private double output;
	  private final NEATNeuronType neuronType;
	  private final int neuronID;
	  private final double activationResponse;
	  private final int posX;
	  private final int posY;
	  private final double splitY; 
	  private final double splitX;
	  
	public NEATNeuron(
			NEATNeuronType neuronType, 
			int neuronID, 
			double splitY,
			double splitX, 
			double activationResponse) {
		this.neuronType = neuronType;
		this.neuronID = neuronID;
		this.splitY = splitY;
		this.splitX = splitX;
		this.activationResponse = activationResponse;
		this.posX = 0;
		this.posY = 0;
		this.output = 0;
		this.sumActivation = 0;
	}
	public List<NEATLink> getInboundLinks() {
		return inboundLinks;
	}
	public List<NEATLink> getOutputboundLinks() {
		return outputboundLinks;
	}
	public double getSumActivation() {
		return sumActivation;
	}
	public double getOutput() {
		return output;
	}
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}
	public int getNeuronID() {
		return neuronID;
	}
	public double getActivationResponse() {
		return activationResponse;
	}
	public int getPosX() {
		return posX;
	}
	public int getPosY() {
		return posY;
	}
	public double getSplitY() {
		return splitY;
	}
	public double getSplitX() {
		return splitX;
	}
	public void setOutput(double output) {
		this.output = output;
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[NEATNeuron:id=");
		result.append(this.neuronID);
		result.append(",type=");
		switch(this.neuronType)
		{
			case Input:
				result.append("I");
				break;
			case Output:
				result.append("O");
				break;
			case Bias:
				result.append("B");
				break;
			case Hidden:
				result.append("H");
				break;
		}
		result.append("]");
		return result.toString();
	
	}
	  
}
