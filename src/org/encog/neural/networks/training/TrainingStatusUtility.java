package org.encog.neural.networks.training;

import org.encog.cloud.CloudTask;
import org.encog.cloud.EncogCloud;
import org.encog.util.Format;

/**
 * Report the status of Encog training to the cloud.
 */
public class TrainingStatusUtility {

	/**
	 * The last time an update was performed.
	 */
	private long lastUpdate;
	
	/**
	 * The iteration number.
	 */
	private int iteration;
	
	/**
	 * The training object.
	 */
	private Train train;
	
	/**
	 * The cloud.
	 */
	private EncogCloud cloud;
	
	/**
	 * The task that we are reporting to.
	 */
	private CloudTask task;
	
	/**
	 * Create a training status utility.
	 * @param cloud The cloud to report to.
	 * @param train The training object being used.
	 */
	public TrainingStatusUtility(EncogCloud cloud, Train train)
	{
		this.cloud = cloud;
		this.train = train;
		this.lastUpdate = 0;
		this.iteration = 0;
	}
	
	/**
	 * Perform an update.
	 */
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
	
	/**
	 * Report that we are finished.
	 */
	public void finish()
	{
		StringBuilder status = new StringBuilder();
		status.append("Done at iteration #");
		status.append(Format.formatInteger(this.iteration));
		status.append(" - Error: ");
		status.append(Format.formatPercent(train.getError()));
		
		this.task.stop(status.toString());
		this.task = null;
	}
}
