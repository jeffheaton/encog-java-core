/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.neural.freeform.training;

import java.io.Serializable;

import org.encog.mathutil.EncogMath;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.freeform.task.ConnectionTask;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.RPROPConst;

public class FreeformResilientPropagation extends FreeformPropagationTraining
		implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Temp value #0: the gradient.
	 */
	public static final int TEMP_GRADIENT = 0;
	
	/**
	 * Temp value #1: the last gradient.
	 */
	public static final int TEMP_LAST_GRADIENT = 1;
	
	/**
	 * Temp value #2: the update.
	 */
	public static final int TEMP_UPDATE = 2;
	
	/**
	 * Temp value #3: the the last weight delta.
	 */
	public static final int TEMP_LAST_WEIGHT_DELTA = 3;

	/**
	 * The max step.
	 */
	private final double maxStep;

	/**
	 * Construct the RPROP trainer, Use default intiial update and max step.
	 * @param theNetwork The network to train.
	 * @param theTraining The training set.
	 */
	public FreeformResilientPropagation(final FreeformNetwork theNetwork,
			final MLDataSet theTraining) {
		this(theNetwork, theTraining, RPROPConst.DEFAULT_INITIAL_UPDATE,
				RPROPConst.DEFAULT_MAX_STEP);
	}

	/**
	 * Construct the RPROP trainer.
	 * @param theNetwork The network to train.
	 * @param theTraining The training set.
	 * @param initialUpdate The initial update.
	 * @param theMaxStep The max step.
	 */
	public FreeformResilientPropagation(final FreeformNetwork theNetwork,
			final MLDataSet theTraining, final double initialUpdate,
			final double theMaxStep) {
		super(theNetwork, theTraining);
		this.maxStep = theMaxStep;
		theNetwork.tempTrainingAllocate(1, 4);
		theNetwork.performConnectionTask(new ConnectionTask() {
			@Override
			public void task(final FreeformConnection c) {
				c.setTempTraining(FreeformResilientPropagation.TEMP_UPDATE,
						initialUpdate);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void learnConnection(final FreeformConnection connection) {

		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath
				.sign(connection
						.getTempTraining(FreeformResilientPropagation.TEMP_GRADIENT)
						* connection
								.getTempTraining(FreeformResilientPropagation.TEMP_LAST_GRADIENT));
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = connection
					.getTempTraining(FreeformResilientPropagation.TEMP_UPDATE)
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = EncogMath
					.sign(connection
							.getTempTraining(FreeformResilientPropagation.TEMP_GRADIENT))
					* delta;
			connection.setTempTraining(
					FreeformResilientPropagation.TEMP_UPDATE, delta);
			connection
					.setTempTraining(
							FreeformResilientPropagation.TEMP_LAST_GRADIENT,
							connection
									.getTempTraining(FreeformResilientPropagation.TEMP_GRADIENT));
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = connection
					.getTempTraining(FreeformResilientPropagation.TEMP_UPDATE)
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
			connection.setTempTraining(
					FreeformResilientPropagation.TEMP_UPDATE, delta);
			weightChange = -connection
					.getTempTraining(FreeformResilientPropagation.TEMP_LAST_WEIGHT_DELTA);
			// set the previous gradient to zero so that there will be no
			// adjustment the next iteration
			connection.setTempTraining(
					FreeformResilientPropagation.TEMP_LAST_GRADIENT, 0);
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = connection
					.getTempTraining(FreeformResilientPropagation.TEMP_UPDATE);
			weightChange = EncogMath
					.sign(connection
							.getTempTraining(FreeformResilientPropagation.TEMP_GRADIENT))
					* delta;
			connection
					.setTempTraining(
							FreeformResilientPropagation.TEMP_LAST_GRADIENT,
							connection
									.getTempTraining(FreeformResilientPropagation.TEMP_GRADIENT));
		}

		// apply the weight change, if any
		connection.addWeight(weightChange);
		connection.setTempTraining(
				FreeformResilientPropagation.TEMP_LAST_WEIGHT_DELTA,
				weightChange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {
		// TODO Auto-generated method stub

	}

}
