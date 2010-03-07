package org.encog.neural.networks.flat;

import org.encog.mathutil.BoundMath;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;

public class FlatNetwork {
	
	private final int inputCount;
	private final int outputCount;
	private final int[] layerCounts;
	private final int[] weightIndex;
	private final boolean tanh;
	private double[] weights;
	private Object[] output;
	
	public FlatNetwork(BasicNetwork network)
	{
		validate(network);
		Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer output = network.getLayer(BasicNetwork.TAG_OUTPUT);
		
		this.inputCount = input.getNeuronCount();
		this.outputCount = output.getNeuronCount();
		
		int layerCount = network.getStructure().getLayers().size();
		
		this.layerCounts = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.output = new Object[layerCount];
		
		int index = 0;

		for(Layer layer: network.getStructure().getLayers() )
		{
			this.output[index] = new double[layer.getNeuronCount()];
			this.layerCounts[index] = layer.getNeuronCount();
			
			if( index==0 )
				this.weightIndex[index] = 0;
			else
				this.weightIndex[index] = this.weightIndex[index-1]+
				(this.layerCounts[index-1]
				+(this.layerCounts[index]*this.layerCounts[index-1]));
			
			index++;
		}
		
		this.weights = NetworkCODEC.networkToArray(network);
		
		if( input.getActivationFunction() instanceof ActivationSigmoid )
			this.tanh = false;
		else
			this.tanh = true;
	}
	
	public double sigmoid(double d)
	{
		return 1.0 / (1 + BoundMath.exp(-1.0 * d));	
	}
	
	public double[] calculate(double[] input)
	{
		int layerIndex = this.output.length - 1;
		
		System.arraycopy(input, 0, output[layerIndex], 0, this.layerCounts[layerIndex]);
		
		for(int i=layerIndex;i>0;i--)
		{
			calculateLayer(i);
		}
		
		return (double[])output[0];
	}
	
	private void calculateLayer(int layerIndex)
	{
		double[] inputData = (double[])output[layerIndex];
		double[] outputData = (double[])output[layerIndex-1];
		
		int index = this.weightIndex[layerIndex-1];
		
		// threshold values
		for(int i=0;i<outputData.length;i++)
		{
			outputData[i] = this.weights[index++];
		}
		
		// weight values
		for(int x=0;x<outputData.length;x++)
		{
			double sum = 0;
			for(int y=0;y<inputData.length;y++)
			{
				sum+=this.weights[index++]*inputData[y];
			}
			outputData[x] += sum;
			outputData[x] = sigmoid(outputData[x]);
		}
		

		
	}
	
	public void validate(BasicNetwork network)
	{
		
	}

	public int getInputCount() {
		return inputCount;
	}

	public int getOutputCount() {
		return outputCount;
	}

	public int[] getLayerCounts() {
		return layerCounts;
	}

	public int[] getWeightIndex() {
		return weightIndex;
	}

	public boolean isTanh() {
		return tanh;
	}

	public double[] getWeights() {
		return weights;
	}

	public Object[] getOutput() {
		return output;
	}
	
	
}
