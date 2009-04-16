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

package org.encog.neural.networks.training.propagation;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;
import org.encog.util.ErrorCalculation;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements basic functionality that is needed by each of the propagation
 * methods.  The specifics of each of the propagation methods is implemented
 * inside of the PropagationMethod interface implementors.
 * @author jheaton
 *
 */
public class Propagation  extends BasicTraining  {

	/**
	 * THe network that is being trained.
	 */
	private final BasicNetwork network;

	private NeuralData fire;
	
	private final PropagationMethod method;
	
	private final List<PropagationLevel> levels = new ArrayList<PropagationLevel>();

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());


	private final NeuralOutputHolder outputHolder = new NeuralOutputHolder();

	/**
	 * Construct a backpropagation trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Propagation(final BasicNetwork network, final PropagationMethod method,final NeuralDataSet training) {
		this.network = network;
		this.method = method;
		this.method.init(this);
		setTraining(training);
		construct();
	}



	/**
	 * Calculate the error for the recognition just done.
	 * 
	 * @param ideal
	 *            What the output neurons should have yielded.
	 */
	public void backwardPass(final NeuralData ideal) {

		// make sure that the input is of the correct size
		if (ideal.size() != this.network.getOutputLayer().getNeuronCount()) {
			
			String str = "Size mismatch: Can't calcError for ideal input size="
				+ ideal.size() + " for output layer size="
				+ this.network.getOutputLayer().getNeuronCount();
			
			if(logger.isErrorEnabled())
			{
				logger.error(str);
			}
			
			throw new NeuralNetworkError(str);
		}

		// log that we are performing a backward pass
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation backward pass");
		}
		
		// calculate the initial deltas from the output layer
		calculateInitialDeltas(this.fire, ideal);
		
		// now work these errors backward through the neural network
		for(int i=0;i<this.levels.size()-1;i++)
		{
			PropagationLevel fromLevel = this.levels.get(i+1);
			PropagationLevel toLevel = this.levels.get(i);
			this.method.calculateError(this.outputHolder,fromLevel, toLevel);
		}
	}
	
	private void construct()
	{
		// get the output layer
		Layer outputLayer = this.network.getOutputLayer();
		
		// construct the level
		PropagationLevel level = new PropagationLevel(this,outputLayer);
		this.levels.add(level);
		
		// construct the other levels
		construct(level);
	}
	
	private void construct(final PropagationLevel current)
	{
		List<Synapse> previousSynapses = current.determinePreviousSynapses();
		
		// are there more levels to move onto?
		if( previousSynapses.size()>0 )
		{
			PropagationLevel prevLevel = new PropagationLevel(this,previousSynapses);
			this.levels.add(prevLevel);
		

			// continue backward
			construct(prevLevel);
		}
	}

	/**
	 * Get the current best neural network.
	 * 
	 * @return The current best neural network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	private NeuralData forwardPass(NeuralData input) {
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation forward pass");
		}
		this.outputHolder.getResult().clear();
		this.fire = network.compute(input, this.outputHolder);
		return this.fire;
	}

	/**
	 * Perform one iteration of training.
	 * 
	 * Note: if you get a StackOverflowError while training, then you have
	 * endless recurrent loops. Try inserting no trainable synapses on one side
	 * of the loop.
	 */
	public void iteration() {

		if (logger.isInfoEnabled()) {
			logger.info("Beginning propagation iteration");
		}

		preIteration();

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (final NeuralDataPair pair : getTraining()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Backpropagation training on: input={},ideal={}",
						pair.getInput(), pair.getIdeal());
			}
			NeuralData actual = forwardPass(pair.getInput());
			
			errorCalculation.updateError(actual, pair.getIdeal());
			backwardPass(pair.getIdeal());
		}
		
		this.method.learn();

		this.setError(errorCalculation.calculateRMS());

		postIteration();
	}


	

	/**
	 * Calculate the error for the given ideal values.
	 * 
	 * @param ideal
	 *            Ideal output values.
	 */
	private PropagationLevel calculateInitialDeltas(final NeuralData actual,
			final NeuralData ideal) {
		
		// get the output layer
		Layer outputLayer = this.network.getOutputLayer();
		
		// construct the level
		PropagationLevel level = this.levels.get(0);
		
		// obtain the output for each output layer neuron
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			level.setDelta(i, actual.getData(i) );
		}

		// take the derivative of these outputs
		outputLayer.getActivationFunction().derivativeFunction(level.getDeltas());

		// multiply by the difference between the actual and idea
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			level.setDelta(i, level.getDelta(i)*(ideal.getData(i) - actual.getData(i)) );
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Initial deltas: " + DumpMatrix.dumpArray(level.getDeltas()));
		}
		
		return level;
	}

	public NeuralOutputHolder getOutputHolder() {
		return outputHolder;
	}



	public List<PropagationLevel> getLevels() {
		return levels;
	}
	
	
}
