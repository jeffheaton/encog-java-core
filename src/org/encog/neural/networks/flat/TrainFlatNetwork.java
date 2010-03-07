package org.encog.neural.networks.flat;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class TrainFlatNetwork {
	
	private NeuralDataSet training;
	private FlatNetwork network;
	private double[] layerDelta;
	private double[] gradients;
	private double[] lastGradient;
	private ErrorCalculation errorCalculation = new ErrorCalculation();
	private double[] updateValues;
	private double[] weights;
	private int[] layerCounts;
	private int[] layerIndex;
	private int[] weightIndex;
	private double[] layerOutput;
	
	public TrainFlatNetwork(FlatNetwork network, NeuralDataSet training)
	{
		this.training = training;
		this.network = network;
		
		this.layerDelta = new double[network.getLayerOutput().length];
		this.gradients = new double[network.getWeights().length];
		this.updateValues = new double[network.getWeights().length];
		this.lastGradient = new double[network.getWeights().length];
		
		this.weights = network.getWeights();
		this.layerIndex = network.getLayerIndex();
		this.layerCounts = network.getLayerCounts();
		this.weightIndex = network.getWeightIndex();
		this.layerOutput = network.getLayerOutput();
		
		for(int i=0;i<this.updateValues.length;i++)
		{
			this.updateValues[i] = ResilientPropagation.DEFAULT_INITIAL_UPDATE;
		}
	}
	
	public double derivativeFunction(final double d) {
		return d * (1.0 - d);
	}
	
	public void iteration()
	{
		double[] actual = new double[network.getOutputCount()];
		errorCalculation.reset();
		
		for(NeuralDataPair pair: this.training)
		{
			double[] input = pair.getInput().getData();
			double[] ideal = pair.getIdeal().getData();
			
			this.network.calculate(input,actual);
			
			errorCalculation.updateError(actual, ideal);
			
			for(int i=0;i<actual.length;i++)
			{
				this.layerDelta[i] = derivativeFunction(actual[i])*(ideal[i]-actual[i]);
			}
			
			for(int i=0;i<this.layerCounts.length-1;i++)
			{
				processLevel(i);
			}
		}
		
		learn();
	}
	
	private void processLevel(int currentLevel)
	{
		int fromLayerIndex = this.layerIndex[currentLevel+1];
		int toLayerIndex = this.layerIndex[currentLevel];
		int fromLayerSize = this.layerCounts[currentLevel+1];
		int toLayerSize = this.layerCounts[currentLevel];
		
		// clear the to-deltas
		for(int i=0;i<fromLayerSize;i++)
		{
			this.layerDelta[fromLayerIndex+i] = 0;
		}
		
		int index = this.weightIndex[currentLevel]+toLayerSize;

		for (int x = 0; x < toLayerSize; x++) {
			for (int y = 0; y < fromLayerSize; y++) {
				final double value = this.layerOutput[fromLayerIndex+y] * layerDelta[toLayerIndex+x];
				this.gradients[index] += value;
				layerDelta[fromLayerIndex+y] +=  this.weights[index] * layerDelta[toLayerIndex+x];
				index++;
			}
		}

		for (int i = 0; i < fromLayerSize; i++) {
			layerDelta[fromLayerIndex+i]*= this.derivativeFunction(this.layerOutput[fromLayerIndex+i]);
		}
	}
	
	private void learn()
	{
		for (int i = 0; i < this.gradients.length; i++) {
			this.weights[i]+=updateWeight(this.gradients, i);
			this.gradients[i] = 0;
		}
	}
	
	/**
	 * Determine the amount to change a weight by.
	 * @param gradients The gradients.
	 * @param index The weight to adjust.
	 * @return The amount to change this weight by.
	 */
	private double updateWeight(final double[] gradients, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(this.gradients[index]
				* this.lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = this.updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, ResilientPropagation.DEFAULT_MAX_STEP);
			weightChange = sign(this.gradients[index]) * delta;
			this.updateValues[index] = delta;
			this.lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = this.updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			this.updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			this.lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = this.lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			this.lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}
	
	/**
	 * Determine the sign of the value.
	 * 
	 * @param value
	 *            The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	private int sign(final double value) {
		if (Math.abs(value) < ResilientPropagation.DEFAULT_ZERO_TOLERANCE) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public double getError()
	{
		return errorCalculation.calculateRMS();
	}
	
}
