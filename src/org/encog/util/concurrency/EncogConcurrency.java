package org.encog.util.concurrency;

public class EncogConcurrency {

	private int threadCount;
	private static EncogConcurrency instance;
	
	public static EncogConcurrency getInstance()
	{
		if( instance==null )
			instance = new EncogConcurrency();
		return instance;
	}
	
	public void processTask(EncogTask task)
	{
		task.run();
	}	
}
