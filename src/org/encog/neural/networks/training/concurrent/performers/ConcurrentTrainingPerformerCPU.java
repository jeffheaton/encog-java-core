package org.encog.neural.networks.training.concurrent.performers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.concurrent.TrainingJob;

public class ConcurrentTrainingPerformerCPU implements ConcurrentTrainingPerformer, Runnable {

	private AtomicBoolean ready = new AtomicBoolean(true);
	private TrainingJob currentJob;
	
	@Override
	public void perform(TrainingJob job) {
		if( this.ready.get()==false )
		{
			throw new NeuralNetworkError("Performer is already performing a job.");
		}
				
		this.ready.set(false);
		this.currentJob = job;
		
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public boolean ready() {
		return ready.get();
	}

	public void run() {
		try
		{
			Train train = this.currentJob.getTrain();			
			int interation = 1;
			
			while( currentJob.shouldContinue() ) {
				train.iteration();
				interation++;
				System.out.println(interation);
			}		
		}
		catch(Throwable t)
		{
			currentJob.setError(t);
		}
		finally
		{
			this.ready.set(true);
		}		
	}
}
