package org.encog.neural.networks;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;

public class SVMNetwork {
	
	private svm_model[] models;
	private svm_parameter params;
	private int inputCount;
	private int outputCount;
	
	public SVMNetwork(int inputCount, int outputCount)
	{
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		
		models = new svm_model[outputCount];
		params = new svm_parameter();
		
		if(params.gamma == 0 && inputCount > 0)
			params.gamma = 1.0/inputCount;
	}
	
	public NeuralData compute(NeuralData input)
	{
		NeuralData result = new BasicNeuralData(this.outputCount);
		
		svm_node[] formattedInput = makeSparse(input);
		
		for(int i=0;i<this.outputCount;i++)
		{
			double d = svm.svm_predict(this.models[i], formattedInput );
			result.setData(i,d);
		}
		return result;
	}
	
	public svm_node[] makeSparse(NeuralData data)
	{
		svm_node[] result = new svm_node[data.size()];
		for(int i=0;i<data.size();i++)
		{
			result[i] = new svm_node();
			result[i].index = i+1;
			result[i].value = data.getData(i);
		}
		
		return result;
	}
	
	public int getInputCount()
	{
		return this.inputCount;
	}
	
	public int getOutputCount()
	{
		return this.outputCount;
	}

	public svm_model[] getModels() {
		return models;
	}

	public svm_parameter getParams() {
		return params;
	}
}
