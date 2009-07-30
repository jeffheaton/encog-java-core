package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRecurrentLogic implements NeuralLogic {

	/**
	 * The logging object.
	 */
	private transient static final Logger logger = LoggerFactory.getLogger(BasicNetwork.class);

	private BasicNetwork network;
	
	public void init(BasicNetwork network)
	{
		this.network = network;
	}
	
	/**
	 * Compute the output for a given input to the neural network. This method
	 * provides a parameter to specify an output holder to use.  This holder
	 * allows propagation training to track the output from each layer.
	 * If you do not need this holder pass null, or use the other 
	 * compare method.
	 * 
	 * @param input The input provide to the neural network.
	 * @param useHolder Allows a holder to be specified, this allows
	 * propagation training to check the output of each layer.  
	 * @return The results from the output neurons.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {
		NeuralOutputHolder holder;

		if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
			SimpleRecurrentLogic.logger.debug("Pattern {} presented to neural network", input);
		}

		if (useHolder == null) {
			holder = new NeuralOutputHolder();
		} else {
			holder = useHolder;
		}

		this.network.checkInputSize(input);
		compute(holder, this.network.getInputLayer(), input, null);
		return holder.getOutput();
	}
	
	/**
	 * Internal computation method for a single layer.  This is called, 
	 * as the neural network processes.
	 * @param holder The output holder.
	 * @param layer The layer to process.
	 * @param input The input to this layer.
	 * @param source The source synapse.
	 */
	private void compute(final NeuralOutputHolder holder, final Layer layer,
			final NeuralData input, final Synapse source) {

		if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
			SimpleRecurrentLogic.logger.debug("Processing layer: {}, input= {}", layer, input);
		}

		handleRecurrentInput(layer, input, source);

		for (final Synapse synapse : layer.getNext()) {
			if (!holder.getResult().containsKey(synapse)) {
				if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
					SimpleRecurrentLogic.logger.debug("Processing synapse: {}", synapse);
				}
				NeuralData pattern = synapse.compute(input);
				pattern = synapse.getToLayer().compute(pattern);
				synapse.getToLayer().process(pattern);
				holder.getResult().put(synapse, input);
				compute(holder, synapse.getToLayer(), pattern, synapse);

				// Is this the output from the entire network?
				if (synapse.getToLayer() == this.network.getOutputLayer()) {
					holder.setOutput(pattern);
				}
			}
		}
	}
	
	/**
	 * Handle recurrent layers.  See if there are any recurrent layers before
	 * the specified layer that must affect the input.
	 * @param layer The layer being processed, see if there are any recurrent
	 * connections to this.
	 * @param input The input to the layer, will be modified with the result
	 * from any recurrent layers.
	 * @param source The source synapse.
	 */
	private void handleRecurrentInput(final Layer layer,
			final NeuralData input, final Synapse source) {
		for (final Synapse synapse 
				: this.network.getStructure().getPreviousSynapses(layer)) {
			if (synapse != source) {
				if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
					SimpleRecurrentLogic.logger.debug("Recurrent layer from: {}", input);
				}
				final NeuralData recurrentInput = synapse.getFromLayer()
						.recur();

				if (recurrentInput != null) {
					final NeuralData recurrentOutput = synapse
							.compute(recurrentInput);

					for (int i = 0; i < input.size(); i++) {
						input.setData(i, input.getData(i)
								+ recurrentOutput.getData(i));
					}

					if (SimpleRecurrentLogic.logger.isDebugEnabled()) {
						SimpleRecurrentLogic.logger.debug("Recurrent layer to: {}", input);
					}
				}
			}
		}
	}


	
}
