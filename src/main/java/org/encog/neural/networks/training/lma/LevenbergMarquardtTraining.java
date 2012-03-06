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
package org.encog.neural.networks.training.lma;

import org.encog.mathutil.error.ErrorCalculation;
import org.encog.mathutil.matrices.decomposition.LUDecomposition;
import org.encog.mathutil.matrices.hessian.ComputeHessian;
import org.encog.mathutil.matrices.hessian.HessianCR;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.validate.ValidateNetwork;

/**
 * Trains a neural network using a Levenberg Marquardt algorithm (LMA). This
 * training technique is based on the mathematical technique of the same name.
 * 
 * The LMA interpolates between the Gauss-Newton algorithm (GNA) and the 
 * method of gradient descent (similar to what is used by backpropagation. 
 * The lambda parameter determines the degree to which GNA and Gradient 
 * Descent are used.  A lower lambda results in heavier use of GNA, 
 * whereas a higher lambda results in a heavier use of gradient descent.
 * 
 * Each iteration starts with a low lambda that builds if the improvement 
 * to the neural network is not desirable.  At some point the lambda is
 * high enough that the training method reverts totally to gradient descent.
 * 
 * This allows the neural network to be trained effectively in cases where GNA
 * provides the optimal training time, but has the ability to fall back to the
 * more primitive gradient descent method
 *
 * LMA finds only a local minimum, not a global minimum.
 *  
 * References:
 * http://www.heatonresearch.com/wiki/LMA
 * http://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm
 * http://en.wikipedia.org/wiki/Finite_difference_method
 * http://crsouza.blogspot.com/2009/11/neural-network-learning-by-levenberg_18.html
 * http://mathworld.wolfram.com/FiniteDifference.html 
 * http://www-alg.ist.hokudai.ac.jp/~jan/alpha.pdf -
 * http://www.inference.phy.cam.ac.uk/mackay/Bayes_FAQ.html
 * 
 */
public class LevenbergMarquardtTraining extends BasicTraining implements MultiThreadable {

	/**
	 * The amount to scale the lambda by.
	 */
	public static final double SCALE_LAMBDA = 10.0;

	/**
	 * The max amount for the LAMBDA.
	 */
	public static final double LAMBDA_MAX = 1e25;
	
	/**
	 * Utility class to compute the Hessian.
	 */
	private ComputeHessian hessian;

	/**
	 * The network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * The training set that we are using to train.
	 */
	private final MLDataSet indexableTraining;

	/**
	 * The training set length.
	 */
	private final int trainingLength;

	/**
	 * How many weights are we dealing with?
	 */
	private final int weightCount;

	/**
	 * The neural network weights and bias values.
	 */
	private double[] weights;

	/**
	 * The lambda, or damping factor. This is increased until a desirable
	 * adjustment is found.
	 */
	private double lambda;

	/**
	 * The diagonal of the hessian.
	 */
	private final double[] diagonal;

	/**
	 * The amount to change the weights by.
	 */
	private double[] deltas;

	/**
	 * The training elements.
	 */
	private final MLDataPair pair;

	/**
	 * Construct the LMA object.
	 * 
	 * @param network
	 *            The network to train. Must have a single output neuron.
	 * @param training
	 *            The training data to use. Must be indexable.
	 */
	public LevenbergMarquardtTraining(final BasicNetwork network,
			final MLDataSet training) {
		this(network,training,new HessianCR());
	}
	
	/**
	 * Construct the LMA object.
	 * 
	 * @param network
	 *            The network to train. Must have a single output neuron.
	 * @param training
	 *            The training data to use. Must be indexable.
	 */
	public LevenbergMarquardtTraining(final BasicNetwork network,
			final MLDataSet training, final ComputeHessian h) {
		super(TrainingImplementationType.Iterative);
		ValidateNetwork.validateMethodToData(network, training);

		setTraining(training);
		this.indexableTraining = getTraining();
		this.network = network;
		this.trainingLength = (int) this.indexableTraining.getRecordCount();
		this.weightCount = this.network.getStructure().calculateSize();
		this.lambda = 0.1;
		this.deltas = new double[this.weightCount];
		this.diagonal = new double[this.weightCount];

		final BasicMLData input = new BasicMLData(
				this.indexableTraining.getInputSize());
		final BasicMLData ideal = new BasicMLData(
				this.indexableTraining.getIdealSize());
		this.pair = new BasicMLDataPair(input, ideal);
		
		this.hessian = h;
		this.hessian.init(network, training);


	}

	private void saveDiagonal() {
		double[][] h = this.hessian.getHessian();
		for (int i = 0; i < this.weightCount; i++) {
			this.diagonal[i] = h[i][i];
		}
	}

	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The trained network.
	 */
	@Override
	public MLMethod getMethod() {
		return this.network;
	}
	
	/**
	 * @return The SSE error with the current weights.
	 */
	private double calculateError() {
		ErrorCalculation result = new ErrorCalculation();
		
		for (int i = 0; i < this.trainingLength; i++) {
			this.indexableTraining.getRecord(i, this.pair);
			final MLData actual = this.network.compute(this.pair.getInput());
			result.updateError(actual.getData(), this.pair.getIdeal().getData(),pair.getSignificance());
		}		
		
		return result.calculateESS();
	}
	
	private void applyLambda() {
		double[][] h = this.hessian.getHessian();
		for (int i = 0; i < this.weightCount; i++) {
			h[i][i] = this.diagonal[i] + this.lambda;
		}
	}
	
	/**
	 * Perform one iteration.
	 */
	@Override
	public void iteration() {

		LUDecomposition decomposition = null;
		preIteration();

		this.hessian.clear();
		this.weights = NetworkCODEC.networkToArray(this.network);
		
		this.hessian.compute();			
		double currentError = this.hessian.getSSE();
		saveDiagonal();

		final double startingError = currentError;
		boolean done = false;
		boolean singular;

		while (!done) {
			applyLambda();
			decomposition = new LUDecomposition(this.hessian.getHessianMatrix());
			
			singular = decomposition.isNonsingular();

			if (singular) {
				this.deltas = decomposition.Solve(this.hessian.getGradients());
				updateWeights();
				currentError = calculateError();				
			}
			
			if ( !singular ||  currentError >= startingError) {
				this.lambda *= LevenbergMarquardtTraining.SCALE_LAMBDA;
				if( this.lambda> LevenbergMarquardtTraining.LAMBDA_MAX ) {
					this.lambda = LevenbergMarquardtTraining.LAMBDA_MAX;
					done = true;
				}
			} else {
				this.lambda /= LevenbergMarquardtTraining.SCALE_LAMBDA;					
				done = true;
			}
		}

		setError(currentError);

		postIteration();
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	/**
	 * Update the weights in the neural network.
	 */
	public void updateWeights() {
		final double[] w = this.weights.clone();

		for (int i = 0; i < w.length; i++) {
			w[i] += this.deltas[i];
		}

		NetworkCODEC.arrayToNetwork(w, this.network);
	}

	/**
	 * @return The Hessian calculation method used.
	 */
	public ComputeHessian getHessian() {
		return hessian;
	}

	@Override
	public int getThreadCount() {
		if( this.hessian instanceof MultiThreadable ) {
			return ((MultiThreadable)this.hessian).getThreadCount();
		} else {
			throw new TrainingError("The Hessian object in use("+this.hessian.getClass().toString()+") does not support multi-threaded mode.");
		}
	}

	@Override
	public void setThreadCount(int numThreads) {
		if( this.hessian instanceof MultiThreadable ) {
			((MultiThreadable)this.hessian).setThreadCount(numThreads);
		} else {
			throw new TrainingError("The Hessian object in use("+this.hessian.getClass().toString()+") does not support multi-threaded mode.");
		}
	}	

}
