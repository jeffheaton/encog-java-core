/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.freeform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLContext;
import org.encog.ml.MLEncodable;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.freeform.basic.BasicActivationSummationFactory;
import org.encog.neural.freeform.basic.BasicFreeformConnectionFactory;
import org.encog.neural.freeform.basic.BasicFreeformLayerFactory;
import org.encog.neural.freeform.basic.BasicFreeformNeuronFactory;
import org.encog.neural.freeform.factory.FreeformConnectionFactory;
import org.encog.neural.freeform.factory.FreeformLayerFactory;
import org.encog.neural.freeform.factory.FreeformNeuronFactory;
import org.encog.neural.freeform.factory.InputSummationFactory;
import org.encog.neural.freeform.task.ConnectionTask;
import org.encog.neural.freeform.task.NeuronTask;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.EngineArray;
import org.encog.util.obj.ObjectCloner;
import org.encog.util.simple.EncogUtility;

/**
 * Implements a freefrom neural network. A freeform neural network can represent
 * much more advanced structures than the flat networks that the Encog
 * BasicNetwork implements. However, while freeform networks are more advanced
 * than the BasicNetwork, they are also much slower.
 * 
 * Freeform networks allow just about any neuron to be connected to another
 * neuron. You can have neuron layers if you want, but they are not required.
 * 
 */
public class FreeformNetwork extends BasicML implements MLContext, Cloneable,
		MLRegression, MLEncodable, MLResettable, MLClassification, MLError {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct an Elmann recurrent neural network.
	 * 
	 * @param input
	 *            The input count.
	 * @param hidden1
	 *            The hidden count.
	 * @param output
	 *            The output count.
	 * @param af
	 *            The activation function.
	 * @return The newly created network.
	 */
	public static FreeformNetwork createElman(final int input,
			final int hidden1, final int output, final ActivationFunction af) {

		final FreeformNetwork network = new FreeformNetwork();
		final FreeformLayer inputLayer = network.createInputLayer(input);
		final FreeformLayer hiddenLayer1 = network.createLayer(hidden1);
		final FreeformLayer outputLayer = network.createOutputLayer(output);

		network.connectLayers(inputLayer, hiddenLayer1, af, 1.0, false);
		network.connectLayers(hiddenLayer1, outputLayer, af, 1.0, false);
		network.createContext(hiddenLayer1, hiddenLayer1);
		network.reset();

		return network;
	}

	/**
	 * Create a feedforward freeform neural network.
	 * 
	 * @param input
	 *            The input count.
	 * @param hidden1
	 *            The first hidden layer count, zero if none.
	 * @param hidden2
	 *            The second hidden layer count, zero if none.
	 * @param output
	 *            The output count.
	 * @param af
	 *            The activation function.
	 * @return The newly crated network.
	 */
	public static FreeformNetwork createFeedforward(final int input,
			final int hidden1, final int hidden2, final int output,
			final ActivationFunction af) {
		final FreeformNetwork network = new FreeformNetwork();
		FreeformLayer lastLayer = network.createInputLayer(input);
		FreeformLayer currentLayer;

		if (hidden1 > 0) {
			currentLayer = network.createLayer(hidden1);
			network.connectLayers(lastLayer, currentLayer, af, 1.0, false);
			lastLayer = currentLayer;
		}

		if (hidden2 > 0) {
			currentLayer = network.createLayer(hidden2);
			network.connectLayers(lastLayer, currentLayer, af, 1.0, false);
			lastLayer = currentLayer;
		}

		currentLayer = network.createOutputLayer(output);
		network.connectLayers(lastLayer, currentLayer, af, 1.0, false);

		network.reset();

		return network;
	}

	/**
	 * The input layer.
	 */
	private FreeformLayer inputLayer;

	/**
	 * The output layer.
	 */
	private FreeformLayer outputLayer;

	/**
	 * The connection factory.
	 */
	private final FreeformConnectionFactory connectionFactory = new BasicFreeformConnectionFactory();

	/**
	 * The layer factory.
	 */
	private final FreeformLayerFactory layerFactory = new BasicFreeformLayerFactory();

	/**
	 * The neuron factory.
	 */
	private final FreeformNeuronFactory neuronFactory = new BasicFreeformNeuronFactory();

	/**
	 * The input summation factory.
	 */
	private final InputSummationFactory summationFactory = new BasicActivationSummationFactory();

	/**
	 * Default constructor. Typically should not be directly used.
	 */
	public FreeformNetwork() {
	}

	/**
	 * Craete a freeform network from a basic network.
	 * 
	 * @param network
	 *            The basic network to use.
	 */
	public FreeformNetwork(final BasicNetwork network) {

		if (network.getLayerCount() < 2) {
			throw new FreeformNetworkError(
					"The BasicNetwork must have at least two layers to be converted.");
		}

		// handle each layer
		FreeformLayer previousLayer = null;
		FreeformLayer currentLayer;

		for (int currentLayerIndex = 0; currentLayerIndex < network
				.getLayerCount(); currentLayerIndex++) {
			// create the layer
			currentLayer = this.layerFactory.factor();

			// Is this the input layer?
			if (this.inputLayer == null) {
				this.inputLayer = currentLayer;
			}

			// Add the neurons for this layer
			for (int i = 0; i < network.getLayerNeuronCount(currentLayerIndex); i++) {
				// obtain the summation object.
				InputSummation summation = null;

				if (previousLayer != null) {
					summation = this.summationFactory.factor(network
							.getActivation(currentLayerIndex));
				}

				// add the new neuron
				currentLayer.add(this.neuronFactory.factorRegular(summation));
			}

			// Fully connect this layer to previous
			if (previousLayer != null) {
				connectLayersFromBasic(network, currentLayerIndex - 1,
						previousLayer, currentLayerIndex, currentLayer,
						currentLayerIndex, false);
			}

			// Add the bias neuron
			// The bias is added after connections so it has no inputs
			if (network.isLayerBiased(currentLayerIndex)) {
				final FreeformNeuron biasNeuron = this.neuronFactory
						.factorRegular(null);
				biasNeuron.setBias(true);
				biasNeuron.setActivation(network
						.getLayerBiasActivation(currentLayerIndex));
				currentLayer.add(biasNeuron);
			}

			// update previous layer
			previousLayer = currentLayer;
			currentLayer = null;
		}

		// finally, set the output layer.
		this.outputLayer = previousLayer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this, data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int classify(final MLData input) {
		final MLData output = compute(input);
		return EngineArray.maxIndex(output.getData());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearContext() {
		performNeuronTask(new NeuronTask() {
			@Override
			public void task(final FreeformNeuron neuron) {
				if (neuron instanceof FreeformContextNeuron) {
					neuron.setActivation(0);
				}
			}
		});
	}

	/**
	 * Return a clone of this neural network. Including structure, weights and
	 * bias values. This is a deep copy.
	 * 
	 * @return A cloned copy of the neural network.
	 */
	@Override
	public Object clone() {
		final BasicNetwork result = (BasicNetwork) ObjectCloner.deepCopy(this);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLData compute(final MLData input) {

		// Allocate result
		final MLData result = new BasicMLData(this.outputLayer.size());

		// Copy the input
		for (int i = 0; i < input.size(); i++) {
			this.inputLayer.setActivation(i, input.getData(i));
		}

		// Request calculation of outputs
		for (int i = 0; i < this.outputLayer.size(); i++) {
			final FreeformNeuron outputNeuron = this.outputLayer.getNeurons()
					.get(i);
			outputNeuron.performCalculation();
			result.setData(i, outputNeuron.getActivation());
		}

		updateContext();

		return result;
	}

	/**
	 * Connect two layers. These layers will be connected with a TANH activation
	 * function in a non-recurrent way. A bias activation of 1.0 will be used,
	 * if needed.
	 * 
	 * @param source
	 *            The source layer.
	 * @param target
	 *            The target layer.
	 */
	public void connectLayers(final FreeformLayer source,
			final FreeformLayer target) {
		connectLayers(source, target, new ActivationTANH(), 1.0, false);
	}

	/**
	 * Connect two layers.
	 * 
	 * @param source
	 *            The source layer.
	 * @param target
	 *            The target layer.
	 * @param theActivationFunction
	 *            The activation function to use.
	 * @param biasActivation
	 *            The bias activation to use.
	 * @param isRecurrent
	 *            True, if this is a recurrent connection.
	 */
	public void connectLayers(final FreeformLayer source,
			final FreeformLayer target,
			final ActivationFunction theActivationFunction,
			final double biasActivation, final boolean isRecurrent) {

		// create bias, if requested
		if (biasActivation > Encog.DEFAULT_DOUBLE_EQUAL) {
			// does the source already have a bias?
			if (source.hasBias()) {
				throw new FreeformNetworkError(
						"The source layer already has a bias neuron, you cannot create a second.");
			}
			final FreeformNeuron biasNeuron = this.neuronFactory
					.factorRegular(null);
			biasNeuron.setActivation(biasActivation);
			biasNeuron.setBias(true);
			source.add(biasNeuron);
		}

		// create connections
		for (final FreeformNeuron targetNeuron : target.getNeurons()) {
			// create the summation for the target
			InputSummation summation = targetNeuron.getInputSummation();

			// do not create a second input summation
			if (summation == null) {
				summation = this.summationFactory.factor(theActivationFunction);
				targetNeuron.setInputSummation(summation);
			}

			// connect the source neurons to the target neuron
			for (final FreeformNeuron sourceNeuron : source.getNeurons()) {
				final FreeformConnection connection = this.connectionFactory
						.factor(sourceNeuron, targetNeuron);
				sourceNeuron.addOutput(connection);
				targetNeuron.addInput(connection);
			}
		}
	}

	/**
	 * Connect two layers, assume bias activation of 1.0 and non-recurrent
	 * connection.
	 * 
	 * @param source
	 *            The source layer.
	 * @param target
	 *            The target layer.
	 * @param theActivationFunction
	 *            The activation function.
	 */
	public void ConnectLayers(final FreeformLayer source,
			final FreeformLayer target,
			final ActivationFunction theActivationFunction) {
		connectLayers(source, target, theActivationFunction, 1.0, false);
	}

	/**
	 * Connect layers from a BasicNetwork. Used internally only.
	 * 
	 * @param network
	 *            The BasicNetwork.
	 * @param fromLayerIdx
	 *            The from layer index.
	 * @param source
	 *            The from layer.
	 * @param sourceIdx
	 *            The source index.
	 * @param target
	 *            The target.
	 * @param targetIdx
	 *            The target index.
	 * @param isRecurrent
	 *            True, if this is recurrent.
	 */
	private void connectLayersFromBasic(final BasicNetwork network,
			final int fromLayerIdx, final FreeformLayer source,
			final int sourceIdx, final FreeformLayer target,
			final int targetIdx, final boolean isRecurrent) {

		for (int targetNeuronIdx = 0; targetNeuronIdx < target.size(); targetNeuronIdx++) {
			for (int sourceNeuronIdx = 0; sourceNeuronIdx < source.size(); sourceNeuronIdx++) {
				final FreeformNeuron sourceNeuron = source.getNeurons().get(
						sourceNeuronIdx);
				final FreeformNeuron targetNeuron = target.getNeurons().get(
						targetNeuronIdx);

				// neurons with no input (i.e. bias neurons)
				if (targetNeuron.getInputSummation() == null) {
					continue;
				}

				final FreeformConnection connection = this.connectionFactory
						.factor(sourceNeuron, targetNeuron);
				sourceNeuron.addOutput(connection);
				targetNeuron.addInput(connection);
				final double weight = network.getWeight(fromLayerIdx,
						sourceNeuronIdx, targetNeuronIdx);
				connection.setWeight(weight);
			}
		}
	}

	/**
	 * Create a context connection, such as those used by Jordan/Elmann.
	 * 
	 * @param source
	 *            The source layer.
	 * @param target
	 *            The target layer.
	 * @return The newly created context layer.
	 */
	public FreeformLayer createContext(final FreeformLayer source,
			final FreeformLayer target) {
		final double biasActivation = 0.0;
		ActivationFunction activatonFunction = null;

		if (source.getNeurons().get(0).getOutputs().size() < 1) {
			throw new FreeformNetworkError(
					"A layer cannot have a context layer connected if there are no other outbound connections from the source layer.  Please connect the source layer somewhere else first.");
		}

		activatonFunction = source.getNeurons().get(0).getInputSummation()
				.getActivationFunction();

		// first create the context layer
		final FreeformLayer result = this.layerFactory.factor();

		for (int i = 0; i < source.size(); i++) {
			final FreeformNeuron neuron = source.getNeurons().get(i);
			if (neuron.isBias()) {
				final FreeformNeuron biasNeuron = this.neuronFactory
						.factorRegular(null);
				biasNeuron.setBias(true);
				biasNeuron.setActivation(neuron.getActivation());
				result.add(biasNeuron);
			} else {
				final FreeformNeuron contextNeuron = this.neuronFactory
						.factorContext(neuron);
				result.add(contextNeuron);
			}
		}

		// now connect the context layer to the target layer

		connectLayers(result, target, activatonFunction, biasActivation, false);

		return result;
	}

	/**
	 * Create the input layer.
	 * 
	 * @param neuronCount
	 *            The input neuron count.
	 * @return The newly created layer.
	 */
	public FreeformLayer createInputLayer(final int neuronCount) {
		if (neuronCount < 1) {
			throw new FreeformNetworkError(
					"Input layer must have at least one neuron.");
		}
		this.inputLayer = createLayer(neuronCount);
		return this.inputLayer;
	}

	/**
	 * Create a hidden layer.
	 * 
	 * @param neuronCount
	 *            The neuron count.
	 * @return The newly created layer.
	 */
	public FreeformLayer createLayer(final int neuronCount) {
		if (neuronCount < 1) {
			throw new FreeformNetworkError(
					"Layer must have at least one neuron.");
		}

		final FreeformLayer result = this.layerFactory.factor();

		// Add the neurons for this layer
		for (int i = 0; i < neuronCount; i++) {
			result.add(this.neuronFactory.factorRegular(null));
		}

		return result;
	}

	/**
	 * Create the output layer.
	 * 
	 * @param neuronCount
	 *            The neuron count.
	 * @return The newly created output layer.
	 */
	public FreeformLayer createOutputLayer(final int neuronCount) {
		if (neuronCount < 1) {
			throw new FreeformNetworkError(
					"Output layer must have at least one neuron.");
		}
		this.outputLayer = createLayer(neuronCount);
		return this.outputLayer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void decodeFromArray(final double[] encoded) {
		int index = 0;
		final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		final List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();

		// first copy outputs to queue
		for (final FreeformNeuron neuron : this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}

		while (queue.size() > 0) {
			// pop a neuron off the queue
			final FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);

			// find anymore neurons and add them to the queue.
			if (neuron.getInputSummation() != null) {
				for (final FreeformConnection connection : neuron
						.getInputSummation().list()) {
					connection.setWeight(encoded[index++]);
					final FreeformNeuron nextNeuron = connection.getSource();
					if (!visited.contains(nextNeuron)) {
						queue.add(nextNeuron);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int encodedArrayLength() {
		int result = 0;
		final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		final List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();

		// first copy outputs to queue
		for (final FreeformNeuron neuron : this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}

		while (queue.size() > 0) {
			// pop a neuron off the queue
			final FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);

			// find anymore neurons and add them to the queue.
			if (neuron.getInputSummation() != null) {
				for (final FreeformConnection connection : neuron
						.getInputSummation().list()) {
					result++;
					final FreeformNeuron nextNeuron = connection.getSource();
					if (!visited.contains(nextNeuron)) {
						queue.add(nextNeuron);
					}
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void encodeToArray(final double[] encoded) {
		int index = 0;
		final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();
		final List<FreeformNeuron> queue = new ArrayList<FreeformNeuron>();

		// first copy outputs to queue
		for (final FreeformNeuron neuron : this.outputLayer.getNeurons()) {
			queue.add(neuron);
		}

		while (queue.size() > 0) {
			// pop a neuron off the queue
			final FreeformNeuron neuron = queue.get(0);
			queue.remove(0);
			visited.add(neuron);

			// find anymore neurons and add them to the queue.
			if (neuron.getInputSummation() != null) {
				for (final FreeformConnection connection : neuron
						.getInputSummation().list()) {
					encoded[index++] = connection.getWeight();
					final FreeformNeuron nextNeuron = connection.getSource();
					if (!visited.contains(nextNeuron)) {
						queue.add(nextNeuron);
					}
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.inputLayer.sizeNonBias();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOutputCount() {
		return this.outputLayer.sizeNonBias();
	}

	/**
	 * @return The output layer.
	 */
	public FreeformLayer getOutputLayer() {
		return this.outputLayer;
	}

	/**
	 * Perform the specified connection task. This task will be performed over
	 * all connections.
	 * 
	 * @param task
	 *            The connection task.
	 */
	public void performConnectionTask(final ConnectionTask task) {
		final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();

		for (final FreeformNeuron neuron : this.outputLayer.getNeurons()) {
			performConnectionTask(visited, neuron, task);
		}
	}

	/**
	 * Perform the specified connection task.
	 * 
	 * @param visited
	 *            The list of visited neurons.
	 * @param parentNeuron
	 *            The parent neuron.
	 * @param task
	 *            The task.
	 */
	private void performConnectionTask(final Set<FreeformNeuron> visited,
			final FreeformNeuron parentNeuron, final ConnectionTask task) {
		visited.add(parentNeuron);

		// does this neuron have any inputs?
		if (parentNeuron.getInputSummation() != null) {
			// visit the inputs
			for (final FreeformConnection connection : parentNeuron
					.getInputSummation().list()) {
				task.task(connection);
				final FreeformNeuron neuron = connection.getSource();
				// have we already visited this neuron?
				if (!visited.contains(neuron)) {
					performConnectionTask(visited, neuron, task);
				}
			}
		}
	}

	/**
	 * Perform the specified neuron task. This task will be executed over all
	 * neurons.
	 * 
	 * @param task
	 */
	public void performNeuronTask(final NeuronTask task) {
		final Set<FreeformNeuron> visited = new HashSet<FreeformNeuron>();

		for (final FreeformNeuron neuron : this.outputLayer.getNeurons()) {
			performNeuronTask(visited, neuron, task);
		}
	}

	/**
	 * Perform the specified neuron task.
	 * @param visited The visited list.
	 * @param parentNeuron The neuron to start with.
	 * @param task The task to perform.
	 */
	private void performNeuronTask(final Set<FreeformNeuron> visited,
			final FreeformNeuron parentNeuron, final NeuronTask task) {
		visited.add(parentNeuron);
		task.task(parentNeuron);

		// does this neuron have any inputs?
		if (parentNeuron.getInputSummation() != null) {
			// visit the inputs
			for (final FreeformConnection connection : parentNeuron
					.getInputSummation().list()) {
				final FreeformNeuron neuron = connection.getSource();
				// have we already visited this neuron?
				if (!visited.contains(neuron)) {
					performNeuronTask(visited, neuron, task);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		reset((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(final int seed) {
		final ConsistentRandomizer randomizer = new ConsistentRandomizer(-1, 1,
				seed);

		/**
		 * {@inheritDoc}
		 */
		performConnectionTask(new ConnectionTask() {
			@Override
			public void task(final FreeformConnection connection) {
				connection.setWeight(randomizer.nextDouble());
			}
		});
	}

	/**
	 * Allocate temp training space.
	 * @param neuronSize The number of elements to allocate on each neuron.
	 * @param connectionSize The number of elements to allocate on each connection.
	 */
	public void tempTrainingAllocate(final int neuronSize,
			final int connectionSize) {
		performNeuronTask(new NeuronTask() {
			@Override
			public void task(final FreeformNeuron neuron) {
				neuron.allocateTempTraining(neuronSize);
				if (neuron.getInputSummation() != null) {
					for (final FreeformConnection connection : neuron
							.getInputSummation().list()) {
						connection.allocateTempTraining(connectionSize);
					}
				}
			}
		});
	}

	/**
	 * Clear the temp training data.
	 */
	public void tempTrainingClear() {
		performNeuronTask(new NeuronTask() {
			@Override
			public void task(final FreeformNeuron neuron) {
				neuron.clearTempTraining();
				if (neuron.getInputSummation() != null) {
					for (final FreeformConnection connection : neuron
							.getInputSummation().list()) {
						connection.clearTempTraining();
					}
				}
			}
		});
	}

	/**
	 * Update context.
	 */
	public void updateContext() {
		performNeuronTask(new NeuronTask() {
			@Override
			public void task(final FreeformNeuron neuron) {
				neuron.updateContext();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateProperties() {
		// not needed
	}

}
