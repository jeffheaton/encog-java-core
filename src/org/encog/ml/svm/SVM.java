/*
 * Encog(tm) Core v3.0 - Java Version
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

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.BasicML;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

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
public class SVM extends BasicML implements MLRegression {

	public static final String PARAMETER_KERNEL_TYPE = "kernelType";
	public static final String PARAMETER_MODELS = "models";
	public static final String PARAMETER_PARAMS = "params";

	public static final String PARAM_DEGREE = "degree";
	public static final String PARAM_GAMMA = "gama";
	public static final String PARAM_COEF = "coef";
	public static final String PARAM_CACHE_SIZE = "cacheSize";
	public static final String PARAM_EPS = "eps";
	public static final String PARAM_C = "c";
	public static final String PARAM_NUM_WEIGHT = "numWeight";
	public static final String PARAM_WEIGHT_LABEL = "weightLabel";
	public static final String PARAM_NU = "nu";
	public static final String PARAM_P = "p";
	public static final String PARAM_SHRINKING = "shrinking";
	public static final String PARAM_PROBABILITY = "probability";
	public static final String PARAM_STAT_ITERATIONS = "statIterations";

	public static final String MODEL_NCLASS = "modelNclass";
	public static final String MODEL_L = "modelL";
	public static final String MODEL_RHO = "modelRho";
	public static final String MODEL_PROB_A = "modelProba";
	public static final String MODEL_PROB_B = "modelProbb";
	public static final String MODEL_NODES = "modelNodes";
	public static final String MODEL_COEF = "modelCoef";
	public static final String MODEL_LABEL = "modelLabel";
	public static final String MODEL_NSV = "modelNsv";

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

	/**
	 * The kernel type.
	 */
	private KernelType kernelType;

	/**
	 * The SVM type.
	 */
	private SVMType svmType;

	public static String svmTypeToString(SVMType t) {
		switch (t) {
		case SupportVectorClassification:
			return "SupportVectorClassification";
		case NewSupportVectorClassification:
			return "NewSupportVectorClassification";
		case SupportVectorOneClass:
			return "SupportVectorOneClass";
		case EpsilonSupportVectorRegression:
			return "EpsilonSupportVectorRegression";
		case NewSupportVectorRegression:
			return "EpsilonSupportVectorRegression";
		default:
			throw new PersistError("Unknown SVMType: " + t);
		}
	}

	public static SVMType stringToSVMType(String str) {
		if (str.equals("SupportVectorClassification"))
			return SVMType.SupportVectorClassification;
		else if (str.equals("NewSupportVectorClassification"))
			return SVMType.NewSupportVectorClassification;
		else if (str.equals("SupportVectorOneClass"))
			return SVMType.SupportVectorOneClass;
		else if (str.equals("EpsilonSupportVectorRegression"))
			return SVMType.EpsilonSupportVectorRegression;
		else if (str.equals("NewSupportVectorRegression"))
			return SVMType.EpsilonSupportVectorRegression;
		else
			throw new PersistError("Unknown SVMType: " + str);
	}

	public static String kernelTypeToString(KernelType t) {
		switch (t) {
		case Linear:
			return "Linear";
		case Poly:
			return "Poly";
		case RadialBasisFunction:
			return "RadialBasisFunction";
		case Sigmoid:
			return "Sigmoid";
		case Precomputed:
			return "Precomputed";
		default:
			throw new PersistError("Unknown SVMType: " + t);
		}
	}

	public static KernelType stringToKernelType(String t) {
		if (t.equals("Linear"))
			return KernelType.Linear;
		else if (t.equals("Poly"))
			return KernelType.Poly;
		else if (t.equals("RadialBasisFunction"))
			return KernelType.RadialBasisFunction;
		else if (t.equals("Sigmoid"))
			return KernelType.Sigmoid;
		else if (t.equals("Precomputed"))
			return KernelType.Precomputed;
		else
			throw new PersistError("Unknown SVMType: " + t);
	}

	public SVM() {

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
		this.kernelType = kernelType;
		this.svmType = svmType;

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

		//			params[i].kernel_type = svm_parameter.RBF;
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
	 * @param input The input to the SVM.
	 * @return The results from the SVM.
	 */
	public NeuralData compute(NeuralData input) {
		
		if( this.model==null ) {
			throw new EncogError("Can't use the SVM yet, it has not been trained, and no model exists.");
		}
		
		NeuralData result = new BasicNeuralData(1);

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

	public boolean supportsMapPersistence() {
		return true;
	}

	public void persistToMap(PersistedObject obj) {
		obj.clear(PersistConst.TYPE_SVM);
		obj.setStandardProperties(this);
		obj.setProperty(PersistConst.TYPE, svmTypeToString(this.svmType), false);
		obj.setProperty(SVM.PARAMETER_KERNEL_TYPE,
				kernelTypeToString(this.kernelType), false);
		obj.setProperty(PersistConst.INPUT_COUNT, this.inputCount, false);

		// handle the params

		PersistedObject pparm = new PersistedObject();
		pparm.clear("Params");
		obj.setProperty(SVM.PARAM_DEGREE, params.degree, true);
		obj.setProperty(SVM.PARAM_GAMMA, params.gamma, true);
		obj.setProperty(SVM.PARAM_COEF, params.coef0, true);
		obj.setProperty(SVM.PARAM_CACHE_SIZE, params.cache_size, true);
		obj.setProperty(SVM.PARAM_EPS, params.eps, true);
		obj.setProperty(SVM.PARAM_C, params.C, true);
		obj.setProperty(SVM.PARAM_NUM_WEIGHT, params.nr_weight, true);
		obj.setProperty(SVM.PARAM_WEIGHT_LABEL, params.weight_label);
		obj.setProperty(PersistConst.WEIGHT, params.weight);
		obj.setProperty(SVM.PARAM_NU, params.nu, true);
		obj.setProperty(SVM.PARAM_P, params.p, true);
		obj.setProperty(SVM.PARAM_SHRINKING, params.shrinking, true);
		obj.setProperty(SVM.PARAM_PROBABILITY, params.probability, true);
		obj.setProperty(SVM.PARAM_DEGREE, params.statIterations, true);

		// handle the models

		if (model != null) {
			obj.setProperty(SVM.MODEL_NCLASS, model.nr_class, true);
			obj.setProperty(SVM.MODEL_L, model.l, true);
			obj.setProperty(SVM.MODEL_RHO, model.rho);
			obj.setProperty(SVM.MODEL_PROB_A, model.probA);
			obj.setProperty(SVM.MODEL_PROB_B, model.probB);
			obj.setProperty(SVM.MODEL_NODES, svmNodeToString(model.SV), false);
			obj.setProperty(SVM.MODEL_COEF, new Matrix(model.sv_coef));
			obj.setProperty(SVM.MODEL_LABEL, model.label);
			obj.setProperty(SVM.MODEL_NSV, model.nSV);
		}
	}

	public void persistFromMap(PersistedObject obj) {
		obj.requireType(PersistConst.TYPE_SVM);
		this.svmType = stringToSVMType(obj.getPropertyString(PersistConst.TYPE,
				true));
		this.kernelType = stringToKernelType(obj.getPropertyString(
				SVM.PARAMETER_KERNEL_TYPE, true));
		this.inputCount = obj.getPropertyInt(PersistConst.INPUT_COUNT, true);

		// read params
		this.params = new svm_parameter();
		int index = 0;

		this.params = new svm_parameter();
		this.params.C = obj.getPropertyDouble(SVM.PARAM_C, true);
		this.params.cache_size = obj.getPropertyDouble(SVM.PARAM_CACHE_SIZE,
				true);
		this.params.coef0 = obj.getPropertyDouble(SVM.PARAM_COEF, true);

		this.params.degree = obj.getPropertyInt(SVM.PARAM_DEGREE, true);
		this.params.gamma = obj.getPropertyDouble(SVM.PARAM_GAMMA, true);
		this.params.eps = obj.getPropertyDouble(SVM.PARAM_EPS, true);
		this.params.nr_weight = obj.getPropertyInt(SVM.PARAM_NUM_WEIGHT, true);
		this.params.weight_label = obj.getPropertyIntArray(
				SVM.PARAM_WEIGHT_LABEL, true);
		this.params.weight = obj.getPropertyDoubleArray(PersistConst.WEIGHT,
				true);
		this.params.nu = obj.getPropertyDouble(SVM.PARAM_NU, true);
		this.params.p = obj.getPropertyDouble(SVM.PARAM_P, true);
		this.params.shrinking = obj.getPropertyInt(SVM.PARAM_SHRINKING, true);
		this.params.probability = obj.getPropertyInt(SVM.PARAM_PROBABILITY,
				true);

		// model

		int l = obj.getPropertyInt(SVM.MODEL_L, false);

		if (l > 0) {
			this.model = new svm_model();

			this.model = new svm_model();
			this.model.nr_class = obj.getPropertyInt(SVM.MODEL_NCLASS, true);

			this.model.l = l;
			this.model.rho = obj.getPropertyDoubleArray(SVM.MODEL_RHO, true);
			this.model.probA = obj.getPropertyDoubleArray(SVM.MODEL_PROB_A,
					false);
			this.model.probB = obj.getPropertyDoubleArray(SVM.MODEL_PROB_B,
					false);
			this.model.SV = svmStringToNode(
					obj.getPropertyString(SVM.MODEL_NODES, true), l);
			this.model.sv_coef = obj.getPropertyMatrix(SVM.MODEL_COEF, true)
					.getData();
			this.model.label = obj.getPropertyIntArray(SVM.MODEL_LABEL, false);
			this.model.nSV = obj.getPropertyIntArray(SVM.MODEL_NSV, false);
			this.model.param = this.params;
		} else {
			this.model = null;
		}
	}

	public static svm_node[][] svmStringToNode(String str, int l) {
		int strIndex = 0;
		svm_node[][] result = new svm_node[l][];

		int index = 0;
		int current;
		while ((current = str.indexOf('|', strIndex)) != -1) {
			String str2 = str.substring(strIndex, current);
			strIndex = current + 1;

			double[] temp = NumberList.fromList(CSVFormat.EG_FORMAT, str2);
			result[index] = new svm_node[temp.length];
			for (int i = 0; i < temp.length; i++) {
				result[index][i] = new svm_node();
				result[index][i].index = i + 1;
				result[index][i].value = temp[i];
			}

			index++;
		}

		return result;
	}

	public static String svmNodeToString(svm_node[][] nodes) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < nodes.length; i++) {
			for (int j = 1; j < (nodes[0].length + 1); j++) {
				for (svm_node node : nodes[i]) {
					if (node.index == j) {
						if (j > 1)
							result.append(',');
						result.append(CSVFormat.ENGLISH.format(node.value,
								Encog.DEFAULT_PRECISION));
					}
				}
			}

			result.append("|");
		}

		return result.toString();
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
}
