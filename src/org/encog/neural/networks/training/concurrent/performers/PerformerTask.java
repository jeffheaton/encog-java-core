package org.encog.neural.networks.training.concurrent.performers;

import org.encog.engine.concurrency.EngineTask;

public class PerformerTask implements EngineTask {

	private ConcurrentTrainingPerformer owner;
	
	public PerformerTask(ConcurrentTrainingPerformer owner) {
		this.owner = owner;
	}
	
	@Override
	public void run() {
		this.owner.run();		
	}

}
