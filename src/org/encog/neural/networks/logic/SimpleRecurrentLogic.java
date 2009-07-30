package org.encog.neural.networks.logic;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRecurrentLogic extends FeedforwardLogic {

	/**
	 * The logging object.
	 */
	private transient static final Logger logger = LoggerFactory.getLogger(SimpleRecurrentLogic.class);
	
	
		
	/**
	 * Handle recurrent layers.  See if there are any recurrent layers before
	 * the specified layer that must affect the input.
	 * @param layer The layer being processed, see if there are any recurrent
	 * connections to this.
	 * @param input The input to the layer, will be modified with the result
	 * from any recurrent layers.
	 * @param source The source synapse.
	 */
	public void preprocessLayer(final Layer layer,
			final NeuralData input, final Synapse source) {
		for (final Synapse synapse 
				: this.getNetwork().getStructure().getPreviousSynapses(layer)) {
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
