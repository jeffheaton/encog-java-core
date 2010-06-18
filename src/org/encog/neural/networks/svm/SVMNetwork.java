package org.encog.neural.networks.svm;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicNetworkPersistor;
import org.encog.persist.persistors.SVMNetworkPersistor;

public class SVMNetwork extends BasicNetwork {

	private svm_model[] models;
	private svm_parameter[] params;
	private int inputCount;
	private int outputCount;
	private KernelType kernelType;
	private SVMType svmType;

	public SVMNetwork(int inputCount, int outputCount, SVMType svmType, KernelType kernelType)
	{
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.kernelType = kernelType;
		this.svmType = svmType;

		models = new svm_model[outputCount];
		params = new svm_parameter[outputCount];

		for (int i = 0; i < outputCount; i++) {
			params[i] = new svm_parameter();
			
			switch(svmType)
			{
				case C_SVC:
					params[i].svm_type = svm_parameter.C_SVC;					
					break;
				case NU_SVC:
					params[i].svm_type = svm_parameter.NU_SVC;					
					break;					
				case ONE_CLASS:
					params[i].svm_type = svm_parameter.ONE_CLASS;					
					break;					
				case EPSILON_SVR:
					params[i].svm_type = svm_parameter.EPSILON_SVR;					
					break;					
				case NU_SVR:
					params[i].svm_type = svm_parameter.NU_SVR;					
					break;					
			}
			
			switch(kernelType)
			{				
				case LINEAR:
					params[i].kernel_type = svm_parameter.LINEAR;
					break;
				case POLY:
					params[i].kernel_type = svm_parameter.POLY;
					break;
				case RBF:
					params[i].kernel_type = svm_parameter.RBF;
					break;
				case SIGMOID:
					params[i].kernel_type = svm_parameter.SIGMOID;
					break;
				case PRECOMPUTED:
					params[i].kernel_type = svm_parameter.PRECOMPUTED;
					break;
			}
			
			params[i].kernel_type = svm_parameter.RBF;
			params[i].degree = 3;
			params[i].coef0 = 0;
			params[i].nu = 0.5;
			params[i].cache_size = 100;
			params[i].C = 1;
			params[i].eps = 1e-3;
			params[i].p = 0.1;
			params[i].shrinking = 1;
			params[i].probability = 0;
			params[i].nr_weight = 0;
			params[i].weight_label = new int[0];
			params[i].weight = new double[0];
			params[i].gamma = 1.0 / inputCount;
		}		
	}
	
	public SVMNetwork(int inputCount, int outputCount, boolean regression) {
		this(inputCount, outputCount, regression?SVMType.NU_SVR:SVMType.NU_SVC,KernelType.RBF);
	}

	public NeuralData compute(NeuralData input) {
		NeuralData result = new BasicNeuralData(this.outputCount);

		svm_node[] formattedInput = makeSparse(input);

		for (int i = 0; i < this.outputCount; i++) {
			double d = svm.svm_predict(this.models[i], formattedInput);
			result.setData(i, d);
		}
		return result;
	}

	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {

		useHolder.setOutput(compute(input));
		return useHolder.getOutput();
	}

	public svm_node[] makeSparse(NeuralData data) {
		svm_node[] result = new svm_node[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = new svm_node();
			result[i].index = i + 1;
			result[i].value = data.getData(i);
		}

		return result;
	}

	/**
	 * Create a persistor for this object.
	 * 
	 * @return The newly created persistor.
	 */
	@Override
	public Persistor createPersistor() {
		return new SVMNetworkPersistor();
	}

	public int getInputCount() {
		return this.inputCount;
	}

	public int getOutputCount() {
		return this.outputCount;
	}

	public svm_model[] getModels() {
		return models;
	}

	public svm_parameter[] getParams() {
		return params;
	}

}
