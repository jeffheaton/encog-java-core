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

package org.encog.neural.networks.layers;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicLayerPersistor;
import org.encog.persist.persistors.RadialBasisFunctionLayerPersistor;
import org.encog.util.math.BoundMath;
import org.encog.util.math.rbf.GaussianFunction;
import org.encog.util.math.rbf.RadialBasisFunction;
import org.encog.util.randomize.RangeRandomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This layer type makes use of several radial basis function to scale the 
 * output from this layer.  Each RBF can have a different center, peak, 
 * and width.  Proper selection of these values will greatly impact the
 * success of the layer.  Currently, Encog provides no automated way of
 * determining these values. There is one RBF per neuron.
 * @author jheaton
 *
 */
public class RadialBasisFunctionLayer extends BasicLayer {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 2779781041654829282L;
	private RadialBasisFunction[] radialBasisFunction;

	/**
	 * Default constructor, mainly so the workbench can easily create a default layer.
	 */
	public RadialBasisFunctionLayer()
	{
		this(1);
	}
	
	public RadialBasisFunctionLayer(int neuronCount) {
		super(new ActivationLinear(), false, neuronCount);
		this.radialBasisFunction = new RadialBasisFunction[neuronCount];
	}

	public RadialBasisFunction getRadialBasisFunction(int index) {
		return this.radialBasisFunction[index];
	}

	public void setRadialBasisFunction(int index, RadialBasisFunction function) {
		this.radialBasisFunction[index] = function;
	}
	
	public void randomizeGaussianCentersAndWidths(double min,double max)
	{
		for(int i=0;i<getNeuronCount();i++)
		{
			this.radialBasisFunction[i] = new GaussianFunction(
					RangeRandomizer.randomize(min,max),
					RangeRandomizer.randomize(min,max),
					RangeRandomizer.randomize(min,max));
		}
	}

	public NeuralData compute(final NeuralData pattern) {

		NeuralData result = new BasicNeuralData(this.getNeuronCount());
		
		for (int i = 0; i < this.getNeuronCount(); i++) {

			if (this.radialBasisFunction[i] == null) {
				String str = "Error, must define radial functions for each neuron";
				if (logger.isErrorEnabled()) {
					logger.error(str);
				}
				throw new NeuralNetworkError(str);
			}

			RadialBasisFunction f = this.radialBasisFunction[i];
			double total = 0;
			for(int j=0;j<pattern.size();j++)
			{
				double value = f.calculate(pattern.getData(j)); 
				total+=value*value;
			}
			
			result.setData(i,BoundMath.sqrt(total));

		}

		return result;
	}
	
	public ActivationFunction getActivationFunction()
	{
		String str = "Should never call getActivationFunction on RadialBasisFunctionLayer, this layer has a compound activation function setup.";
		if(logger.isErrorEnabled())
		{
			logger.error(str);
		}
		throw new NeuralNetworkError(str);
	}
	
	/**
	 * Create a persistor for this layer.
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return new RadialBasisFunctionLayerPersistor();
	}
}
