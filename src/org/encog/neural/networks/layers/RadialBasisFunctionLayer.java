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
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.util.math.BoundMath;
import org.encog.util.math.rbf.RadialBasisFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public RadialBasisFunctionLayer(int neuronCount) {
		super(new ActivationSigmoid(), false, neuronCount);
		this.radialBasisFunction = new RadialBasisFunction[neuronCount];
	}
	
	public RadialBasisFunction getRadialBasisFunction(int index)
	{
		return this.radialBasisFunction[index];
	}
	
	public void setRadialBasisFunction(int index,RadialBasisFunction function)
	{
		this.radialBasisFunction[index] = function; 
	}
	
	public void compute(final NeuralData pattern)
	{
        
            for(int i = 0; i < this.getNeuronCount(); i++) {
            	
            	if( this.radialBasisFunction[i]==null)
            	{
            		String str = "Error, must define radial functions for each neuron";
            		if(logger.isErrorEnabled())
            		{
            			logger.error(str);
            		}
            		throw new NeuralNetworkError(str);
            	}
            	
            	RadialBasisFunction f = this.radialBasisFunction[i];
            	
            	double total = 0;
            	for(int j=0;j<pattern.size();j++)
            	{            		
            		total+=f.calculate(pattern.getData(j));
            	}
            	
            	/*
                // perform Gaussian function on pattern                
                // Calculate squared Euclidean distance
                double mySquaredEuclDist = 0;
                double myTemp;
                for(int j = 0; j < pattern.size(); j++) {
                    myTemp = pattern.getData(j) - this.radialBasisFunction[i].calculate(pattern.getData(i));
                    mySquaredEuclDist += (myTemp * myTemp);
                }
                pattern.setData(i, BoundMath.exp(mySquaredEuclDist / 
                    (-2 * this.radialBasisFunction[i].getWidth() * (-2 * this.radialBasisFunction[i].getWidth()))));
            	 */
            }   
	}
}
