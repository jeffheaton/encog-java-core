package org.encog.ca.runner;

import java.util.ArrayList;
import java.util.List;

import org.encog.ca.program.CAProgram;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseListener;

public class BasicCARunner implements CARunner, Runnable {
	private Universe universe;
	private Universe tempUniverse;
	private CAProgram physics;
	private boolean running;
	private int iteration;
	private double diff;
	private List<UniverseListener> listeners = new ArrayList<UniverseListener>();
	private Thread thread;

	public BasicCARunner(Universe theUniverse, CAProgram thePhysics) {
		init(theUniverse, thePhysics);
	}
	
	public void init(Universe theUniverse, CAProgram thePhysics)
	{
		this.universe = theUniverse;
		this.tempUniverse = (Universe) theUniverse.clone();
		this.physics = thePhysics;
	}

	public void addListener(UniverseListener listener) {
		this.listeners.add(listener);
	}

	public String toString() {
		return "Iteration: " + this.iteration + ", Diff=" + diff;
	}

	public void iteration() {
		this.tempUniverse.copy(this.universe);

		this.physics.setSourceUniverse(this.universe);
		this.physics.setTargetUniverse(this.tempUniverse);
		this.physics.iteration();
		
		this.diff = this.tempUniverse.compare(universe);
		this.iteration++;

		this.universe.copy(this.tempUniverse);

		for (UniverseListener listener : this.listeners) {
			listener.iterationComplete();
		}
	}

	public void start() {
		if (!running) {
			this.running = true;
			this.thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		this.running = false;
		try {
			if (this.thread != null) {
				this.thread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.running = true;

		while (this.running) {
			iteration();
		}
	}

	public void reset() {
		this.physics.randomize();
		this.universe.randomize();
		this.iteration = 0;
	}

	public int runToConverge(int maxIterations) {
		this.iteration = 0;
		for (;;) {
			iteration();

			if (this.iteration > 5 && this.diff < 0.01)
				break;

			if (this.iteration > maxIterations)
				break;
		}
		return this.iteration;
	}
	
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public Universe getUniverse() {
		return this.universe;
	}

	@Override
	public CAProgram getPhysics() {
		return this.physics;
	}

	@Override
	public int runToConverge(int i, double desiredScore) {
		do {
			this.iteration();
			//System.out.println(this.iteration);
		} while( (this.iteration<25 || this.diff>desiredScore) && this.iteration<i);
		
		return this.iteration;
	}

	@Override
	public double getScore() {
		return this.diff;
	}
	
	
}
