/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.prune;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.BasicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prune a neural network selectively. This class allows you to either add or
 * remove neurons from layers of a neural network. Tools
 * 
 * @author jheaton
 * 
 */
public class PruneSelective {

	/**
	 * 
	 */
	private final BasicNetwork network;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an object prune the neural network.
	 * 
	 * @param network
	 *            The network to prune.
	 */
	public PruneSelective(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Change the neuron count for the network. If the count is increased then a
	 * zero-weighted neuron is added, which will not affect the output of the
	 * neural network. If the neuron count is decreased, then the weakest neuron
	 * will be removed.
	 * 
	 * @param layer
	 *            The layer to adjust.
	 * @param neuronCount
	 *            The new neuron count for this layer.
	 */
	public void changeNeuronCount(final int layer, final int neuronCount) {

		if (neuronCount == 0) {
			throw new NeuralNetworkError("Can't decrease to zero neurons.");
		}

		int currentCount = this.network.getLayerNeuronCount(layer);

		// is there anything to do?
		if (neuronCount == currentCount) {
			return;
		}

		if (neuronCount > currentCount) {
			increaseNeuronCount(layer, neuronCount);
		} else {
			decreaseNeuronCount(layer, neuronCount);
		}
	}

	/**
	 * Internal function to decrease the neuron count of a layer.
	 * 
	 * @param layer
	 *            The layer to affect.
	 * @param neuronCount
	 *            The new neuron count.
	 */
	private void decreaseNeuronCount(final int layer, final int neuronCount) {
		// create an array to hold the least significant neurons, which will be
		// removed

		final int lostNeuronCount = this.network.getLayerNeuronCount(layer)
				- neuronCount;
		final int[] lostNeuron = findWeakestNeurons(layer, lostNeuronCount);

		// finally, actually prune the neurons that the previous steps
		// determined to remove
		for (int i = 0; i < lostNeuronCount; i++) {
			prune(layer, lostNeuron[i] - i);
		}
	}

	/**
	 * Determine the significance of the neuron. The higher the return value,
	 * the more significant the neuron is.
	 * 
	 * @param layer
	 *            The layer to query.
	 * @param neuron
	 *            The neuron to query.
	 * @return How significant is this neuron.
	 */
	public double determineNeuronSignificance(final int layer, final int neuron) {

		network.validateNeuron(layer, neuron);
		
		// calculate the bias significance
		double result = 0;

		// calculate the inbound significance
		if( layer>0 ) {
			int prevLayer = layer-1;
			int prevCount = network.getLayerTotalNeuronCount(prevLayer);
			for(int i=0;i<prevCount;i++) {
				result+=network.getWeight(prevLayer, i, neuron);
			}
		}
		
		// calculate the outbound significance
		if( layer<network.getLayerCount()-1 ) {
			int nextLayer = layer+1;
			int nextCount = network.getLayerNeuronCount(nextLayer);
			for(int i=0;i<nextCount;i++) {
				result+=network.getWeight(layer, neuron, i);
			}
		}

		return Math.abs(result);
	}

	/**
	 * Find the weakest neurons on a layer. Considers both weight and bias.
	 * 
	 * @param layer
	 *            The layer to search.
	 * @param count
	 *            The number of neurons to find.
	 * @return An array of the indexes of the weakest neurons.
	 */
	private int[] findWeakestNeurons(final int layer, final int count) {
		// create an array to hold the least significant neurons, which will be
		// returned
		final double[] lostNeuronSignificance = new double[count];
		final int[] lostNeuron = new int[count];

		// init the potential lost neurons to the first ones, we will find
		// better choices if we can
		for (int i = 0; i < count; i++) {
			lostNeuron[i] = i;
			lostNeuronSignificance[i] = determineNeuronSignificance(layer, i);
		}

		// now loop over the remaining neurons and see if any are better ones to
		// remove
		for (int i = count; i < network.getLayerNeuronCount(layer); i++) {
			final double significance = determineNeuronSignificance(layer, i);

			// is this neuron less significant than one already chosen?
			for (int j = 0; j < count; j++) {
				if (lostNeuronSignificance[j] > significance) {
					lostNeuron[j] = i;
					lostNeuronSignificance[j] = significance;
					break;
				}
			}
		}

		return lostNeuron;
	}

	/**
	 * @return The network that is being processed.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Internal function to increase the neuron count. This will add a
	 * zero-weight neuron to this layer.
	 * 
	 * @param layer
	 *            The layer to increase.
	 * @param neuronCount
	 *            The new neuron count.
	 */
	private void increaseNeuronCount(final int targetLayer, final int neuronCount) {

		// check for errors
		if( targetLayer>this.network.getLayerCount() ) {
			throw new NeuralNetworkError("Invalid layer " + targetLayer);
		}
		
		if( neuronCount<=0 ) {
			throw new NeuralNetworkError("Invalid neuron count " + neuronCount);
		}
		
		int increaseBy = neuronCount - network.getLayerNeuronCount(targetLayer);
		
		if( increaseBy<=0 ) {
			throw new NeuralNetworkError("New neuron count is either a decrease or no change: " + neuronCount);
		}
		
		// access the flat network
		FlatNetwork flat = this.network.getStructure().getFlat();
		double[] oldWeights = flat.getWeights();

		// first find out how many connections there will be after this prune.
		int connections = oldWeights.length;
		int inBoundConnections = 0;
		int outBoundConnections = 0;

		// are connections added from the previous layer?
		if (targetLayer > 0) {
			inBoundConnections = this.network
					.getLayerTotalNeuronCount(targetLayer - 1);
			connections += inBoundConnections * increaseBy;
		}

		// are there connections added from the next layer?
		if (targetLayer < (this.network.getLayerCount() - 1)) {
			outBoundConnections = this.network
					.getLayerNeuronCount(targetLayer + 1);
			connections += outBoundConnections * increaseBy;
		}
		
		// increase layer count
		int flatLayer = network.getLayerCount() - targetLayer - 1;
		flat.getLayerCounts()[flatLayer]+=increaseBy;
		flat.getLayerFeedCounts()[flatLayer]+=increaseBy;

		// allocate new weights now that we know how big the new weights will be
		double[] newWeights = new double[connections];

		// construct the new weights
		int weightsIndex = 0;

		for (int fromLayer = flat.getLayerCounts().length - 2; fromLayer >= 0; fromLayer--) {
			int fromNeuronCount = network.getLayerTotalNeuronCount(fromLayer);
			int toNeuronCount = network.getLayerNeuronCount(fromLayer + 1);
			int toLayer = fromLayer + 1;

			for (int toNeuron = 0; toNeuron < toNeuronCount; toNeuron++) {
				for (int fromNeuron = 0; fromNeuron < fromNeuronCount; fromNeuron++) {
					newWeights[weightsIndex++] = this.network.getWeight(
						fromLayer, fromNeuron, toNeuron);					
				}
			}
		}

		// swap in the new weights
		flat.setWeights(newWeights);

		// reindex
		reindexNetwork();		
	}

	/**
	 * Prune one of the neurons from this layer. Remove all entries in this
	 * weight matrix and other layers.
	 * 
	 * @param targetLayer
	 *            The neuron to prune. Zero specifies the first neuron.
	 * @param neuron
	 *            The neuron to prune.
	 */
	public void prune(final int targetLayer, final int neuron) {
		// check for errors
		network.validateNeuron(targetLayer, neuron);
		
		// don't empty a layer
		if( this.network.getLayerNeuronCount(targetLayer)<=1 ) {
			throw new NeuralNetworkError("A layer must have at least a single neuron.  If you want to remove the entire layer use the pruneLayer method.");
		}
		
		// access the flat network
		FlatNetwork flat = this.network.getStructure().getFlat();
		double[] oldWeights = flat.getWeights();

		// first find out how many connections there will be after this prune.
		int connections = oldWeights.length;
		int inBoundConnections = 0;
		int outBoundConnections = 0;

		// are connections removed from the previous layer?
		if (targetLayer > 0) {
			inBoundConnections = this.network
					.getLayerTotalNeuronCount(targetLayer - 1);
			connections -= inBoundConnections;
		}

		// are there connections removed from the next layer?
		if (targetLayer < (this.network.getLayerCount() - 1)) {
			outBoundConnections = this.network
					.getLayerNeuronCount(targetLayer + 1);
			connections -= outBoundConnections;
		}

		// allocate new weights now that we know how big the new weights will be
		double[] newWeights = new double[connections];

		// construct the new weights
		int weightsIndex = 0;

		for (int fromLayer = flat.getLayerCounts().length - 2; fromLayer >= 0; fromLayer--) {
			int fromNeuronCount = network.getLayerTotalNeuronCount(fromLayer);
			int toNeuronCount = network.getLayerNeuronCount(fromLayer + 1);
			int toLayer = fromLayer + 1;

			for (int toNeuron = 0; toNeuron < toNeuronCount; toNeuron++) {
				for (int fromNeuron = 0; fromNeuron < fromNeuronCount; fromNeuron++) {
					boolean skip = false;
					if ((toLayer == targetLayer) && (toNeuron == neuron))
						skip = true;
					else if ((fromLayer == targetLayer)
							&& (fromNeuron == neuron))
						skip = true;

					if (!skip) {
						newWeights[weightsIndex++] = this.network.getWeight(
								fromLayer, fromNeuron, toNeuron);
					}
				}
			}
		}

		// swap in the new weights
		flat.setWeights(newWeights);

		// decrease layer count
		int flatLayer = network.getLayerCount() - targetLayer - 1;
		flat.getLayerCounts()[flatLayer]--;
		flat.getLayerFeedCounts()[flatLayer]--;

		// reindex
		reindexNetwork();

	}
	
	private void reindexNetwork()
	{
		FlatNetwork flat = this.network.getStructure().getFlat();
		
		int neuronCount = 0;
		int weightCount = 0;
		for (int i = 0; i < flat.getLayerCounts().length; i++) {
			if( i>0 ) {
				int from = flat.getLayerFeedCounts()[i-1];
				int to = flat.getLayerCounts()[i];
				weightCount+=from*to;				
			}
			flat.getLayerIndex()[i] = neuronCount;
			flat.getWeightIndex()[i] = weightCount;
			neuronCount += flat.getLayerCounts()[i];			
		}
		
		flat.setLayerOutput(new double[neuronCount]);
		flat.clearContext();
	}

	/**
	 * Stimulate the specified neuron by the specified percent. This is used to
	 * randomize the weights and bias values for weak neurons.
	 * 
	 * @param percent
	 *            The percent to randomize by.
	 * @param layer
	 *            The layer that the neuron is on.
	 * @param neuron
	 *            The neuron to randomize.
	 */
	public void stimulateNeuron(final double percent, final int layer,
			final int neuron) {
		/*
		 * final Distort d = new Distort(percent);
		 * 
		 * if (layer.hasBias()) { layer.setBiasWeight(neuron,
		 * d.randomize(layer.getBiasWeight(neuron))); }
		 * 
		 * // calculate the outbound significance for (final Synapse synapse :
		 * layer.getNext()) { for (int i = 0; i < synapse.getToNeuronCount();
		 * i++) { final double v = synapse.getMatrix().get(neuron, i);
		 * synapse.getMatrix().set(neuron, i, d.randomize(v)); } }
		 * 
		 * final Collection<Synapse> inboundSynapses =
		 * this.network.getStructure() .getPreviousSynapses(layer);
		 * 
		 * for (final Synapse synapse : inboundSynapses) { for (int i = 0; i <
		 * synapse.getFromNeuronCount(); i++) { final double v =
		 * synapse.getMatrix().get(i, neuron); synapse.getMatrix().set(i,
		 * neuron, d.randomize(v)); } }
		 */
	}

	/**
	 * Stimulate weaker neurons on a layer. Find the weakest neurons and then
	 * randomize them by the specified percent.
	 * 
	 * @param layer
	 *            The layer to stimulate.
	 * @param count
	 *            The number of weak neurons to stimulate.
	 * @param percent
	 *            The percent to stimulate by.
	 */
	public void stimulateWeakNeurons(final int layer, final int count,
			final double percent) {
		final int[] weak = findWeakestNeurons(layer, count);
		for (final int element : weak) {
			stimulateNeuron(percent, layer, element);
		}
	}
}
