package org.encog.neural.networks.flat;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;

public class TrainFlatNetwork {
	
	private NeuralDataSet training;
	private FlatNetwork network;
	private Object[] layerDelta;
	private double[] errors;
	private ErrorCalculation errorCalculation = new ErrorCalculation();
	
	public TrainFlatNetwork(FlatNetwork network, NeuralDataSet training)
	{
		this.training = training;
		this.network = network;
		
		this.layerDelta = new Object[network.getLayerCounts().length];
		this.errors = new double[network.getWeights().length];
		
		for(int i=0;i<network.getLayerCounts().length;i++)
		{
			this.layerDelta[i] = new double[network.getLayerCounts()[i]];
		}
	}
	
	public double derivativeFunction(final double d) {
		return d * (1.0 - d);
	}
	
	public void iteration()
	{
		errorCalculation.reset();
		
		for(NeuralDataPair pair: this.training)
		{
			double[] input = pair.getInput().getData();
			double[] ideal = pair.getIdeal().getData();
			double[] actual = this.network.calculate(input);
			
			errorCalculation.updateError(actual, ideal);
			
			double[] error = (double[])this.layerDelta[0];
			
			for(int i=0;i<actual.length;i++)
			{
				error[i] = derivativeFunction(actual[i])*(ideal[i]-actual[i]);
			}
			
			for(int i=0;i<this.network.getLayerCounts().length-1;i++)
			{
				processLevel(i);
			}
		}
		
		learn();
	}
	
	private void processLevel(int currentLevel)
	{
		double[] fromDeltas = (double[])layerDelta[currentLevel+1];
		double[] toDeltas = (double[])layerDelta[currentLevel];
		
		// clear the to-deltas
		for(int i=0;i<fromDeltas.length;i++)
		{
			fromDeltas[i] = 0;
		}
		
		int index = this.network.getWeightIndex()[currentLevel]+toDeltas.length;
		
		double[] actualData = (double[])this.network.getOutput()[currentLevel+1];

		for (int x = 0; x < toDeltas.length; x++) {
			for (int y = 0; y < fromDeltas.length; y++) {
				final double value = actualData[y] * toDeltas[x];
				this.errors[index] += value;
				fromDeltas[y] += this.network.getWeights()[index] * toDeltas[x];
				index++;
			}
		}

		for (int i = 0; i < actualData.length; i++) {
			fromDeltas[i]*= this.derivativeFunction(actualData[i]);
		}
	}
	
	private void learn()
	{
		for(int i=0;i<this.errors.length;i++)
		{
			this.network.getWeights()[i]+=this.errors[i]*0.7;
			this.errors[i] = 0;
		}
	}
	
	public double getError()
	{
		return errorCalculation.calculateRMS();
	}
	
}
