/*
 * Encog Artificial Intelligence Framework v1.x
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
package org.encog.neural.networks.layers;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;

public class Synapse {
	
	private Layer fromLayer;
	private Layer toLayer;


	/**
	 * The weight and threshold matrix.
	 */
	private Matrix matrix;
	
	public Synapse(Layer fromLayer,Layer toLayer)
	{
		this.fromLayer = fromLayer;
		this.toLayer = toLayer;	
		this.matrix = new Matrix(getFromNeuronCount() + 1, getToNeuronCount());		
	}
	
	/**
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
	 */
	public Matrix getMatrix() {
		return this.matrix;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		if (this.matrix == null) {
			return 0;
		}
		return this.matrix.size();
	}
	
	
	public int getFromNeuronCount() {
		return this.fromLayer.getNeuronCount();
	}
	
	public int getToNeuronCount() {
		return this.toLayer.getNeuronCount();
	}
	



	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		this.matrix = matrix;

	}

	public Layer getFromLayer() {
		return fromLayer;
	}




	public void setFromLayer(Layer fromLayer) {
		this.fromLayer = fromLayer;
	}




	public Layer getToLayer() {
		return toLayer;
	}




	public void setToLayer(Layer toLayer) {
		this.toLayer = toLayer;
	}




	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Synapse: from=");
		result.append(this.getFromNeuronCount());
		result.append(",to=");
		result.append(this.getToNeuronCount());
		result.append("]");
		return result.toString();
	}
	
	
}
