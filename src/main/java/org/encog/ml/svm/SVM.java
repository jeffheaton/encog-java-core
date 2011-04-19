/*
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.svm;

import org.encog.EncogError;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.basic.BasicMLDataArray;
import org.encog.neural.data.MLDataArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.util.simple.EncogUtility;

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
public class SVM extends BasicML implements MLRegression, MLClassification, MLError {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The SVM model to use.
	 */
	private svm_model model;

	/**
	 * The params for the model.
	 */
	private svm_parameter params;

	/**
	 * The input count.
	 */
	private int inputCount;

	public SVM() {
		this.params = new svm_parameter();
	}

	public SVM(svm_model model) {
		this.model = model;
		this.params = model.param;
		this.inputCount = 0;

		// determine the input count
		for (int row = 0; row < this.model.SV.length; row++) {
			for (int col = 0; col < this.model.SV[row].length; col++) {
				inputCount = Math
						.max(this.model.SV[row][col].index, inputCount);
			}
		}

		//
	}

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
	public SVM(int inputCount, SVMType svmType, KernelType kernelType) {
		this.inputCount = inputCount;

		params = new svm_parameter();

		switch (svmType) {
		case SupportVectorClassification:
			params.svm_type = svm_parameter.C_SVC;
			break;
		case NewSupportVectorClassification:
			params.svm_type = svm_parameter.NU_SVC;
			break;
		case SupportVectorOneClass:
			params.svm_type = svm_parameter.ONE_CLASS;
			break;
		case EpsilonSupportVectorRegression:
			params.svm_type = svm_parameter.EPSILON_SVR;
			break;
		case NewSupportVectorRegression:
			params.svm_type = svm_parameter.NU_SVR;
			break;
		}

		switch (kernelType) {
		case Linear:
			params.kernel_type = svm_parameter.LINEAR;
			break;
		case Poly:
			params.kernel_type = svm_parameter.POLY;
			break;
		case RadialBasisFunction:
			params.kernel_type = svm_parameter.RBF;
			break;
		case Sigmoid:
			params.kernel_type = svm_parameter.SIGMOID;
			break;
		case Precomputed:
			params.kernel_type = svm_parameter.PRECOMPUTED;
			break;
		}

		// params[i].kernel_type = svm_parameter.RBF;
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
		params.gamma = 1.0 / inputCount;

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
	public SVM(int inputCount, boolean regression) {
		this(inputCount, regression ? SVMType.EpsilonSupportVectorRegression
				: SVMType.SupportVectorClassification,
				KernelType.RadialBasisFunction);
	}

	/**
	 * Compute the output for the given input.
	 * 
	 * @param input
	 *            The input to the SVM.
	 * @return The results from the SVM.
	 */
	public MLDataArray compute(MLDataArray input) {

		if (this.model == null) {
			throw new EncogError(
					"Can't use the SVM yet, it has not been trained, and no model exists.");
		}

		MLDataArray result = new BasicMLDataArray(1);

		svm_node[] formattedInput = makeSparse(input);

		double d = svm.svm_predict(this.model, formattedInput);
		result.setData(0, d);

		return result;
	}

	/**
	 * Convert regular Encog NeuralData into the "sparse" data needed by an SVM.
	 * 
	 * @param data
	 *            The data to convert.
	 * @return The SVM sparse data.
	 */
	public svm_node[] makeSparse(MLDataArray data) {
		svm_node[] result = new svm_node[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = new svm_node();
			result[i].index = i + 1;
			result[i].value = data.getData(i);
		}

		return result;
	}

	/**
	 * @return The input count.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The SVM models for each output.
	 */
	public svm_model getModel() {
		return model;
	}

	/**
	 * @return The SVM params for each of the outputs.
	 */
	public svm_parameter getParams() {
		return params;
	}

	public boolean supportsMapPersistence() {
		return true;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	public void setModel(svm_model model) {
		this.model = model;

	}

	@Override
	public void updateProperties() {
		// unneeded
	}

	public KernelType getKernelType() {
		switch (this.params.kernel_type) {
		case svm_parameter.LINEAR:
			return KernelType.Linear;
		case svm_parameter.POLY:
			return KernelType.Poly;
		case svm_parameter.RBF:
			return KernelType.RadialBasisFunction;
		case svm_parameter.SIGMOID:
			return KernelType.Sigmoid;
		case svm_parameter.PRECOMPUTED:
			return KernelType.Precomputed;
		default:
			return null;
		}
	}

	public SVMType getSVMType() {
		switch (params.svm_type) {
		case svm_parameter.C_SVC:
			return SVMType.SupportVectorClassification;
		case svm_parameter.NU_SVC:
			return SVMType.NewSupportVectorClassification;
		case svm_parameter.ONE_CLASS:
			return SVMType.SupportVectorOneClass;
		case svm_parameter.EPSILON_SVR:
			return SVMType.EpsilonSupportVectorRegression;
		case svm_parameter.NU_SVR:
			return SVMType.NewSupportVectorRegression;
		default:
			return null;
		}
	}

	public void setInputCount(int i) {
		this.inputCount = i;
		
	}
	
	/**
	 * Calculate the error for this SVM. 
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final NeuralDataSet data) {
		
		switch (this.getSVMType()) {
		case SupportVectorClassification:
		case NewSupportVectorClassification:
		case SupportVectorOneClass:
			return EncogUtility.calculateClassificationError(this,data);
		case EpsilonSupportVectorRegression:
		case NewSupportVectorRegression:
			return EncogUtility.calculateRegressionError(this,data);
		}
		
		return EncogUtility.calculateRegressionError(this,data);
	}

	@Override
	public int classify(MLDataArray input) {
		if (this.model == null) {
			throw new EncogError(
					"Can't use the SVM yet, it has not been trained, and no model exists.");
		}

		svm_node[] formattedInput = makeSparse(input);
		return (int)svm.svm_predict(this.model, formattedInput);
	}
}
