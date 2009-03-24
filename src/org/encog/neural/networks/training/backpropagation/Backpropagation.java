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
package org.encog.neural.networks.training.backpropagation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.util.ErrorCalculation;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backpropagation: This class implements a backpropagation training algorithm
 * for feed forward neural networks. It is used in the same manner as any other
 * training class that implements the Train interface.
 * 
 * Backpropagation is a common neural network training algorithm. It works by
 * analyzing the error of the output of the neural network. Each neuron in the
 * output layer's contribution, according to weight, to this error is
 * determined. These weights are then adjusted to minimize this error. This
 * process continues working its way backwards through the layers of the neural
 * network.
 * 
 * This implementation of the backpropagation algorithm uses both momentum and a
 * learning rate. The learning rate specifies the degree to which the weight
 * matrixes will be modified through each iteration. The momentum specifies how
 * much the previous learning iteration affects the current. To use no momentum
 * at all specify zero.
 */
public class Backpropagation extends BasicTraining implements LearningRate, Momentum {

	/**
	 * The learning rate. This is the degree to which the deltas will affect the
	 * current network.
	 */
	private double learnRate;

	/**
	 * The momentum, this is the degree to which the previous training cycle
	 * affects the current one.
	 */
	private double momentum;

	/**
	 * THe network that is being trained.
	 */
	private final BasicNetwork network;
	
	private NeuralData fire;
	
	/**
	 * The logger to use.
	 */
	final Logger logger = LoggerFactory.getLogger(BasicNetwork.class);

	/**
	 * A map between neural network layers and the corresponding
	 * BackpropagationLayer.
	 */
	private final Map<Synapse, PropagationSynapse> synapseMap = 
		new HashMap<Synapse, PropagationSynapse>();
	
	private final NeuralOutputHolder outputHolder = new NeuralOutputHolder();

	/**
	 * Construct a backpropagation trainer.
	 * @param network The network to train.
	 * @param training The training data to use.
	 * @param learnRate
	 *            The rate at which the weight matrix will be adjusted based on
	 *            learning.
	 * @param momentum
	 *            The influence that previous iteration's training deltas will
	 *            have on the current iteration.
	 */
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training, final double learnRate,
			final double momentum) {
		this.network = network;
		this.learnRate = learnRate;
		this.momentum = momentum;
		setTraining(training);

		for(Synapse synapse: network.getSynapses() )
		{
			if( synapse.isTeachable() )
			{
			PropagationSynapse ps = new PropagationSynapse(synapse); 
			this.synapseMap.put(synapse, ps);
			}
		}
		
	}
	
	public Backpropagation(final BasicNetwork network,
			final NeuralDataSet training)
	{
		this(network,training,0,0);
		addStrategy(new SmartLearningRate());
		addStrategy(new SmartMomentum());
	}

	/**
	 * Calculate the error for the recognition just done.
	 * 
	 * @param ideal
	 *            What the output neurons should have yielded.
	 */
	public void backwardPass(final NeuralData ideal) {

		if (ideal.size() != this.network.getOutputLayer().getNeuronCount()) {
			throw new NeuralNetworkError(
					"Size mismatch: Can't calcError for ideal input size="
							+ ideal.size() + " for output layer size="
							+ this.network.getOutputLayer().getNeuronCount());
		}
		
		if( logger.isDebugEnabled() ) {
			logger.debug("Backpropagation backward pass");
		}

		Layer current = this.network.getOutputLayer();
		double[] backDeltas = this.calculateInitialDelta(this.fire, ideal);
		backwardPass(current,backDeltas);	
	}
	
	private void backwardPass(final Layer current, final double[] backDeltas)
	{	
		if( logger.isDebugEnabled() ) {
			logger.debug("Backpropagation backward pass, layer= {}", current);
		}
		for(Synapse synapse : network.getPreviousSynapses(current))
		{
			
			
			double[] nextDeltas = backDeltas;
			if( synapse.isTeachable() && !network.isOutput(synapse.getFromLayer())) {
				
				NeuralData actual = this.outputHolder.getResult().get(synapse);
				if( logger.isDebugEnabled() ) {
					logger.debug("Backpropagation backward pass, synapse= {}, actual= {}", synapse, actual);
				}
				nextDeltas = getPropagationSynapse(synapse).calcError(synapse.getFromLayer().getActivationFunction(),actual,backDeltas, network.isHidden(synapse.getFromLayer()));
				backwardPass(synapse.getFromLayer(), nextDeltas);
			}
			else
			{
				if( logger.isDebugEnabled() ) {
					logger.debug("Backpropagation backward pass, skip synapse= {}", synapse);
				}
			}
		}
	}

	/**
	 * Get the BackpropagationLayer that corresponds to the specified layer.
	 * 
	 * @param layer
	 *            The specified layer.
	 * @return The BackpropagationLayer that corresponds to the specified layer.
	 */
	public PropagationSynapse getPropagationSynapse(final Synapse synapse) {
		final PropagationSynapse result = this.synapseMap.get(synapse);

		if (result == null) {
			throw new NeuralNetworkError(
					"Layer unknown to backpropagation trainer, "
					+ "was a layer added after training begain?");
		}

		return result;
	}

	/**
	 * Get the current best neural network.
	 * 
	 * @return The current best neural network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}
	
	private NeuralData forwardPass(NeuralData input)
	{
		if( logger.isDebugEnabled() ) {
			logger.debug("Backpropagation forward pass");
		}
		this.outputHolder.getResult().clear();
		this.fire = network.compute(input,this.outputHolder);
		return this.fire;
	}

	/**
	 * Perform one iteration of training.
	 * 
	 * Note: if you get a StackOverflowError while training, then you have endless
	 * recurrant loops.  Try inserting no trainable synapses on one side of the
	 * loop.
	 */
	public void iteration() {
		
		if( logger.isInfoEnabled() ) {
			logger.info("Beginning backpropagation iteration");
		}
		
		preIteration();
		
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		for (final NeuralDataPair pair : getTraining() ) {
			if( logger.isDebugEnabled() ) {
				logger.debug("Backpropagation training on: input={},ideal={}",pair.getInput(),pair.getIdeal());
			}
			NeuralData actual = forwardPass(pair.getInput());
			errorCalculation.updateError(actual, pair.getIdeal());
			backwardPass(pair.getIdeal());
		}		
		learn();

		this.setError(errorCalculation.calculateRMS());
		
		postIteration();
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 */
	public void learn() {
		if( logger.isDebugEnabled() ) {
			logger.debug("Backpropagation learning pass");
		}
		for(PropagationSynapse synapse: this.synapseMap.values() )
		{
			logger.debug("Backpropagation, synapse learning: {}",synapse.getSynapse());
			synapse.learn(this.learnRate, this.momentum);
		}

	}
	
	/**
	 * Calculate the error for the given ideal values.
	 * 
	 * @param ideal
	 *            Ideal output values.
	 */
	public double []calculateInitialDelta(final NeuralData actual, final NeuralData ideal) {
		Layer outputLayer = this.network.getOutputLayer();
		double[] result = new double[outputLayer.getNeuronCount()];	
		// layer errors and deltas for output layer
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			result[i] = actual.getData(i);
		}
		
		outputLayer.getActivationFunction().derivativeFunction(result);
		
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			result[i]*= ideal.getData(i) - actual.getData(i);
		}
		
		if( logger.isTraceEnabled() ) {
			logger.trace("Initial deltas: " + DumpMatrix.dumpArray(result));
		}
		return result;
	}

	public double getLearningRate() {
		return this.learnRate;
	}

	public void setLearningRate(double rate) {
		this.learnRate = rate;		
	}

	public double getMomentum() {
		return this.momentum;
	}

	public void setMomentum(double m) {
		this.momentum = m;		
	}
}
