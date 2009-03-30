package org.encog.neural.networks.training.propagation.back;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationLayer;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.util.logging.DumpMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackPropagationMethod implements PropagationMethod {

	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	BackPropagation backpropagation;
	
	public void init(Propagation propagation) {
		this.backpropagation = (BackPropagation)propagation;
	}
	
	/**
	 * Calculate the error for the given ideal values.
	 * 
	 * @param ideal
	 *            Ideal output values.
	 */
	public double[] calculateInitialDelta(final NeuralData actual,
			final NeuralData ideal) {
		
		// get the output layer
		Layer outputLayer = this.backpropagation.getNetwork().getOutputLayer();
		
		// prepare an array to hold the deltas from the output layer
		double[] result = new double[outputLayer.getNeuronCount()];
		
		// obtain the output for each output layer neuron
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			result[i] = actual.getData(i);
		}

		// take the derivative of these outputs
		outputLayer.getActivationFunction().derivativeFunction(result);

		// multiply by the difference between the actual and idea
		for (int i = 0; i < outputLayer.getNeuronCount(); i++) {
			result[i] *= ideal.getData(i) - actual.getData(i);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Initial deltas: " + DumpMatrix.dumpArray(result));
		}
		return result;
	}
	
	public void determineDeltas(NeuralData ideal) {
		
		BasicNetwork network = this.backpropagation.getNetwork();
		
		// make sure that the input is of the correct size
		if (ideal.size() != network.getOutputLayer().getNeuronCount()) {
			throw new NeuralNetworkError(
					"Size mismatch: Can't calcError for ideal input size="
							+ ideal.size() + " for output layer size="
							+ network.getOutputLayer().getNeuronCount());
		}

		// log that we are performing a backward pass
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation backward pass");
		}
		
		// calculate the initial deltas from the output layer
		double[] backDeltas = this.calculateInitialDelta(this.backpropagation.getFire(), ideal);

		int layerNumber = 0;
		
		// loop over all of the "virtual" layers backwards
		for(PropagationLayer propLayer: this.backpropagation.getLayers() )
		{
			// is this the output layer?
			if( layerNumber == 0)
			{
				determineOutputDeltas(propLayer, this.backpropagation.getFire(),ideal);
			}
			else
			{
				//determineOtherDeltas(backDeltas);
			}
			layerNumber++;
		}
	}	
	
	private void determineOutputDeltas(PropagationLayer layer, NeuralData actual,NeuralData ideal)
	{	
		// obtain the output for each output layer neuron
		for (int i = 0; i < layer.getNeuronCount(); i++) {
			layer.setErrorDelta(i, actual.getData(i) );
		}

		// take the derivative of these outputs
		layer.getLayer().getActivationFunction().derivativeFunction(layer.getErrorDeltas());

		// multiply by the difference between the actual and idea
		for (int i = 0; i < layer.getNeuronCount(); i++) {
			layer.getErrorDeltas()[i] *= ideal.getData(i) - actual.getData(i);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Initial deltas: " + DumpMatrix.dumpArray(layer.getErrorDeltas()));
		}

	}
	
	private void determineOtherDeltas(PropagationLayer next)
	{
		/*for (int i = 0; i < this.layer.getNext().getNeuronCount(); i++) {
			for (int j = 0; j < this.layer.getNeuronCount(); j++) {
				accumulateMatrixDelta(j, i, next.getErrorDelta(i) * this.layer.getFire(j));
				setError(j, getError(j) + this.layer.getMatrix().get(j, i) * next.getErrorDelta(i));
			}
			accumulateThresholdDelta(i, next.getErrorDelta(i));
		}

		if (this.layer.isHidden()) {
			// hidden layer deltas
			for (int i = 0; i < this.layer.getNeuronCount(); i++) {
				setErrorDelta(i, BoundNumbers.bound(calculateDelta(i)));
			}
		}*/
	}
}