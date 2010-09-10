package org.encog.neural.networks.training.concurrent;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformer;

public class ConcurrentTrainingManager implements Runnable {

	private List<ConcurrentTrainingPerformer> performers = new ArrayList<ConcurrentTrainingPerformer>();
	private List<TrainingJob> queue = new ArrayList<TrainingJob>();
	private Thread thread;

	public void clearPerformers() {
		this.performers.clear();
	}

	public void clearQueue() {
		this.queue.clear();
	}

	public void addPerformer(ConcurrentTrainingPerformer performer) {
		this.performers.add(performer);
	}

	public void addTrainingJob(TrainingJob job) {
		this.queue.add(job);

	}

	public ConcurrentTrainingPerformer waitForFreePerformer() {
		ConcurrentTrainingPerformer result = null;

		while (result == null) {
			for (ConcurrentTrainingPerformer performer : this.performers) {
				if (performer.ready())
					result = performer;
			}
			
			if( result==null )
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}

		return result;
	}

	public void run()
	{		
		for(TrainingJob job: this.queue) 
		{
			// find a performer
			ConcurrentTrainingPerformer perform = this.waitForFreePerformer();
			perform.perform(job);
		}		
	}

	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void join() {
		try {
			this.thread.join();
		} catch (InterruptedException e) {

		}
		
	}

}
