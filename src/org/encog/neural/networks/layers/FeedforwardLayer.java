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
import org.encog.matrix.MatrixMath;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.Layer;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.FeedforwardLayerPersistor;

/**
 * FeedforwardLayer: This class represents one layer in a feed forward neural
 * network. This layer could be input, output, or hidden, depending on its
 * placement inside of the FeedforwardNetwork class.
 * 
 * An activation function can also be specified. Usually all layers in a neural
 * network will use the same activation function. By default this class uses the
 * sigmoid activation function.
 */
public class FeedforwardLayer extends BasicLayer implements
		EncogPersistedObject {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = -3698708039331150031L;

	/**
	 * Construct this layer with a non-default threshold function.
	 * 
	 * @param thresholdFunction
	 *            The threshold function to use.
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public FeedforwardLayer(final ActivationFunction thresholdFunction,
			final int neuronCount) {
		super(neuronCount);
		this.setActivationFunction( thresholdFunction );
	}

	/**
	 * Construct this layer with a sigmoid threshold function.
	 * 
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public FeedforwardLayer(final int neuronCount) {
		this(new ActivationSigmoid(), neuronCount);
	}

	/**
	 * Clone the structure of this layer, but do not copy any matrix data.
	 * 
	 * @return The cloned layer.
	 */
	public FeedforwardLayer cloneStructure() {
		return new FeedforwardLayer(this.getActivationFunction(), getNeuronCount());
	}

	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		
		
		
		if( this.getNext()!=null )
		{
			NeuralData result = new BasicNeuralData(getNext().getToNeuronCount());
			final Matrix inputMatrix = createInputMatrix(pattern);
	
			for (int i = 0; i < getNext().getToNeuronCount(); i++) {
				final Matrix col = getNext().getMatrix().getCol(i);
				final double sum = MatrixMath.dotProduct(col, inputMatrix);
				result.setData(i,sum);
			}
			
			// apply the activation function
			this.getActivationFunction().activationFunction(result.getData());
	
			return result;
		}
		else
		{
			// must be the output layer, so just return it
			return pattern;
		}
	}

	/**
	 * Take a simple double array and turn it into a matrix that can be used to
	 * calculate the results of the input array. Also takes into account the
	 * threshold.
	 * 
	 * @param pattern
	 *            The pattern to create the matrix for.
	 * @return A matrix that represents the input pattern.
	 */
	public static Matrix createInputMatrix(final NeuralData pattern) {
		final Matrix result = new Matrix(1, pattern.size() + 1);
		for (int i = 0; i < pattern.size(); i++) {
			result.set(0, i, pattern.getData(i));
		}

		// add a "fake" first column to the input so that the threshold is
		// always multiplied by one, resulting in it just being added.
		result.set(0, pattern.size(), 1);

		return result;
	}

	/**
	 * Create a persistor for this layer.
	 * @return A persistor.
	 */
	public Persistor createPersistor() {
		return new FeedforwardLayerPersistor();
	}




	/**
	 * @return The string form of the layer.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[FeedforwardLayer: Neuron Count=");
		result.append(getNeuronCount());
		result.append("]");
		return result.toString();
	}

}
