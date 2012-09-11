package org.encog.neural.freeform.training;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.freeform.FreeformConnection;
import org.encog.neural.freeform.FreeformNetwork;

public class FreeformBackPropagation extends FreeformPropagationTraining {

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

}
