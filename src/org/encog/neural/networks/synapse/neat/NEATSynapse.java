package org.encog.neural.networks.synapse.neat;

import java.util.ArrayList;
import java.util.List;

import org.encog.math.matrices.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.NeuralNetworkError;
import org.encog.persist.Persistor;

public class NEATSynapse implements Synapse {
	
	private Layer fromLayer;
	private Layer toLayer;
	private List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();
	private int networkDepth;
	
	
	public NEATSynapse(BasicLayer fromLayer, BasicLayer toLayer,
			List<NEATNeuron> neurons, int networkDepth) {
		this.fromLayer = fromLayer;
		this.toLayer = toLayer;
		this.neurons.addAll(neurons);
		this.networkDepth = networkDepth;
	}

	/**
	 * @return A clone of this object.
	 */
	public Object clone()
	{
		return null;
	}

	/**
	 * Compute the output from this synapse.
	 * @param input The input to this synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(NeuralData input)
	{
		return null;
	}

	
	/**
	 * @return The from layer.
	 */
	public Layer getFromLayer()
	{
		return this.fromLayer;
	}

	/**
	 * @return The neuron count from the "from layer".
	 */
	public int getFromNeuronCount()
	{
		return this.fromLayer.getNeuronCount();
	}

	/**
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
	 */
	public Matrix getMatrix()
	{
		return null;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	public int getMatrixSize()
	{
		return 0;
	}

	/**
	 * @return The "to layer".
	 */
	public Layer getToLayer()
	{
		return this.toLayer;
	}

	/**
	 * @return The neuron count from the "to layer".
	 */
	public int getToNeuronCount()
	{
		return this.toLayer.getNeuronCount();
	}

	/**
	 * @return The type of synapse that this is.
	 */
	public SynapseType getType()
	{
		return null;
	}

	/**
	 * @return True if this is a self-connected synapse.  That is,
	 * the from and to layers are the same.
	 */
	public boolean isSelfConnected()
	{
		return false;
	}

	/**
	 * @return True if the weights for this synapse can be modified.
	 */
	public boolean isTeachable()
	{
		return false;
	}

	/**
	 * Set the from layer for this synapse.
	 * @param fromLayer The from layer for this synapse.
	 */
	public void setFromLayer(Layer fromLayer)
	{
		this.fromLayer = fromLayer;
	}

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix)
	{
		throw new NeuralNetworkError("Neat synapse cannot have a simple matrix.");
	}

	/**
	 * Set the target layer from this synapse.
	 * @param toLayer The target layer from this synapse.
	 */
	public void setToLayer(Layer toLayer)
	{
		this.toLayer = toLayer;
	}

	public String getDescription()
	{
		return null;
	}
	
	public String getName()
	{
		return null;
	}
	
	public void setName(String name)
	{
	
	}
	
	public void setDescription(String description)
	{
		
	}

	@Override
	public Persistor createPersistor() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
