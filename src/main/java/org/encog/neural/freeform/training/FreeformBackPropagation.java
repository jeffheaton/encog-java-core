package org.encog.neural.freeform.training;

import java.io.Serializable;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNetwork;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class FreeformBackPropagation extends FreeformPropagationTraining implements Serializable {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;
	
	private double learningRate;
	private double momentum;
	
	public FreeformBackPropagation(FreeformNetwork theNetwork,
			MLDataSet theTraining, double theLearningRate, double theMomentum) {
		super(theNetwork, theTraining);
		theNetwork.tempTrainingAllocate(1,2);
		this.learningRate = theLearningRate;
		this.momentum = theMomentum;
	}
	
	@Override
	protected void learnConnection(FreeformConnection connection) {
		double gradient = connection.getTempTraining(0);
		double delta = (gradient * this.learningRate) + (connection.getTempTraining(1) * this.momentum);
		connection.setTempTraining(1, delta);
		connection.addWeight(delta);
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
