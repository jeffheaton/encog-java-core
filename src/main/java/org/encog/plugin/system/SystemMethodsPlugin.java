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
package org.encog.plugin.system;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.method.BayesianFactory;
import org.encog.ml.factory.method.FeedforwardFactory;
import org.encog.ml.factory.method.PNNFactory;
import org.encog.ml.factory.method.RBFNetworkFactory;
import org.encog.ml.factory.method.SOMFactory;
import org.encog.ml.factory.method.SVMFactory;
import org.encog.ml.train.MLTrain;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;

/**
 * The system machine learning methods plugin.  This provides all of the 
 * built in machine learning methods for Encog.
 */
public class SystemMethodsPlugin implements EncogPluginService1 {

	/**
	 * A factory used to create feedforward neural networks.
	 */
	private final FeedforwardFactory feedforwardFactory 
		= new FeedforwardFactory();
	
	/**
	 * A factory used to create support vector machines.
	 */
	private final SVMFactory svmFactory = new SVMFactory();
	
	/**
	 * A factory used to create RBF networks.
	 */
	private final RBFNetworkFactory rbfFactory = new RBFNetworkFactory();
	
	/**
	 * The factory for PNN's.
	 */
	private final PNNFactory pnnFactory = new PNNFactory();
	
	/**
	 * A factory used to create SOM's.
	 */
	private final SOMFactory somFactory = new SOMFactory();
	
	/**
	 * A factory used to create Bayesian networks
	 */
	private final BayesianFactory bayesianFactory = new BayesianFactory();

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This plugin provides the built in machine " +
				"learning methods for Encog.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Methods";
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActivationFunction createActivationFunction(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLMethod createMethod(String methodType, String architecture,
			int input, int output) {
		// TODO Auto-generated method stub
		if (MLMethodFactory.TYPE_FEEDFORWARD.equals(methodType)) {
			return this.feedforwardFactory.create(architecture, input, output);
		} else if (MLMethodFactory.TYPE_RBFNETWORK.equals(methodType)) {
			return this.rbfFactory.create(architecture, input, output);
		} else if (MLMethodFactory.TYPE_SVM.equals(methodType)) {
			return this.svmFactory.create(architecture, input, output);
		} else if (MLMethodFactory.TYPE_SOM.equals(methodType)) {
			return this.somFactory.create(architecture, input, output);
		} else if (MLMethodFactory.TYPE_PNN.equals(methodType)) {
			return this.pnnFactory.create(architecture, input, output);
		} else if (MLMethodFactory.TYPE_BAYESIAN.equals(methodType)) {
			return this.bayesianFactory.create(architecture, input, output);
		}
		throw new EncogError("Unknown method type: " + methodType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLTrain createTraining(MLMethod method, MLDataSet training,
			String type, String args) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPluginServiceType() {
		return EncogPluginBase.TYPE_SERVICE;
	}
}
