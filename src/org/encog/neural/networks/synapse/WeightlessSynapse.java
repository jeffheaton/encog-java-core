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
package org.encog.neural.networks.synapse;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeightlessSynapse extends BasicSynapse {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public WeightlessSynapse(Layer fromLayer,Layer toLayer)
	{
		this.setFromLayer(fromLayer);
		this.setToLayer(toLayer);		
	}
	
	public WeightlessSynapse() {
		// TODO Auto-generated constructor stub
	}

	public NeuralData compute(NeuralData input) {
		NeuralData result = new BasicNeuralData(getToNeuronCount());
		// just sum the input
		double sum = 0;	
		for(int i=0;i<input.size();i++)
		{
			sum+=input.getData(i);
		}
		
		for (int i = 0; i < getToNeuronCount(); i++) {
			result.setData(i,sum);
		}
		return result;
	}

	public Matrix getMatrix() {
		return null;
	}

	public int getMatrixSize() {
		return 0;
	}

	public void setMatrix(Matrix matrix) {
		throw new NeuralNetworkError("Can't set the matrix for a WeightlessSynapse");
	}

	public SynapseType getType() {
		return SynapseType.Weighted;
	}
	
	public boolean isTeachable()
	{
		return false;
	}
	
	public Object clone() {
		WeightlessSynapse result = new WeightlessSynapse();
		result.setMatrix(this.getMatrix().clone());
		return result;
	}

}
