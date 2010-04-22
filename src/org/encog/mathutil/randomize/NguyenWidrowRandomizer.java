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
          
          Layer previousLayer                       = network.getLayer( BasicNetwork.TAG_INPUT );  
          final long            numOfInputNeurons   = previousLayer.getNeuronCount();
          final Iterator<Layer> iterator            = previousLayer.getNextLayers().iterator();
                        
                
                
          while ( !previousLayer.equals( network.getLayer( BasicNetwork.TAG_OUTPUT ) )&& iterator.hasNext() ) {
              final Layer     current         = iterator.next();
              final double    beta            =  0.7d * Math.pow( (double)current.getNeuronCount() , (double)( 1.0/numOfInputNeurons ) );
              final double    normOfWeight[]  = getNormOfWeight(  previousLayer.getNext() );
              
              for (final Synapse synapse : previousLayer.getNext()) {
                  if ( synapse.getMatrix() != null ) {
                    final Matrix      matrix          = synapse.getMatrix();
                    final double[][]  data            = matrix.getData();
                    
                    for (int col = 0; col < matrix.getCols(); col++) {
                        for (int row = 0; row < matrix.getRows(); row++) {
                            data[row][col] = beta * ( data[row][col]/normOfWeight[col] );
                        }
                        synapse.getToLayer().getThreshold()[col]= beta * (synapse.getToLayer().getThreshold()[col]/normOfWeight[col]);
                    }
                  }    
              }
              
              previousLayer = current;
              
          }
       
    }

     
   /**
    * Matrix' cols corresponds to next neuron count
    * Matrix' rows corresponds to from neuron count.
    * 
    * Normalization of weights correspond the sum of 
    * neuron's weights squared   
    * 
    * @param structure
    * @return
    */
    public double[] getNormOfWeight( List<Synapse>  synapses ){
        
        double[]  normsOfWeight; 
        final Synapse[] synarray =  synapses.toArray( new Synapse[]{} );
        
        /* Synapse matrix should be all the same dimension */
        int cols = synarray[0].getMatrix().getCols();
        normsOfWeight = new double[cols];
        
        for ( int j = 0; j < synarray.length; j++ ) {
            for ( int col = 0; col < cols; col++ ) { 
                double normOfWeight =  0;
                for ( int row = 0; row < synarray[j].getMatrix().getRows(); row++ ) {
                    normOfWeight += Math.pow( synarray[j].getMatrix().getData()[row][col], 2 );  
                }  
                normsOfWeight[col] = Math.sqrt(normOfWeight);
            }
        }
        
        return normsOfWeight;
    }
 
}
