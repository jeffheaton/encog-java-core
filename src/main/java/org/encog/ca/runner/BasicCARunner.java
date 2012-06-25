package org.encog.ca.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.ca.program.CAProgram;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseListener;

public class BasicCARunner implements CARunner, Runnable {
	private Universe world;
	private Universe tempWorld;
	private CAProgram physics;
	private boolean running;
	private int iteration;
	private double diff;
	private List<UniverseListener> listeners = new ArrayList<UniverseListener>();

	/**
	 * The event used to sync waiting for tasks to stop.
	 */
	private final Lock accessLock = new ReentrantLock();

	/**
	 * Condition used to check if we are done.
	 */
	private final Condition mightBeDone = this.accessLock.newCondition();

	public BasicCARunner(Universe theWorld, CAProgram thePhysics) {
		this.world = theWorld;
		this.tempWorld = (Universe) theWorld.clone();
		this.physics = thePhysics;
	}

	public void addListener(UniverseListener listener) {
		this.listeners.add(listener);
	}

	public String toString() {
		return "Iteration: " + this.iteration + ", Diff=" + diff;
	}

	public void iteration() {
		int height = world.getRows();
		int width = world.getColumns();

		try {
			this.accessLock.lock();
			this.tempWorld.copy(this.world);
			
			this.physics.setTargetUniverse(this.tempWorld);
			this.physics.iteration();
							
			this.diff = this.tempWorld.compare(world);
			this.iteration++;
			
			this.world.copy(this.tempWorld);

			for (UniverseListener listener : this.listeners) {
				listener.iterationComplete();
			}
		} finally {
			this.accessLock.unlock();
		}
	}

	public void start() {
		if (!running) {
			this.running = true;
			Thread t = new Thread(this);
			t.start();
		}
	}

	public void stop() {
		this.running = false;
	}

	@Override
	public void run() {
		this.running = true;

		while (this.running) {
			iteration();
		}
	}

	public void reset() {
		this.accessLock.lock();
		this.physics.randomize();
		this.world.randomize();
		this.iteration = 0;
		this.accessLock.unlock();
	}

	public int runToConverge(int maxIterations) {
		this.iteration = 0;
		for(;;) {
			iteration();
			
			if( this.iteration>5 && this.diff<0.01 )
				break;
			
			if( this.iteration>maxIterations ) 
				break;
		}
		return this.iteration;
	}
}
