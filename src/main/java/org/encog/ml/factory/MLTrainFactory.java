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
import org.encog.ml.MLTrain;
import org.encog.ml.factory.train.BackPropFactory;
import org.encog.ml.factory.train.ClusterSOMFactory;
import org.encog.ml.factory.train.LMAFactory;
import org.encog.ml.factory.train.NeighborhoodSOMFactory;
import org.encog.ml.factory.train.RPROPFactory;
import org.encog.ml.factory.train.SCGFactory;
import org.encog.ml.factory.train.SVMFactory;
import org.encog.ml.factory.train.SVMSearchFactory;
import org.encog.neural.data.NeuralDataSet;

/**
 * This factory is used to create trainers for machine learning methods.
 *
 */
public class MLTrainFactory {
	
	/**
	 * String constant for RPROP training.
	 */
	public static final String TYPE_RPROP = "rprop";
	
	/**
	 * String constant for backprop training.
	 */
	public static final String TYPE_BACKPROP = "backprop";
	
	/**
	 * String constant for SCG training.
	 */
	public static final String TYPE_SCG = "scg";
	
	/**
	 * String constant for LMA training.
	 */
	public static final String TYPE_LMA = "lma";
	
	/**
	 * String constant for SVM training.
	 */
	public static final String TYPE_SVM = "svm-train";
	
	/**
	 * String constant for SVM-Search training.
	 */
	public static final String TYPE_SVM_SEARCH = "svm-search";
	
	/**
	 * String constant for SOM-Neighborhood training.
	 */
	public static final String TYPE_SOM_NEIGHBORHOOD = "som-neighborhood";
	
	/**
	 * String constant for SOM-Cluster training.
	 */
	public static final String TYPE_SOM_CLUSTER = "som-cluster";

	/**
	 * Property for learning rate.
	 */
	public static final String PROPERTY_LEARNING_RATE = "LR";
	
	/**
	 * Property for momentum.
	 */
	public static final String PROPERTY_LEARNING_MOMENTUM = "MOM";
	
	/**
	 * Property for init update.
	 */
	public static final String PROPERTY_INITIAL_UPDATE = "INIT_UPDATE";
	
	/**
	 * Property for max step.
	 */
	public static final String PROPERTY_MAX_STEP = "MAX_STEP";
	
	/**
	 * Property for bayes reg.
	 */
	public static final String PROPERTY_BAYESIAN_REGULARIZATION = "BAYES_REG";
	
	/**
	 * Property for gamma.
	 */
	public static final String PROPERTY_GAMMA = "GAMMA";
	
	/**
	 * Property for constant.
	 */
	public static final String PROPERTY_C = "C";
	
	/**
	 * Property for neighborhood.
	 */
	public static final String PROPERTY_PROPERTY_NEIGHBORHOOD = "NEIGHBORHOOD";
	
	/**
	 * Property for iterations.
	 */
	public static final String PROPERTY_ITERATIONS = "ITERATIONS";
	
	/**
	 * Property for starting learning rate.
	 */
	public static final String PROPERTY_START_LEARNING_RATE = "START_LR";
	
	/**
	 * Property for ending learning rate.
	 */
	public static final String PROPERTY_END_LEARNING_RATE = "END_LR";
	
	/**
	 * Property for starting radius.
	 */
	public static final String PROPERTY_START_RADIUS = "START_RADIUS";
	
	/**
	 * Property for ending radius.
	 */
	public static final String PROPERTY_END_RADIUS = "END_RADIUS";
	
	/**
	 * Property for neighborhood.
	 */
	public static final String PROPERTY_NEIGHBORHOOD = "NEIGHBORHOOD";
	
	/**
	 * Property for rbf type.
	 */
	public static final String PROPERTY_RBF_TYPE = "RBF_TYPE";
	
	/**
	 * Property for dimensions.
	 */
	public static final String PROPERTY_DIMENSIONS = "DIM";

	/**
	 * The factory for backprop.
	 */
	private final BackPropFactory backpropFactory = new BackPropFactory();
	
	/**
	 * The factory for LMA.
	 */
	private final LMAFactory lmaFactory = new LMAFactory();
	
	/**
	 * The factory for RPROP.
	 */
	private final RPROPFactory rpropFactory = new RPROPFactory();
	
	/**
	 * THe factory for basic SVM.
	 */
	private final SVMFactory svmFactory = new SVMFactory();
	
	/**
	 * The factory for SVM-Search.
	 */
	private final SVMSearchFactory svmSearchFactory = new SVMSearchFactory();
	
	/**
	 * The factory for SCG.
	 */
	private final SCGFactory scgFactory = new SCGFactory();
	
	/**
	 * The factory for neighborhood SOM.
	 */
	private final NeighborhoodSOMFactory neighborhoodFactory 
		= new NeighborhoodSOMFactory();
	
	/**
	 * The factory for SOM cluster.
	 */
	private final ClusterSOMFactory somClusterFactory = new ClusterSOMFactory();

	/**
	 * Create a trainer.
	 * @param method The method to train.
	 * @param training The training data.
	 * @param type Type type of trainer.
	 * @param args The training args.
	 * @return The new training method.
	 */
	public final MLTrain create(final MLMethod method, 
			final NeuralDataSet training,
			final String type, final String args) {

		String args2 = args;

		if (args2 == null) {
			args2 = "";
		}

		if (MLTrainFactory.TYPE_RPROP.equalsIgnoreCase(type)) {
			return this.rpropFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_BACKPROP.equalsIgnoreCase(type)) {
			return this.backpropFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SCG.equalsIgnoreCase(type)) {
			return this.scgFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_LMA.equalsIgnoreCase(type)) {
			return this.lmaFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SVM.equalsIgnoreCase(type)) {
			return this.svmFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SVM_SEARCH.equalsIgnoreCase(type)) {
			return this.svmSearchFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SOM_NEIGHBORHOOD.equalsIgnoreCase(
				type)) {
			return this.neighborhoodFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SOM_CLUSTER.equalsIgnoreCase(type)) {
			return this.somClusterFactory.create(method, training, args2);
		} else {
			throw new EncogError("Unknown training type: " + type);
		}
	}
}
