package org.encog.neural.networks.svm;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNetworkPersistor;
import org.encog.persist.persistors.SVMNetworkPersistor;

public class SVMNetwork extends BasicNetwork {
	
	private svm_model[] models;
	private svm_parameter params;
	private int inputCount;
	private int outputCount;
	
	public SVMNetwork(int inputCount, int outputCount, boolean regression)
	{
		this(inputCount,outputCount,0);
		
		if( regression )
			this.params.svm_type = svm_parameter.EPSILON_SVR;
		else
			this.params.svm_type = svm_parameter.C_SVC;
				
	}
	
	public SVMNetwork(int inputCount, int outputCount, int svmType)
	{
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		
		models = new svm_model[outputCount];
		params = new svm_parameter();
		
		params.svm_type = svm_parameter.C_SVC;
		params.kernel_type = svm_parameter.RBF;
		params.degree = 3;
		params.coef0 = 0;
		params.nu = 0.5;
		params.cache_size = 100;
		params.C = 1;
		params.eps = 1e-3;
		params.p = 0.1;
		params.shrinking = 1;
		params.probability = 0;
		params.nr_weight = 0;
		params.weight_label = new int[0];
		params.weight = new double[0];

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
	
	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	public Persistor createPersistor() {
		return new SVMNetworkPersistor();
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
