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
import org.encog.neural.networks.layers.SOMLayer;
import org.encog.util.NormalizeInput;



/**
 * TrainSelfOrganizingMap: Implements an unsupervised training algorithm for use
 * with a Self Organizing Map.
 */
public class TrainSelfOrganizingMap {
	
	/**
	 * The learning method, either additive or subtractive.
	 * @author jheaton
	 *
	 */
	public enum LearningMethod 
	{
		ADDITIVE,
		SUBTRACTIVE
	}

	/**
	 * The self organizing map to train.
	 */
	private final SOMLayer somLayer;
	
	/**
	 * The learning method.
	 */
	protected LearningMethod learnMethod;

	/**
	 * The learning rate.
	 */
	protected double learnRate;

	/**
	 * Reduction factor.
	 */
	protected double reduction = .99;

	/**
	 * Mean square error of the network for the iteration.
	 */
	protected double totalError;


	/**
	 * Mean square of the best error found so far.
	 */
	protected double globalError;

	/**
	 * Keep track of how many times each neuron won.
	 */
	int won[];
	
	/**
	 * The training sets.
	 */
	private NeuralDataSet train;

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
	private Matrix correc;
	
	private int trainSize;
	
	private BasicNetwork network;

	/**
	 * Construct the trainer for a self organizing map.
	 * @param som The self organizing map.
	 * @param train The training method.
	 * @param learnMethod The learning method.
	 * @param learnRate The learning rate.
	 */
	public TrainSelfOrganizingMap(final BasicNetwork network,
			final NeuralDataSet train,LearningMethod learnMethod,double learnRate) {
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

		for(NeuralDataPair pair: train) {
			this.trainSize++;
			final Matrix dptr = Matrix.createColumnMatrix(pair.getInput().getData());
			if (MatrixMath.vectorLength(dptr) < SOMLayer.VERYSMALL) {
				throw (new RuntimeException(
						"Multiplicative normalization has null training case"));
			}

		}

		this.bestMatrix = somLayer.getMatrix().clone();

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

	private SOMLayer findSOMLayer() {

		for(Layer layer: this.network.getLayers() )
		{
			if( layer instanceof SOMLayer )
				return (SOMLayer)layer;
		}
		
		return null;
	}

	/**
	 * Adjust the weights and allow the network to learn.
	 */
	protected void adjustWeights()
	{
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
		for(NeuralDataPair pair: this.train) {
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
					this.work.set(0, i, this.learnRate * pair.getInput().getData(i)
							* input.getNormfac() + wptr.get(0, i));
				}
			}
			diff = input.getSynth() - wptr.get(0, this.inputNeuronCount);
			length += diff * diff;
			if (this.learnMethod ==LearningMethod.SUBTRACTIVE) {
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
	 * Force a win, if no neuron won.
	 */
	protected void forceWin() {
		int best; 
		NeuralDataPair which = null;

		final Matrix outputWeights = this.somLayer.getMatrix();
		
		// Loop over all training sets.  Find the training set with
		// the least output.
		double dist = Double.MAX_VALUE;
		for(NeuralDataPair pair: train) {
			best = this.network.winner(pair.getInput());
			NeuralData output = this.somLayer.getFire();
			
			if (output.getData(best) < dist) {
				dist = output.getData(best);
				which = pair;
			}
		}
		
		if( which!=null )
		{
		final NormalizeInput input = new NormalizeInput(which.getInput(),
				this.somLayer.getNormalizationType());
		best = this.network.winner(which.getInput());
		final NeuralData output = this.somLayer.getFire();
		int which2 = 0;
		
		dist = Double.MIN_VALUE;
		int i = this.outputNeuronCount;
		while ((i--) > 0) {
			if (this.won[i] != 0) {
				continue;
			}
			if (output.getData(i) > dist) {
				dist = output.getData(i);
				which2 = i;
			}
		}

		for (int j = 0; j < input.getInputMatrix().getCols(); j++) {
			outputWeights.set(which2, j, input.getInputMatrix().get(0,j));
		}

		normalizeWeight(outputWeights, which2);
		}
	}

	/**
	 * Get the best error so far.
	 * @return The best error so far.
	 */
	public double getBestError() {
		return this.bestError;
	}

	/**
	 * Get the error for this iteration.
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

		if ((winners < this.outputNeuronCount) && (winners < this.trainSize)) {
			forceWin();
			return;
		}

		adjustWeights();

		if (this.learnRate > 0.01) {
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
	 * @param matrix The weight matrix.
	 * @param row The row to normalize.
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
}
