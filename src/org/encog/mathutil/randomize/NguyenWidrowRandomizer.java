/*
 * Encog(tm) Artificial Intelligence Framework v2.3 Java Version
 * http://www.heatonresearch.com/encog/ http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc. For
 * information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.mathutil.randomize;

import java.util.Iterator;
import java.util.List;

import org.encog.EncogError;
import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;

/**
 * Implementation of <i>Nguyen-Widrow</i> weight initialization.
 * 
 * 
 * 
 * @author Stéphan Corriveau
 * 
 */
public class NguyenWidrowRandomizer extends RangeRandomizer implements Randomizer {

    public NguyenWidrowRandomizer( double min, double max ) {
        super( min, max );
    }
    
    
    /**
     * The <i>Nguyen-Widrow</i> initialization algorithm is the following : <br>
     * 1. Initialize all weight of hidden layers with (ranged) random values<br>
     * 2. For each hidden layer<br>
     *      2.1 calculate beta value, 0.7 * Nth(#neurons of input layer) root of #neurons of current layer   <br>    
     *      2.2 for each synapse<br>
     *          2.1.1 for each weight <br>
     *              2.1.2 Adjust weight by dividing by norm of  
     *                    weight for neuron and multiplying by beta value 
     *               
     * 
     * @see org.encog.math.randomize.BasicRandomizer#randomize(org.encog.neural.networks.BasicNetwork)
     */
    @Override
    public final void randomize( BasicNetwork network ) {
        
          super.randomize( network ); 
          
          int neuronCount = 0;
          
          for(Layer layer: network.getStructure().getLayers() )
          {
        	  neuronCount+=layer.getNeuronCount();
          }
          
          Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
          Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
          
          if( inputLayer==null )
        	  throw new EncogError("Must have an input layer for Nguyen-Widrow.");
          
          if( outputLayer==null )
        	  throw new EncogError("Must have an output layer for Nguyen-Widrow.");
          
          int hiddenNeurons = neuronCount-inputLayer.getNeuronCount()-outputLayer.getNeuronCount();
          
          if( hiddenNeurons<1 )
        	  throw new EncogError("Must have hidden neurons for Nguyen-Widrow.");
          
          double beta = 0.7 * Math.pow(hiddenNeurons, 1.0 / inputLayer.getNeuronCount());
          
          for(Synapse synapse: network.getStructure().getSynapses() )
          {
        	  randomize(beta,synapse);
          }
          
       
    }
    
    private void randomize(double beta, Synapse synapse)
    {
    	for (int j = 0; j < synapse.getToNeuronCount(); j++)
        {
            double norm = 0.0;

            // Calculate the Euclidean Norm for the weights
            for (int k = 0; k < synapse.getFromNeuronCount(); k++)
            {
            	double value = synapse.getMatrix().get(k,j);
                norm +=   value*value;
            }
            
            double value = synapse.getToLayer().getThreshold(j);
            norm += value*value;
            norm = Math.sqrt(norm);

            // Rescale the weights using beta and the norm
            for (int k = 0; k < synapse.getFromNeuronCount(); k++)
            {
            	value = synapse.getMatrix().get(k,j);
                synapse.getMatrix().set(k,j, beta * value / norm);
            }
            
            value = synapse.getToLayer().getThreshold(j);
            synapse.getToLayer().setThreshold(j, beta * value / norm);
        }	
    }
}
