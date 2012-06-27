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
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.train.AnnealFactory;
import org.encog.ml.factory.train.BackPropFactory;
import org.encog.ml.factory.train.ClusterSOMFactory;
import org.encog.ml.factory.train.GeneticFactory;
import org.encog.ml.factory.train.LMAFactory;
import org.encog.ml.factory.train.ManhattanFactory;
import org.encog.ml.factory.train.NeighborhoodSOMFactory;
import org.encog.ml.factory.train.NelderMeadFactory;
import org.encog.ml.factory.train.PNNTrainFactory;
import org.encog.ml.factory.train.PSOFactory;
import org.encog.ml.factory.train.QuickPropFactory;
import org.encog.ml.factory.train.RBFSVDFactory;
import org.encog.ml.factory.train.RPROPFactory;
import org.encog.ml.factory.train.SCGFactory;
import org.encog.ml.factory.train.SVMFactory;
import org.encog.ml.factory.train.SVMSearchFactory;
import org.encog.ml.factory.train.TrainBayesianFactory;
import org.encog.ml.train.MLTrain;
import org.encog.plugin.EncogPluginBase;
import org.encog.plugin.EncogPluginService1;

public class SystemTrainingPlugin implements EncogPluginService1 {
	
	/**
	 * The factory for K2
	 */
	private final TrainBayesianFactory bayesianFactory = new TrainBayesianFactory();
	
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
	 * The factory for simulated annealing.
	 */
	private final AnnealFactory annealFactory = new AnnealFactory();
	
	/**
	 * Nelder Mead Factory.
	 */
	private final NelderMeadFactory nmFactory = new NelderMeadFactory();
	
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
	 * The factory for genetic.
	 */
	private final GeneticFactory geneticFactory = new GeneticFactory();
	
	/**
	 * The factory for Manhattan networks.
	 */
	private final ManhattanFactory manhattanFactory = new ManhattanFactory();
	
	/**
	 * Factory for SVD.
	 */
	private final RBFSVDFactory svdFactory = new RBFSVDFactory();
	
	/**
	 * Factory for PNN.
	 */
	private final PNNTrainFactory pnnFactory = new PNNTrainFactory();

	/**
	 * Factory for quickprop.
	 */
	private final QuickPropFactory qpropFactory = new QuickPropFactory(); 
	
	private final PSOFactory psoFactory = new PSOFactory();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This plugin provides the built in training " +
				"methods for Encog.";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Training";
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}

	/**
	 * This plugin does not support activation functions, so it will 
	 * always return null.
	 * @return Null, because this plugin does not support activation functions.
	 */
	@Override
	public ActivationFunction createActivationFunction(String name) {
		return null;
	}

	@Override
	public MLMethod createMethod(String methodType, String architecture,
			int input, int output) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLTrain createTraining(MLMethod method, MLDataSet training,
			String type, String args) {
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
		} else if (MLTrainFactory.TYPE_ANNEAL.equalsIgnoreCase(type)) {
			return this.annealFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_GENETIC.equalsIgnoreCase(type)) {
			return this.geneticFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_SOM_CLUSTER.equalsIgnoreCase(type)) {
			return this.somClusterFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_MANHATTAN.equalsIgnoreCase(type)) {
			return this.manhattanFactory.create(method, training, args2);
		}  else if (MLTrainFactory.TYPE_SVD.equalsIgnoreCase(type)) {
			return this.svdFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_PNN.equalsIgnoreCase(type)) {
			return this.pnnFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_QPROP.equalsIgnoreCase(type)) {
			return this.qpropFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_BAYESIAN.equals(type) ) {
			return this.bayesianFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_NELDER_MEAD.equals(type) ) {
			return this.nmFactory.create(method, training, args2);
		} else if (MLTrainFactory.TYPE_PSO.equals(type) ) {
			return this.psoFactory.create(method, training, args2);
		}
		else {
			throw new EncogError("Unknown training type: " + type);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPluginServiceType() {
		return EncogPluginBase.TYPE_SERVICE;
	}
}
