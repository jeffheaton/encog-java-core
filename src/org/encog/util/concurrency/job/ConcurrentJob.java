package org.encog.util.concurrency.job;

import org.encog.StatusReportable;
import org.encog.util.concurrency.EncogConcurrency;

public abstract class ConcurrentJob {	
	
	private StatusReportable report;
	private int totalTasks;
	
	public abstract Object requestNextTask();
	public abstract int loadWorkload();
	public abstract void performJobUnit(JobUnitContext context);
	
	
	public ConcurrentJob(StatusReportable report)
	{
		this.report = report;
	}
	
	public void start()
	{
		Object task;
		
		this.totalTasks = loadWorkload();
		int currentTask = 0;
		
		while( (task=requestNextTask())!=null )
		{
			currentTask++;
			JobUnitContext context = new JobUnitContext();
			context.setJobUnit(task);
			context.setOwner(this);
			context.setTaskNumber(currentTask);
			
			JobUnitWorker worker = new JobUnitWorker(context);
			EncogConcurrency.getInstance().processTask(worker);
		}
		
		EncogConcurrency.getInstance().shutdown(Long.MAX_VALUE);
	}
	
	public void reportStatus(JobUnitContext context,String status)
	{
		this.report.report(totalTasks, context.getTaskNumber(), status);
	}
}
