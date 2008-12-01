/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.networks.training.som;

import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.Network;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.util.NormalizeInput;

/**
 * TrainSelfOrganizingMap: Implements an unsupervised training algorithm for use
 * with a Self Organizing Map.
 */
public class TrainSelfOrganizingMap implements Train {

	/**
	 * The default reduction to use.
	 */
	public static final double DEFAULT_REDUCTION = 0.99;
	
	/**
	 * The minimum learning rate for reduction to be applied.
	 */
	public static final double MIN_LEARNRATE_FOR_REDUCTION = 0.01;
	
	/**
	 * The learning method, either additive or subtractive.
	 * 
	 * @author jheaton
	 * 
	 */
	public enum LearningMethod {
		/**
		 * Additive learning.
		 */
		ADDITIVE, 
		/**
		 * Subtractive learning.
		 */
		SUBTRACTIVE
	}

	/**
	 * The self organizing map to train.
	 */
	private final SOMLayer somLayer;

	/**
	 * The learning method.
	 */
	private LearningMethod learnMethod;

	/**
	 * The learning rate.
	 */
	private double learnRate;

	/**
	 * Reduction factor.
	 */
	private double reduction = TrainSelfOrganizingMap.DEFAULT_REDUCTION;

	/**
	 * Mean square error of the network for the iteration.
	 */
	private double totalError;

	/**
	 * Mean square of the best error found so far.
	 */
	private double globalError;

	/**
	 * Keep track of how many times each neuron won.
	 */
	private int[] won;

	/**
	 * The training sets.
	 */
	private final NeuralDataSet train;

	/**
	 * How many output neurons.
	 */
	private final int outputNeuronCount;

	/**
	 * How many input neurons.
	 */
	private final int inputNeuronCount;

	/**
	 * The best network found so far.
	 */
	private final Matrix bestMatrix;

	/**
	 * The best error found so far.
	 */
	private double bestError;

	/**
	 * The work matrix, used to calculate corrections.
	 */
	private Matrix work;

	/**
	 * The correction matrix, will be applied to the weight matrix after each
	 * training iteration.
	 */
	private final Matrix correc;

	/**
	 * The size of the training set.
	 */
	private int trainSize;

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * Construct the trainer for a self organizing map.
	 * 
	 * @param network
	 *            The network to train.
	 * @param train
	 *            The training method.
	 * @param learnMethod
	 *            The learning method.
	 * @param learnRate
	 *            The learning rate.
	 */
	public TrainSelfOrganizingMap(final BasicNetwork network,
			final NeuralDataSet train, final LearningMethod learnMethod,
			final double learnRate) {
		this.network = network;
		this.train = train;
		this.totalError = 1.0;
		this.learnMethod = learnMethod;
		this.learnRate = learnRate;

		this.somLayer = findSOMLayer();

		this.outputNeuronCount = this.somLayer.getNext().getNeuronCount();
		this.inputNeuronCount = this.somLayer.getNeuronCount();

		this.totalError = 1.0;
		this.trainSize = 0;

		for (final NeuralDataPair pair : train) {
			this.trainSize++;
			final Matrix dptr = Matrix.createColumnMatrix(pair.getInput()
					.getData());
			if (MatrixMath.vectorLength(dptr) < SOMLayer.VERYSMALL) {
				throw new RuntimeException(
						"Multiplicative normalization has null training case");
			}

		}

		this.bestMatrix = this.somLayer.getMatrix().clone();

		this.won = new int[this.outputNeuronCount];
		this.correc = new Matrix(this.outputNeuronCount,
				this.inputNeuronCount + 1);
		if (this.learnMethod == LearningMethod.ADDITIVE) {
			this.work = new Matrix(1, this.inputNeuronCount + 1);
		} else {
			this.work = null;
		}

		initialize();
		this.bestError = Double.MAX_VALUE;
	}

	/**
	 * Adjust the weights and allow the network to learn.
	 */
	protected void adjustWeights() {
		for (int i = 0; i < this.outputNeuronCount; i++) {

			if (this.won[i] == 0) {
				continue;
			}

			double f = 1.0 / this.won[i];
			if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
				f *= this.learnRate;
			}

			double length = 0.0;

			for (int j = 0; j <= this.inputNeuronCount; j++) {
				final double corr = f * this.correc.get(i, j);
				this.somLayer.getMatrix().add(i, j, corr);
				length += corr * corr;
			}
		}
	}

	/**
	 * Evaludate the current error level of the network.
	 */
	public void evaluateErrors() {

		this.correc.clear();

		for (int i = 0; i < this.won.length; i++) {
			this.won[i] = 0;
		}

		this.globalError = 0.0;
		// loop through all training sets to determine correction
		for (final NeuralDataPair pair : this.train) {
			final NormalizeInput input = new NormalizeInput(pair.getInput(),
					this.somLayer.getNormalizationType());
			final int best = this.network.winner(pair.getInput());

			this.won[best]++;
			final Matrix wptr = this.somLayer.getMatrix().getRow(best);

			double length = 0.0;
			double diff;

			for (int i = 0; i < this.inputNeuronCount; i++) {
				diff = pair.getInput().getData(i) * input.getNormfac()
						- wptr.get(0, i);
				length += diff * diff;
				if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
					this.correc.add(best, i, diff);
				} else {
					this.work.set(0, i, this.learnRate
							* pair.getInput().getData(i) * input.getNormfac()
							+ wptr.get(0, i));
				}
			}
			diff = input.getSynth() - wptr.get(0, this.inputNeuronCount);
			length += diff * diff;
			if (this.learnMethod == LearningMethod.SUBTRACTIVE) {
				this.correc.add(best, this.inputNeuronCount, diff);
			} else {
				this.work
						.set(0, this.inputNeuronCount, this.learnRate
								* input.getSynth()
								+ wptr.get(0, this.inputNeuronCount));
			}

			if (length > this.globalError) {
				this.globalError = length;
			}

			if (this.learnMethod == LearningMethod.ADDITIVE) {
				normalizeWeight(this.work, 0);
				for (int i = 0; i <= this.inputNeuronCount; i++) {
					this.correc.add(best, i, this.work.get(0, i)
							- wptr.get(0, i));
				}
			}

		}

		this.globalError = Math.sqrt(this.globalError);
	}

	/**
	 * Find the layer that is a SOM.
	 * @return The SOM layer.
	 */
	private SOMLayer findSOMLayer() {

		for (final Layer layer : this.network.getLayers()) {
			if (layer instanceof SOMLayer) {
				return (SOMLayer) layer;
			}
		}

		return null;
	}

	/**
	 * Force a win, if no neuron won.
	 */
	protected void forceWin() {
		int best;
		NeuralDataPair which = null;

		final Matrix outputWeights = this.somLayer.getMatrix();

		// Loop over all training sets. Find the training set with
		// the least output.
		double dist = Double.MAX_VALUE;
		for (final NeuralDataPair pair : this.train) {
			best = this.network.winner(pair.getInput());
			final NeuralData output = this.somLayer.getFire();

			if (output.getData(best) < dist) {
				dist = output.getData(best);
				which = pair;
			}
		}

		if (which != null) {
			final NormalizeInput input = new NormalizeInput(which.getInput(),
					this.somLayer.getNormalizationType());
			best = this.network.winner(which.getInput());
			final NeuralData output = this.somLayer.getFire();
			int which2 = 0;

			dist = Double.MIN_VALUE;
			int i = this.outputNeuronCount;
			while (i-- > 0) {
				if (this.won[i] != 0) {
					continue;
				}
				if (output.getData(i) > dist) {
					dist = output.getData(i);
					which2 = i;
				}
			}

			for (int j = 0; j < input.getInputMatrix().getCols(); j++) {
				outputWeights.set(which2, j, input.getInputMatrix().get(0, j));
			}

			normalizeWeight(outputWeights, which2);
		}
	}

	/**
	 * Get the best error so far.
	 * 
	 * @return The best error so far.
	 */
	public double getBestError() {
		return this.bestError;
	}

	/**
	 * Get the error for this iteration.
	 * 
	 * @return The error for this iteration.
	 */
	public double getTotalError() {
		return this.totalError;
	}

	/**
	 * Called to initialize the SOM.
	 */
	public void initialize() {

		for (int i = 0; i < this.outputNeuronCount; i++) {
			normalizeWeight(this.somLayer.getMatrix(), i);
		}
	}

	/**
	 * This method is called for each training iteration. Usually this method is
	 * called from inside a loop until the error level is acceptable.
	 */
	public void iteration() {

		evaluateErrors();

		this.totalError = this.globalError;

		if (this.totalError < this.bestError) {
			this.bestError = this.totalError;
			MatrixMath.copy(this.somLayer.getMatrix(), this.bestMatrix);
		}

		int winners = 0;
		for (int i = 0; i < this.won.length; i++) {
			if (this.won[i] != 0) {
				winners++;
			}
		}

		if (winners < this.outputNeuronCount && winners < this.trainSize) {
			//forceWin();
			return;
		}

		adjustWeights();

		if (this.learnRate  
		  > TrainSelfOrganizingMap.MIN_LEARNRATE_FOR_REDUCTION) {
			this.learnRate *= this.reduction;
		}

		// done

		MatrixMath.copy(this.somLayer.getMatrix(), this.bestMatrix);

		for (int i = 0; i < this.outputNeuronCount; i++) {
			normalizeWeight(this.somLayer.getMatrix(), i);
		}
	}

	/**
	 * Normalize the specified row in the weight matrix.
	 * 
	 * @param matrix
	 *            The weight matrix.
	 * @param row
	 *            The row to normalize.
	 */
	protected void normalizeWeight(final Matrix matrix, final int row) {

		double len = MatrixMath.vectorLength(matrix.getRow(row));
		len = Math.max(len, SOMLayer.VERYSMALL);

		len = 1.0 / len;
		for (int i = 0; i < this.inputNeuronCount; i++) {
			matrix.set(row, i, matrix.get(row, i) * len);
		}
		matrix.set(row, this.inputNeuronCount, 0);
	}

	public double getError() {
		return this.bestError;
	}

	public Network getNetwork() {
		return this.network;
	}
}
