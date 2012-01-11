/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.NeuralNetworkError;
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
public class SVM extends BasicML implements MLRegression, MLClassification,
		MLError {
	
	/**
	 * The default degree.
	 */
	public static final int DEFAULT_DEGREE = 3;
	
	/**
	 * The default COEF0.
	 */
	public static final int DEFAULT_COEF0 = 0;
	
	/**
	 * The default NU.
	 */
	public static final double DEFAULT_NU = 0.5;
	
	/**
	 * The default cache size.
	 */
	public static final int DEFAULT_CACHE_SIZE = 100;
	
	/**
	 * The default C.
	 */
	public static final int DEFAULT_C = 1;
	
	/**
	 * The default EPS.
	 */
	public static final double DEFAULT_EPS = 1e-3;
	
	/**
	 * The default P.
	 */
	public static final double DEFAULT_P = 0.1;

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
	private final svm_parameter params;

	/**
	 * The input count.
	 */
	private int inputCount;

	/**
	 * Construct the SVM.
	 */
	public SVM() {
		this.params = new svm_parameter();
	}

	/**
	 * Construct an SVM network. For regression it will use an epsilon support
	 * vector. Both types will use an RBF kernel.
	 * 
	 * @param theInputCount
	 *            The input count.
	 * @param regression
	 *            True if this network is used for regression.
	 */
	public SVM(final int theInputCount, final boolean regression) {
		this(theInputCount, regression ? SVMType.EpsilonSupportVectorRegression
				: SVMType.SupportVectorClassification,
				KernelType.RadialBasisFunction);
	}

	/**
	 * Construct a SVM network.
	 * 
	 * @param theInputCount
	 *            The input count.
	 * @param svmType
	 *            The type of SVM.
	 * @param kernelType
	 *            The SVM kernal type.
	 */
	public SVM(final int theInputCount, final SVMType svmType,
			final KernelType kernelType) {
		this.inputCount = theInputCount;

		this.params = new svm_parameter();

		switch (svmType) {
		case SupportVectorClassification:
			this.params.svm_type = svm_parameter.C_SVC;
			break;
		case NewSupportVectorClassification:
			this.params.svm_type = svm_parameter.NU_SVC;
			break;
		case SupportVectorOneClass:
			this.params.svm_type = svm_parameter.ONE_CLASS;
			break;
		case EpsilonSupportVectorRegression:
			this.params.svm_type = svm_parameter.EPSILON_SVR;
			break;
		case NewSupportVectorRegression:
			this.params.svm_type = svm_parameter.NU_SVR;
			break;
		default:
			throw new NeuralNetworkError("Invalid svm type");
		}

		switch (kernelType) {
		case Linear:
			this.params.kernel_type = svm_parameter.LINEAR;
			break;
		case Poly:
			this.params.kernel_type = svm_parameter.POLY;
			break;
		case RadialBasisFunction:
			this.params.kernel_type = svm_parameter.RBF;
			break;
		case Sigmoid:
			this.params.kernel_type = svm_parameter.SIGMOID;
			break;
		case Precomputed:
			this.params.kernel_type = svm_parameter.PRECOMPUTED;
			break;
		default:
			throw new NeuralNetworkError("Invalid kernel type");
		}

		// params[i].kernel_type = svm_parameter.RBF;
		this.params.degree = DEFAULT_DEGREE;
		this.params.coef0 = 0;
		this.params.nu = DEFAULT_NU;
		this.params.cache_size = DEFAULT_CACHE_SIZE;
		this.params.C = 1;
		this.params.eps = DEFAULT_EPS;
		this.params.p = DEFAULT_P;
		this.params.shrinking = 1;
		this.params.probability = 0;
		this.params.nr_weight = 0;
		this.params.weight_label = new int[0];
		this.params.weight = new double[0];
		this.params.gamma = 1.0 / inputCount;

	}

	/**
	 * Construct a SVM from a model.
	 * @param theModel The model.
	 */
	public SVM(final svm_model theModel) {
		this.model = theModel;
		this.params = model.param;
		this.inputCount = 0;

		// determine the input count
		for (final svm_node[] element : this.model.SV) {
			for (int col = 0; col < element.length; col++) {
				this.inputCount = Math.max(element[col].index, this.inputCount);
			}
		}

		//
	}

	/**
	 * Calculate the error for this SVM.
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	@Override
	public final double calculateError(final MLDataSet data) {

		switch (getSVMType()) {
		case SupportVectorClassification:
		case NewSupportVectorClassification:
		case SupportVectorOneClass:
			return EncogUtility.calculateClassificationError(this, data);
		case EpsilonSupportVectorRegression:
		case NewSupportVectorRegression:
			return EncogUtility.calculateRegressionError(this, data);
		default:
			return EncogUtility.calculateRegressionError(this, data);
		}

		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int classify(final MLData input) {
		if (this.model == null) {
			throw new EncogError(
					"Can't use the SVM yet, it has not been trained, " 
					+ "and no model exists.");
		}

		final svm_node[] formattedInput = makeSparse(input);
		return (int) svm.svm_predict(this.model, formattedInput);
	}

	/**
	 * Compute the output for the given input.
	 * 
	 * @param input
	 *            The input to the SVM.
	 * @return The results from the SVM.
	 */
	@Override
	public final MLData compute(final MLData input) {

		if (this.model == null) {
			throw new EncogError(
					"Can't use the SVM yet, it has not been trained, "
					+ "and no model exists.");
		}

		final MLData result = new BasicMLData(1);

		final svm_node[] formattedInput = makeSparse(input);

		final double d = svm.svm_predict(this.model, formattedInput);
		result.setData(0, d);

		return result;
	}

	/**
	 * @return The input count.
	 */
	@Override
	public final int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The kernel type.
	 */
	public final KernelType getKernelType() {
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

	/**
	 * @return The SVM models for each output.
	 */
	public final svm_model getModel() {
		return this.model;
	}

	/**
	 * @return For a SVM, the output count is always one.
	 */
	@Override
	public final int getOutputCount() {
		return 1;
	}

	/**
	 * @return The SVM params for each of the outputs.
	 */
	public final svm_parameter getParams() {
		return this.params;
	}

	/**
	 * @return The SVM type.
	 */
	public final SVMType getSVMType() {
		switch (this.params.svm_type) {
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

	/**
	 * Convert regular Encog MLData into the "sparse" data needed by an SVM.
	 * 
	 * @param data
	 *            The data to convert.
	 * @return The SVM sparse data.
	 */
	public final svm_node[] makeSparse(final MLData data) {
		final svm_node[] result = new svm_node[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = new svm_node();
			result[i].index = i + 1;
			result[i].value = data.getData(i);
		}

		return result;
	}

	/**
	 * Set the input count.
	 * @param i The new input count.
	 */
	public final void setInputCount(final int i) {
		this.inputCount = i;

	}

	/**
	 * Set the model.
	 * @param theModel The model.
	 */
	public final void setModel(final svm_model theModel) {
		this.model = theModel;

	}

	/**
	 * Not needed, no properties to update.
	 */
	@Override
	public void updateProperties() {
		// unneeded
	}
}
