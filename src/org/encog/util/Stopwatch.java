package org.encog.util;

/**
 * A stopwatch, meant to emulate the C# Stopwatch class.
 */
public class Stopwatch {
	private boolean stopped;
	private long startTime;
	private long stopTime;
	
	public Stopwatch()
	{
		reset();
		stopped = false;
	}
	
	public void reset() {
		this.startTime = System.nanoTime();
		this.stopTime = System.nanoTime();
		stopped = false;
	}

	public void start() {
		this.startTime = System.nanoTime();
		this.stopped = false;
	}

	public void stop() {
		this.stopTime = System.nanoTime();
		this.stopped = true;
	}

	public long getElapsedTicks() {
		if( !stopped )
		{
			stopTime = System.nanoTime();
		}
		
		return (stopTime - startTime)/1000;
	}

	public long getElapsedMilliseconds() {
		return getElapsedTicks()/1000;
	}

}
