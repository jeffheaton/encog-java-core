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
package org.encog.ml.factory;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.method.FeedforwardFactory;
import org.encog.ml.factory.method.PNNFactory;
import org.encog.ml.factory.method.RBFNetworkFactory;
import org.encog.ml.factory.method.SOMFactory;
import org.encog.ml.factory.method.SVMFactory;

/**
 * This factory is used to create machine learning methods.
 */
public class MLMethodFactory {

	/**
	 * String constant for feedforward neural networks.
	 */
	public static final String TYPE_FEEDFORWARD = "feedforward";
	
	/**
	 * String constant for RBF neural networks.
	 */
	public static final String TYPE_RBFNETWORK = "rbfnetwork";
	
	/**
	 * String constant for support vector machines.
	 */
	public static final String TYPE_SVM = "svm";
	
	/**
	 * String constant for SOMs.
	 */
	public static final String TYPE_SOM = "som";

	/**
	 * A probabilistic neural network. Supports both PNN & GRNN.
	 */
	public static final String TYPE_PNN = "pnn";

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
	 * Create a new machine learning method.
	 * @param methodType The method to create.
	 * @param architecture The architecture string.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The newly created machine learning method.
	 */
	public final MLMethod create(final String methodType, 
			final String architecture,
			final int input, final int output) {
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
		}
		throw new EncogError("Unknown method type: " + methodType);
	}

}
