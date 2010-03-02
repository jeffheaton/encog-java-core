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

public class NEATLinkGene implements Comparable<NEATLinkGene> {
		
	private final int fromNeuronID;
	private final int toNeuronID;
	private double weight;
	private boolean enabled;
	private final boolean recurrent;
	private final int innovationID;
	

	public NEATLinkGene(
			int fromNeuronID, 
			int toNeuronID, 
			boolean enabled, 
			int innovationID, 
			double weight,
			boolean recurrent) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.enabled = enabled;
		this.innovationID = innovationID;
		this.weight = weight;
		this.recurrent = recurrent;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getFromNeuronID() {
		return fromNeuronID;
	}

	public int getToNeuronID() {
		return toNeuronID;
	}

	public boolean isRecurrent() {
		return recurrent;
	}

	public int getInnovationID() {
		return innovationID;
	}

	public int compareTo(NEATLinkGene other) {
		return( (int)(this.getInnovationID() - other.getInnovationID()) );
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[NEATLinkGene:innov=");
		result.append(this.innovationID);
		result.append(",enabled=");
		result.append(this.enabled);
		result.append(",from=");
		result.append(this.fromNeuronID);
		result.append(",to=");
		result.append(this.toNeuronID);
		result.append("]");
		return result.toString();
	}
	
	
}
