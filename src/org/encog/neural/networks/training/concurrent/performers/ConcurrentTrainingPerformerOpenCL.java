package org.encog.neural.networks.training.concurrent.performers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.neural.networks.training.concurrent.TrainingJob;

public class ConcurrentTrainingPerformerOpenCL implements ConcurrentTrainingPerformer {

	AtomicBoolean ready;
	
	
	
	@Override
	public void perform(TrainingJob job) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean ready() {
		// TODO Auto-generated method stub
		return false;
	}


}
