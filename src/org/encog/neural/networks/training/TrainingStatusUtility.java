package org.encog.neural.networks.training;

import org.encog.cloud.CloudTask;
import org.encog.cloud.EncogCloud;
import org.encog.util.Format;

public class TrainingStatusUtility {
	
	private long lastUpdate;
	private int iteration;
	private Train train;
	private EncogCloud cloud;
	private CloudTask task;
	
	public TrainingStatusUtility(EncogCloud cloud, Train train)
	{
		this.cloud = cloud;
		this.train = train;
		this.lastUpdate = 0;
		this.iteration = 0;
	}
	
	public void update()
	{
		long now = System.currentTimeMillis();
		long elapsed = (now-lastUpdate)/1000;
		
		if( task==null )
		{
			this.task = this.cloud.beginTask(train.getClass().getSimpleName());
		}
		
		this.iteration++;
		
		if( elapsed>10 )
		{
			this.lastUpdate = now;
			StringBuilder status = new StringBuilder();
			status.append("Iteration #");
			status.append(Format.formatInteger(this.iteration));
			status.append(" - Error: ");
			status.append(Format.formatPercent(train.getError()));
			this.task.setStatus(status.toString());
		}
	}
	
	public void finish()
	{
		StringBuilder status = new StringBuilder();
		status.append("Done at iteration #");
		status.append(Format.formatInteger(this.iteration));
		status.append(" - Error: ");
		status.append(Format.formatPercent(train.getError()));
		this.task.setStatus(status.toString());
		
		this.task.stop();
		this.task = null;
	}
}
