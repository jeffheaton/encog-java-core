/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.map.PersistConst;
import org.encog.persist.map.PersistedObject;

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
public class SVM extends BasicPersistedObject implements MLRegression {

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

	public static final String MODEL_NCLASS = "nclass";
	public static final String MODEL_L = "l";
	public static final String MODEL_RHO = "rho";
	public static final String MODEL_PROB_A = "proba";
	public static final String MODEL_PROB_B = "probb";
	public static final String MODEL_NODES = "nodes";
	public static final String MODEL_COEF = "coef";
	public static final String MODEL_LABEL = "label";
	public static final String MODEL_NSV = "nsv";
	
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
	
	public SVM()
	{
		
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
	public SVM(int inputCount, int outputCount, SVMType svmType,
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

			//			params[i].kernel_type = svm_parameter.RBF;
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
	public SVM(int inputCount, int outputCount, boolean regression) {
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

	public boolean supportsMapPersistence() {
		return true;
	}

	public void persistToMap(PersistedObject obj) {
		obj.clear(PersistConst.TYPE_SVM);
		obj.setStandardProperties(this);
		obj.setProperty(PersistConst.TYPE, svmTypeToString(this.svmType), false);
		obj.setProperty(SVM.PARAMETER_KERNEL_TYPE, kernelTypeToString(this.kernelType), false);
		obj.setProperty(PersistConst.INPUT_COUNT, this.inputCount, false);
		obj.setProperty(PersistConst.OUTPUT_COUNT, this.outputCount, false);
		
		// handle the params
		List<PersistedObject> persistedParams = new ArrayList<PersistedObject>();
		for(svm_parameter param: this.params)
		{
			PersistedObject pparm = new PersistedObject();
			pparm.clear("Params");
			pparm.setProperty(SVM.PARAM_DEGREE, param.degree, true);
			pparm.setProperty(SVM.PARAM_GAMMA, param.gamma, true);
			pparm.setProperty(SVM.PARAM_COEF, param.coef0, true);
			pparm.setProperty(SVM.PARAM_CACHE_SIZE, param.cache_size, true);
			pparm.setProperty(SVM.PARAM_EPS, param.eps, true);
			pparm.setProperty(SVM.PARAM_C, param.C, true);
			pparm.setProperty(SVM.PARAM_NUM_WEIGHT, param.nr_weight, true);
			pparm.setProperty(SVM.PARAM_WEIGHT_LABEL, param.weight_label);
			pparm.setProperty(PersistConst.WEIGHT, param.weight);
			pparm.setProperty(SVM.PARAM_NU, param.nu, true);
			pparm.setProperty(SVM.PARAM_P, param.p, true);
			pparm.setProperty(SVM.PARAM_SHRINKING, param.shrinking, true);
			pparm.setProperty(SVM.PARAM_PROBABILITY, param.probability, true);
			pparm.setProperty(SVM.PARAM_DEGREE, param.statIterations, true);
			persistedParams.add(pparm);
		}
		
		obj.setProperty(SVM.PARAMETER_PARAMS,persistedParams);
		
		// handle the models
		List<PersistedObject> persistedModels = new ArrayList<PersistedObject>();
		for(svm_model model: this.models)
		{
			PersistedObject pmodel = new PersistedObject();
			pmodel.clear("Model");
			
			pmodel.setProperty(SVM.MODEL_NCLASS, model.nr_class, true);
			pmodel.setProperty(SVM.MODEL_L, model.l, true);
			pmodel.setProperty(SVM.MODEL_RHO, model.rho);
			pmodel.setProperty(SVM.MODEL_PROB_A, model.probA );
			pmodel.setProperty(SVM.MODEL_PROB_B, model.probB );
//			pmodel.setProperty(SVM.MODEL_NODES,  = "nodes";
			pmodel.setProperty(SVM.MODEL_COEF, new Matrix( model.sv_coef ) );
			pmodel.setProperty(SVM.MODEL_LABEL, model.label );
			pmodel.setProperty(SVM.MODEL_NSV, model.nSV );
			persistedModels.add(pmodel);
		}
		
		obj.setProperty(SVM.PARAMETER_MODELS,persistedModels);
		
	}

	public void persistFromMap(PersistedObject obj) {
		obj.requireType(PersistConst.TYPE_SVM);
		this.svmType = stringToSVMType( obj.getPropertyString(PersistConst.TYPE, true) );
		this.kernelType = stringToKernelType( obj.getPropertyString(SVM.PARAMETER_KERNEL_TYPE, true) );
		this.inputCount = obj.getPropertyInt(PersistConst.INPUT_COUNT, true);
		this.outputCount = obj.getPropertyInt(PersistConst.OUTPUT_COUNT, true);
	}

}
