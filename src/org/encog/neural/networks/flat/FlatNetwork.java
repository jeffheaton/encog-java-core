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
	private final int[] layerIndex;
	private final boolean tanh;
	private double[] weights;
	private double[] layerOutput;
	
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
		this.layerIndex = new int[layerCount];
		
		int index = 0;
		int neuronCount = 0;

		for(Layer layer: network.getStructure().getLayers() )
		{
			this.layerCounts[index] = layer.getNeuronCount();
			neuronCount+=layer.getNeuronCount();
			
			if( index==0 ) {
				this.weightIndex[index] = 0;
				this.layerIndex[index] = 0;
			}
			else {
				this.weightIndex[index] = this.weightIndex[index-1]+
				(this.layerCounts[index-1]
				+(this.layerCounts[index]*this.layerCounts[index-1]));
				this.layerIndex[index] = this.layerIndex[index-1] + 
				this.layerCounts[index-1];
			}
			
			index++;
		}
		
		this.weights = NetworkCODEC.networkToArray(network);
		this.layerOutput = new double[neuronCount];
		
		if( input.getActivationFunction() instanceof ActivationSigmoid )
			this.tanh = false;
		else
			this.tanh = true;
	}
	
	public double sigmoid(double d)
	{
		return 1.0 / (1 + BoundMath.exp(-1.0 * d));	
	}
	
	public void calculate(double[] input, double[] output)
	{
		int sourceIndex = this.layerOutput.length - this.inputCount;
		
		System.arraycopy(input, 0, this.layerOutput, sourceIndex, this.inputCount);
		
		for(int i=this.layerIndex.length-1;i>0;i--)
		{
			calculateLayer(i);
		}
		
		System.arraycopy(layerOutput, 0, output, 0, this.outputCount);
	}
	
	private void calculateLayer(int currentLayer)
	{
		//double[] inputData = (double[])output[layerIndex];
		//double[] outputData = (double[])output[layerIndex-1];
		
		int inputIndex = this.layerIndex[currentLayer];
		int outputIndex = this.layerIndex[currentLayer-1];
		int inputSize = this.layerCounts[currentLayer];
		int outputSize = this.layerCounts[currentLayer-1];
		
		int index = this.weightIndex[currentLayer-1];
		
		// threshold values
		for(int i=0;i<outputSize;i++)
		{
			this.layerOutput[i+outputIndex] = this.weights[index++];
		}
		
		// weight values
		for(int x=0;x<outputSize;x++)
		{
			double sum = 0;
			for(int y=0;y<inputSize;y++)
			{
				sum+=this.weights[index++]*layerOutput[inputIndex+y];
			}
			layerOutput[outputIndex+x] += sum;
			layerOutput[outputIndex+x] = sigmoid(layerOutput[outputIndex+x]);
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

	public double[] getLayerOutput() {
		return this.layerOutput;
	}

	public int[] getLayerIndex() {
		return layerIndex;
	}
	
	
}
