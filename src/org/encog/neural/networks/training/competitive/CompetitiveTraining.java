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
package org.encog.neural.networks.training.competitive;

import java.util.Collection;
import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompetitiveTraining extends BasicTraining implements LearningRate {
	
	private NeighborhoodFunction neighborhood;
	private double learningRate;
	private BasicNetwork network;
	private Layer inputLayer;
	private Layer outputLayer;
	private int[] won;
	private Collection<Synapse> synapses;
	private int inputNeuronCount;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public CompetitiveTraining(BasicNetwork network,double learningRate, NeuralDataSet training, NeighborhoodFunction neighborhood)
	{
		this.neighborhood = neighborhood;
		setTraining(training);
		this.learningRate = learningRate;
		this.network = network;
		this.inputLayer = network.getInputLayer();
		this.outputLayer = network.getOutputLayer();
		this.synapses = network.getStructure().getPreviousSynapses(this.outputLayer);
		this.inputNeuronCount = this.inputLayer.getNeuronCount();
		this.setError(0);
		this.won = new int[this.outputLayer.getNeuronCount()];
		
		// set the threshold to zero
		for(Synapse synapse: synapses)
		{		
			Matrix matrix = synapse.getMatrix();
			for(int col = 0; col< matrix.getCols(); col++ )
			{
				matrix.set(matrix.getRows()-1,col,0);
			}
		}
	}

	public BasicNetwork getNetwork() {
		return this.network;
	}

	public void iteration() {
		
		if( logger.isInfoEnabled())
		{
			logger.info("Performing Competitive Training iteration.");
		}
		
		preIteration();
		
		for (int i = 0; i < this.won.length; i++) {
			this.won[i] = 0;
		}
		
		double error = 0;
		
		for(NeuralDataPair pair: getTraining() )
		{
			final NeuralData input = pair.getInput();
			final int best = this.network.winner(input);
			
			double length = 0.0;			

			this.won[best]++;
			for(Synapse synapse: this.synapses )
			{
				Matrix wptr = synapse.getMatrix().getCol(best);
			
				for (int i = 0; i < this.inputNeuronCount; i++) {
					double diff = input.getData(i) - wptr.get(i, 0);
					length += diff * diff;
					double newWeight = adjustWeight(wptr.get(i, 0), input.getData(i),best,i);
					synapse.getMatrix().set(i,best,newWeight);
				}	
			}
			
			if (length > error ) {
				error = length;
			}			
		}
		
		setError(Math.sqrt(error));
		
		postIteration();
	}
	
	private double adjustWeight(double startingWeight,double input, int currentNeuron, int bestNeuron) 
	{
        double wt = startingWeight;
        double vw = input;
        wt += this.neighborhood.function(currentNeuron, bestNeuron, 1.0) * learningRate * (vw - wt);
        return wt;

    }
	
	public NeighborhoodFunction getNeighborhood() {
		return neighborhood;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	public void setLearningRate(double rate) {
		this.learningRate = rate;
	}
	
	

}
