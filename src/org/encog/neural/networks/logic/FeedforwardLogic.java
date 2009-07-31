package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedforwardLogic implements NeuralLogic {

	/**
	 * The logging object.
	 */
	private transient static final Logger logger = LoggerFactory.getLogger(FeedforwardLogic.class);
	
	private BasicNetwork network;
	
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

		if (FeedforwardLogic.logger.isDebugEnabled()) {
			FeedforwardLogic.logger.debug("Pattern {} presented to neural network", input);
		}

		if (useHolder == null) {
			holder = new NeuralOutputHolder();
		} else {
			holder = useHolder;
		}

		this.getNetwork().checkInputSize(input);
		compute(holder, this.getNetwork().getInputLayer(), input, null);
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

		if (FeedforwardLogic.logger.isDebugEnabled()) {
			FeedforwardLogic.logger.debug("Processing layer: {}, input= {}", layer, input);
		}

		preprocessLayer(layer, input, source);

		for (final Synapse synapse : layer.getNext()) {
			if (!holder.getResult().containsKey(synapse)) {
				if (FeedforwardLogic.logger.isDebugEnabled()) {
					FeedforwardLogic.logger.debug("Processing synapse: {}", synapse);
				}
				NeuralData pattern = synapse.compute(input);
				pattern = synapse.getToLayer().compute(pattern);
				synapse.getToLayer().process(pattern);
				holder.getResult().put(synapse, input);
				compute(holder, synapse.getToLayer(), pattern, synapse);

				// Is this the output from the entire network?
				if (synapse.getToLayer() == this.getNetwork().getOutputLayer()) {
					holder.setOutput(pattern);
				}
			}
		}
	}


	public void init(BasicNetwork network) {
		this.network = network;		
	}

	public BasicNetwork getNetwork() {
		return network;
	}

	public void preprocessLayer(Layer layer, NeuralData input, Synapse source) {
		// nothing to do
		
	}
	
	

}
