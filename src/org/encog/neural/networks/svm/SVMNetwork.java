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

/**
 * This is a network that is backed by one or more Support Vector Machines
 * (SVM). It is designed to function very similarly to an Encog neural network,
 * and is largely interchangeable with an Encog neural network.
 * 
 * The support vector machine supports several types. Regression is used when
 * you want the network to predict a value, given the input. Function
 * approximation is a good example of regression. Classification is used when
 * you want the SVM to group the input data into one or more classes.
 * 
 * Support Vector Machines typically have a single output. Neural networks can
 * have multiple output neurons. To get around this issue, this class will
 * create multiple SVM's if there is more than one output specified.
 * 
 * Because a SVM is trained quite differently from a neural network, none of the
 * neural network training classes will work. This class must be trained using
 * SVMTrain.
 */
public class SVMNetwork extends BasicNetwork {

	/**
	 * The SVM's to use, one for each output.
	 */
	private svm_model[] models;

	/**
	 * The parameters for each of the SVM's.
	 */
	private svm_parameter[] params;

	/**
	 * The input count.
	 */
	private int inputCount;

	/**
	 * The output count.
	 */
	private int outputCount;

	/**
	 * The kernel type.
	 */
	private KernelType kernelType;

	/**
	 * The SVM type.
	 */
	private SVMType svmType;

	/**
	 * Construct a SVM network.
	 * 
	 * @param inputCount
	 *            The input count.
	 * @param outputCount
	 *            The output count.
	 * @param svmType
	 *            The type of SVM.
	 * @param kernelType
	 *            The SVM kernal type.
	 */
	public SVMNetwork(int inputCount, int outputCount, SVMType svmType,
			KernelType kernelType) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.kernelType = kernelType;
		this.svmType = svmType;

		models = new svm_model[outputCount];
		params = new svm_parameter[outputCount];

		for (int i = 0; i < outputCount; i++) {
			params[i] = new svm_parameter();

			switch (svmType) {
			case SupportVectorClassification:
				params[i].svm_type = svm_parameter.C_SVC;
				break;
			case NewSupportVectorClassification:
				params[i].svm_type = svm_parameter.NU_SVC;
				break;
			case SupportVectorOneClass:
				params[i].svm_type = svm_parameter.ONE_CLASS;
				break;
			case EpsilonSupportVectorRegression:
				params[i].svm_type = svm_parameter.EPSILON_SVR;
				break;
			case NewSupportVectorRegression:
				params[i].svm_type = svm_parameter.NU_SVR;
				break;
			}

			switch (kernelType) {
			case Linear:
				params[i].kernel_type = svm_parameter.LINEAR;
				break;
			case Poly:
				params[i].kernel_type = svm_parameter.POLY;
				break;
			case RadialBasisFunction:
				params[i].kernel_type = svm_parameter.RBF;
				break;
			case Sigmoid:
				params[i].kernel_type = svm_parameter.SIGMOID;
				break;
			case Precomputed:
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

	/**
	 * Construct an SVM network. For regression it will use an epsilon support
	 * vector. Both types will use an RBF kernel.
	 * 
	 * @param inputCount
	 *            The input count.
	 * @param outputCount
	 *            The output count.
	 * @param regression
	 *            True if this network is used for regression.
	 */
	public SVMNetwork(int inputCount, int outputCount, boolean regression) {
		this(inputCount, outputCount,
				regression ? SVMType.EpsilonSupportVectorRegression
						: SVMType.SupportVectorClassification,
				KernelType.RadialBasisFunction);
	}

	/**
	 * Compute the output for the given input.
	 * @param input The input to the SVM.
	 * @return The results from the SVM.
	 */
	public NeuralData compute(NeuralData input) {
		NeuralData result = new BasicNeuralData(this.outputCount);

		svm_node[] formattedInput = makeSparse(input);

		for (int i = 0; i < this.outputCount; i++) {
			double d = svm.svm_predict(this.models[i], formattedInput);
			result.setData(i, d);
		}
		return result;
	}

	/**
	 * Compute the output for the given input.
	 * @param input The input to the SVM.
	 * @param useHolder The output holder to use.
	 * @return The results from the SVM.
	 */
	public NeuralData compute(final NeuralData input,
			final NeuralOutputHolder useHolder) {

		useHolder.setOutput(compute(input));
		return useHolder.getOutput();
	}

	/**
	 * Convert regular Encog NeuralData into the "sparse" data needed by an SVM.
	 * 
	 * @param data
	 *            The data to convert.
	 * @return The SVM sparse data.
	 */
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

	/**
	 * @return The input count.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The output count.
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return The SVM models for each output.
	 */
	public svm_model[] getModels() {
		return models;
	}

	/**
	 * @return The SVM params for each of the outputs.
	 */
	public svm_parameter[] getParams() {
		return params;
	}

	/**
	 * @return The SVM kernel type.
	 */
	public KernelType getKernelType() {
		return kernelType;
	}

	/**
	 * @return The type of SVM in use.
	 */
	public SVMType getSvmType() {
		return svmType;
	}

}
