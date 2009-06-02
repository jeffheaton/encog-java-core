package org.encog.util.concurrency.job;

import org.encog.util.concurrency.EncogTask;

public class JobUnitWorker implements EncogTask {
	
	private JobUnitContext context;
	
	public JobUnitWorker(JobUnitContext context)
	{
		this.context = context;
	}
	
	public void run()
	{
		this.context.getOwner().performJobUnit(this.context);
	}
}
