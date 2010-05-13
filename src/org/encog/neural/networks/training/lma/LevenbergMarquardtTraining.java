/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 *
 * Copyright 2008-2010 by Heaton Research Inc.
 *
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 *
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.lma;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.decomposition.LUDecomposition;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;

/**
 * Trains a neural network using a Levenberg Marquardt algorithm (LMA). This
 * training technique is based on the mathematical technique of the same name. ì
 *
 * http://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm
 *
 * The LMA training technique has some important limitations that you should be
 * aware of, before using it.
 *
 * Only neural networks that have a single output neuron can be used with this
 * training technique.
 *
 * The entire training set must be loaded into memory. Because of this an
 * Indexable training set must be used.
 *
 * However, despite these limitations, the LMA training technique can be a very
 * effective training method.
 *
 * References:
 * - http://www-alg.ist.hokudai.ac.jp/~jan/alpha.pdf
 * - http://www.inference.phy.cam.ac.uk/mackay/Bayes_FAQ.html
 *
 */
public class LevenbergMarquardtTraining extends BasicTraining {

	/**
	 * The amount to scale the lambda by.
	 */
	public final static double SCALE_LAMBDA = 10.0;

	/**
	 * The max amount for the LAMBDA.
	 */
	public final static double LAMBDA_MAX = 1e25;

	/**
	 * The network that is to be trained.
	 */
	private final BasicNetwork network;

	/**
	 * The training set that we are using to train.
	 */
	private final Indexable indexableTraining;

	/**
	 * The training set length.
	 */
	private final int trainingLength;

	/**
	 * The number of "parameters" in the LMA algorithm. The parameters are what
	 * the LMA adjusts to achieve the desired outcome. For neural network
	 * optimization, the parameters are the weights and bias values.
	 */
	private final int parametersLength;

	/**
	 * The neural network weights and bias values.
	 */
	private double[] weights;

	/**
	 * The "hessian" matrix, used by the LMA.
	 */
	private final Matrix hessianMatrix;

	/**
	 * The "hessian" matrix as a 2d array.
	 */
	private final double[][] hessian;

	/**
	 * The alpha is multiplied by sum squared of weights. This scales the effect
	 * that the sum squared of the weights has.
	 */
	private double alpha;

	/**
	 * The beta is multiplied by the sum squared of the errors.
	 */
	private double beta;

	/**
	 * The lambda, or damping factor. This is increased until a desirable
	 * adjustment is found.
	 */
	private double lambda;

	/**
	 * The calculated gradients.
	 */
	private final double[] gradient;

	/**
	 * The diagonal of the hessian.
	 */
	private final double[] diagonal;

	/**
	 * The amount to change the weights by.
	 */
	private double[] deltas;

	private double gamma;

	/**
	 * The training elements.
	 */
	private final NeuralDataPair pair;

	/**
	 * Should we use Bayesian regularization.
	 */
	private boolean useBayesianRegularization;

	/**
	 * Construct the LMA object.
	 *
	 * @param network
	 *            The network to train. Must have a single output neuron.
	 * @param training
	 *            The training data to use. Must be indexable.
	 */
	public LevenbergMarquardtTraining(final BasicNetwork network,
			final NeuralDataSet training) {
		if (!(training instanceof Indexable)) {
			throw new TrainingError(
					"Levenberg Marquardt requires an indexable training set.");
		}

		final Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);

		if (outputLayer == null) {
			throw new TrainingError(
					"Levenberg Marquardt requires an output layer.");
		}

		if (outputLayer.getNeuronCount() != 1) {
			throw new TrainingError(
					"Levenberg Marquardt requires an output layer with a single neuron.");
		}

		setTraining(training);
		this.indexableTraining = (Indexable) getTraining();
		this.network = network;
		this.trainingLength = (int) this.indexableTraining.getRecordCount();
		this.parametersLength = this.network.getStructure().calculateSize();
		this.hessianMatrix = new Matrix(this.parametersLength,
				this.parametersLength);
		this.hessian = this.hessianMatrix.getData();
		this.alpha = 0.0;
		this.beta = 1.0;
		this.lambda = 0.1;
		this.deltas = new double[this.parametersLength];
		this.gradient = new double[this.parametersLength];
		this.diagonal = new double[this.parametersLength];

		final BasicNeuralData input = new BasicNeuralData(
				this.indexableTraining.getInputSize());
		final BasicNeuralData ideal = new BasicNeuralData(
				this.indexableTraining.getIdealSize());
		this.pair = new BasicNeuralDataPair(input, ideal);
	}

	/**
	 * Calculate the Hessian matrix.
	 *
	 * @param jacobian
	 *            The Jacobian matrix.
	 * @param errors
	 *            The errors.
	 */
	public void calculateHessian(final double[][] jacobian,
			final double[] errors) {
		for (int i = 0; i < this.parametersLength; i++) {
			// Compute Jacobian Matrix Errors
			double s = 0.0;
			for (int j = 0; j < this.trainingLength; j++) {
				s += jacobian[j][i] * errors[j];
			}
			this.gradient[i] = s;

			// Compute Quasi-Hessian Matrix using Jacobian (H = J'J)
			for (int j = 0; j < this.parametersLength; j++) {
				double c = 0.0;
				for (int k = 0; k < this.trainingLength; k++) {
					c += jacobian[k][i] * jacobian[k][j];
				}
				this.hessian[i][j] = this.beta * c;
			}
		}

		for (int i = 0; i < this.parametersLength; i++) {
			this.diagonal[i] = this.hessian[i][i];
		}
	}

	/**
	 * Calculate the sum squared of the weights.
	 *
	 * @return The sum squared of the weights.
	 */
	private double calculateSumOfSquaredWeights() {
		double result = 0;

		for (final double weight : this.weights) {
			result += weight * weight;
		}

		return result / 2.0;
	}

	/**
	 * @return The trained network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

    /**
     * Return the sum of the diagonal.
     * @param m The matrix to sum.
     * @return The trace of the matrix.
     */
    public static double trace(double[][] m)
    {
        double result = 0.0;
        for (int i = 0; i < m.length; i++)
        {
            result += m[i][i];
        }
        return result;
    }

	/**
	 * Perform one iteration.
	 */
	public void iteration() {

		LUDecomposition decomposition = null;
		double trace = 0;

		preIteration();

		this.weights = NetworkCODEC.networkToArray(this.network);

		final ComputeJacobian j = new JacobianChainRule(this.network,
				this.indexableTraining);

		double sumOfSquaredErrors = j.calculate(this.weights);
		double sumOfSquaredWeights = calculateSumOfSquaredWeights();

		// this.setError(j.getError());
		calculateHessian(j.getJacobian(), j.getRowErrors());

		// Define the objective function
		// bayesian regularization objective function
		final double objective = this.beta * sumOfSquaredErrors + this.alpha
				* sumOfSquaredWeights;
		double current = objective + 1.0;

		// Start the main Levenberg-Macquardt method
		this.lambda /= LevenbergMarquardtTraining.SCALE_LAMBDA;

		// We'll try to find a direction with less error
		// (or where the objective function is smaller)
		while ((current >= objective)
				&& (this.lambda < LevenbergMarquardtTraining.LAMBDA_MAX)) {
			this.lambda *= LevenbergMarquardtTraining.SCALE_LAMBDA;

			// Update diagonal (Levenberg-Marquardt formula)
			for (int i = 0; i < this.parametersLength; i++) {
				this.hessian[i][i] = this.diagonal[i]
						+ (this.lambda + this.alpha);
			}

			// Decompose to solve the linear system
			decomposition = new LUDecomposition(
					this.hessianMatrix);

			// Check if the Jacobian has become non-invertible
			if (!decomposition.isNonsingular()) {
				continue;
			}

			// Solve using LU (or SVD) decomposition
			this.deltas = decomposition.Solve(this.gradient);

			// Update weights using the calculated deltas
			sumOfSquaredWeights = updateWeights();

			// Calculate the new error
			sumOfSquaredErrors = 0.0;
			for (int i = 0; i < this.trainingLength; i++) {
				this.indexableTraining.getRecord(i, this.pair);
				final NeuralData actual = this.network.compute(this.pair
						.getInput());
				final double e = this.pair.getIdeal().getData(0)
						- actual.getData(0);
				sumOfSquaredErrors += e * e;
			}
			sumOfSquaredErrors /= 2.0;

			// Update the objective function
			current = this.beta * sumOfSquaredErrors + this.alpha
					* sumOfSquaredWeights;

			// If the object function is bigger than before, the method
			// is tried again using a greater dumping factor.
		}

		// If this iteration caused a error drop, then next iteration
		// will use a smaller damping factor.
		this.lambda /= LevenbergMarquardtTraining.SCALE_LAMBDA;

        if (useBayesianRegularization && decomposition!=null)
        {
            // Compute the trace for the inverse Hessian
            trace = trace(decomposition.inverse());

            // Poland update's formula:
            gamma = this.parametersLength - (alpha * trace);
            alpha = this.parametersLength / (2.0 * sumOfSquaredWeights + trace);
            beta = Math.abs((this.trainingLength - gamma) / (2.0 * sumOfSquaredErrors));
        }

		setError(sumOfSquaredErrors);

		postIteration();
	}

	/**
	 * Update the weights.
	 *
	 * @return The sum squared of the weights.
	 */
	public double updateWeights() {
		double result = 0;
		final double[] w = this.weights.clone();

		for (int i = 0; i < w.length; i++) {
			w[i] += this.deltas[i];
			result += w[i] * w[i];
		}

		NetworkCODEC.arrayToNetwork(w, this.network);

		return result / 2.0;
	}

	public boolean isUseBayesianRegularization() {
		return useBayesianRegularization;
	}

	public void setUseBayesianRegularization(boolean useBayesianRegularization) {
		this.useBayesianRegularization = useBayesianRegularization;
	}



}
