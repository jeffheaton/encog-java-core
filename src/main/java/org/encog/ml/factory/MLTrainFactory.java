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
package org.encog.ml.factory;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.factory.train.AnnealFactory;
import org.encog.ml.factory.train.BackPropFactory;
import org.encog.ml.factory.train.ClusterSOMFactory;
import org.encog.ml.factory.train.GeneticFactory;
import org.encog.ml.factory.train.LMAFactory;
import org.encog.ml.factory.train.ManhattanFactory;
import org.encog.ml.factory.train.NeighborhoodSOMFactory;
import org.encog.ml.factory.train.PNNTrainFactory;
import org.encog.ml.factory.train.QuickPropFactory;
import org.encog.ml.factory.train.RBFSVDFactory;
import org.encog.ml.factory.train.RPROPFactory;
import org.encog.ml.factory.train.SCGFactory;
import org.encog.ml.factory.train.SVMFactory;
import org.encog.ml.factory.train.SVMSearchFactory;
import org.encog.ml.train.MLTrain;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;

/**
 * This factory is used to create trainers for machine learning methods.
 *
 */
public class MLTrainFactory {
	
	/**
	 * K2 training for Bayesian.
	 */
	public static final String TYPE_NELDER_MEAD = "nm";
	
	/**
	 * K2 training for Bayesian.
	 */
	public static final String TYPE_BAYESIAN = "bayesian";
	
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
	 * The number of cycles.
	 */
	public static final String CYCLES = "cycles";

	/**
	 * The starting temperature.
	 */
	public static final String PROPERTY_TEMPERATURE_START = "startTemp";

	/**
	 * The ending temperature.
	 */
	public static final String PROPERTY_TEMPERATURE_STOP = "stopTemp";

	/**
	 * Use simulated annealing.
	 */
	public static final String TYPE_ANNEAL = "anneal";

	/**
	 * Population size.
	 */
	public static final String PROPERTY_POPULATION_SIZE = "population";

	/**
	 * Percent to mutate.
	 */
	public static final String PROPERTY_MUTATION = "mutate";

	/**
	 * Percent to mate.
	 */
	public static final String PROPERTY_MATE = "mate";

	/**
	 * Genetic training.
	 */
	public static final String TYPE_GENETIC = "genetic";

	/**
	 * Manhattan training.
	 */
	public static final String TYPE_MANHATTAN = "manhattan";

	/**
	 * RBF-SVD training.
	 */
	public static final String TYPE_SVD = "rbf-svd";

	/**
	 * PNN training.
	 */
	public static final String TYPE_PNN = "pnn";
	
	/**
	 * QPROP training.
	 */
	public static final String TYPE_QPROP = "qprop";

	public static final String PROPERTY_MAX_PARENTS = "MAXPARENTS";

	public static final String PROPERTY_PARTICLES = "PARTICLES";

	public static final String TYPE_PSO = "pso";

	
	/**
	 * Create a trainer.
	 * @param method The method to train.
	 * @param training The training data.
	 * @param type Type type of trainer.
	 * @param args The training args.
	 * @return The new training method.
	 */
	public final MLTrain create(final MLMethod method, 
			final MLDataSet training,
			final String type, final String args) {
		
		for (EncogPluginBase plugin : Encog.getInstance().getPlugins()) {
			if (plugin instanceof EncogPluginService1) {
				MLTrain result = ((EncogPluginService1) plugin).createTraining(
						method, training, type, args);
				if (result != null) {
					return result;
				}
			}
		}
		throw new EncogError("Unknown training type: " + type);
	}
}
