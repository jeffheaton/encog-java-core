package org.encog.neural.freeform.training;

import java.io.Serializable;

import org.encog.mathutil.EncogMath;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.freeform.task.ConnectionTask;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.RPROPConst;

public class FreeformResilientPropagation extends FreeformPropagationTraining implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int TEMP_GRADIENT = 0;
	public static final int TEMP_LAST_GRADIENT = 1;
	public static final int TEMP_UPDATE = 2;
	public static final int TEMP_LAST_WEIGHT_DELTA = 3;

	private double maxStep;

	public FreeformResilientPropagation(final FreeformNetwork theNetwork,
			final MLDataSet theTraining, final double initialUpdate, double theMaxStep) {
		super(theNetwork, theTraining);
		this.maxStep = theMaxStep;
		theNetwork.tempTrainingAllocate(1, 4);
		theNetwork.performConnectionTask(new ConnectionTask() {
			@Override
			public void task(FreeformConnection c) {
				c.setTempTraining(TEMP_UPDATE, initialUpdate);				
			}});
	}

	public FreeformResilientPropagation(FreeformNetwork theNetwork,
			MLDataSet theTraining) {
		this(theNetwork, theTraining, RPROPConst.DEFAULT_INITIAL_UPDATE, RPROPConst.DEFAULT_MAX_STEP);
	}

	@Override
	protected void learnConnection(FreeformConnection connection) {

		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = EncogMath.sign(connection
				.getTempTraining(TEMP_GRADIENT)
				* connection.getTempTraining(TEMP_LAST_GRADIENT));
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = connection.getTempTraining(TEMP_UPDATE)
					* RPROPConst.POSITIVE_ETA;
			delta = Math.min(delta, this.maxStep);
			weightChange = EncogMath.sign(connection
					.getTempTraining(TEMP_GRADIENT)) * delta;
			connection.setTempTraining(TEMP_UPDATE, delta);
			connection.setTempTraining(TEMP_LAST_GRADIENT,
					connection.getTempTraining(TEMP_GRADIENT));
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = connection.getTempTraining(TEMP_UPDATE)
					* RPROPConst.NEGATIVE_ETA;
			delta = Math.max(delta, RPROPConst.DELTA_MIN);
			connection.setTempTraining(TEMP_UPDATE, delta);
			weightChange = -connection.getTempTraining(TEMP_LAST_WEIGHT_DELTA);
			// set the previous gradient to zero so that there will be no
			// adjustment the next iteration
			connection.setTempTraining(TEMP_LAST_GRADIENT, 0);
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = connection.getTempTraining(TEMP_UPDATE);
			weightChange = EncogMath.sign(connection
					.getTempTraining(TEMP_GRADIENT)) * delta;
			connection.setTempTraining(TEMP_LAST_GRADIENT,
					connection.getTempTraining(TEMP_GRADIENT));
		}

		// apply the weight change, if any
		connection.addWeight(weightChange);		
		connection.setTempTraining(TEMP_LAST_WEIGHT_DELTA, weightChange);
	}

	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		// TODO Auto-generated method stub
		
	}
	
	

}
