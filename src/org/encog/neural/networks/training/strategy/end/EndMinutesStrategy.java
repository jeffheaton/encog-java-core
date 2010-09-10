package org.encog.neural.networks.training.strategy.end;

import java.util.concurrent.atomic.AtomicInteger;

import org.encog.neural.networks.training.Train;

public class EndMinutesStrategy implements EndTrainingStrategy {
	
	private final int minutes;
	private long startedTime;
	private boolean started;
	private final AtomicInteger minutesLeft = new AtomicInteger(0);
	
	public EndMinutesStrategy(int minutes)
	{
		this.minutes = minutes;
		started = false;
		this.minutesLeft.set(minutes);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		return started && this.minutesLeft.get()>=0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Train train) {
		this.started = true;
		this.startedTime = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		long now = System.currentTimeMillis();
		this.minutesLeft.set((int)((now - this.startedTime)/60000));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	}

	/**
	 * @return the minutesLeft
	 */
	public int getMinutesLeft() {
		return minutesLeft.get();
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}
	
	
}
