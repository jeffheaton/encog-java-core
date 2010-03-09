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
import org.encog.solve.genetic.innovation.BasicInnovation;

public class NEATInnovation extends BasicInnovation {

	private final NEATInnovationType innovationType;
	private final NEATNeuronType neuronType;
	private final int fromNeuronID;
	private final int toNeuronID;
	private int neuronID;
	private final double splitX;
	private final double splitY;

	public NEATInnovation(int fromNeuronID, int toNeuronID,
			NEATInnovationType innovationType, int innovationID) {

		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.innovationType = innovationType;
		this.setInnovationID(innovationID);

		this.neuronID = -1;
		this.splitX = 0;
		this.splitY = 0;
		this.neuronType = NEATNeuronType.None;
	}

	public NEATInnovation(NEATNeuronGene neuronGene, int innovationID,
			int neuronID) {

		this.neuronID = neuronID;
		this.setInnovationID( innovationID );
		this.splitX = neuronGene.getSplitX();
		this.splitY = neuronGene.getSplitY();

		this.neuronType = neuronGene.getNeuronType();
		this.innovationType = NEATInnovationType.NewNeuron;
		this.fromNeuronID = -1;
		this.toNeuronID = -1;
	}

	public NEATInnovation(int fromNeuronID, int toNeuronID,
			NEATInnovationType innovationType, int innovationID,
			NEATNeuronType neuronType, double x, double y) {
		this.fromNeuronID = fromNeuronID;
		this.toNeuronID = toNeuronID;
		this.innovationType = innovationType;
		this.setInnovationID( innovationID );
		this.neuronType = neuronType;
		this.splitX = x;
		this.splitY = y;

		this.neuronID = 0;
	}

	public NEATInnovationType getInnovationType() {
		return innovationType;
	}

	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	public int getFromNeuronID() {
		return fromNeuronID;
	}

	public int getToNeuronID() {
		return toNeuronID;
	}

	public int getNeuronID() {
		return neuronID;
	}

	public double getSplitX() {
		return splitX;
	}

	public double getSplitY() {
		return splitY;
	}

	public void setNeuronID(int neuronID) {
		this.neuronID = neuronID;
	}

}
