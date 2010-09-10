package org.encog.neural.networks.training.concurrent.performers;

import org.encog.neural.networks.training.concurrent.TrainingJob;

public interface ConcurrentTrainingPerformer  {
	boolean ready();
	void perform(TrainingJob job);
}
