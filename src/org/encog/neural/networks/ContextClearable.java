/**
 * This class implements a neural network. This class works in conjunction the
 * Layer classes. Layers are added to the BasicNetwork to specify the structure
 * of the neural network.
 * 
 * The first layer added is the input layer, the final layer added is the output
 * layer. Any layers added between these two layers are the hidden layers.
 * 
 * The network structure is stored in the structure member. It is important to
 * call:
 * 
 * network.getStructure().finalizeStructure();
 * 
 * Once the neural network has been completely constructed.
 * 
 */
package org.encog.neural.networks;

/**
 * Allows the clearContext method to be called. If the layer has a context that
 * can be cleared, it should support this interface.
 * 
 */
public interface ContextClearable {

	/**
	 * Clear the context for this layer.
	 */
	void clearContext();
}
