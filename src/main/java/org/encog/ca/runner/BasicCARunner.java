/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
	private double percentChanged;
	private double percentInvalid;
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
		return "Iteration: " + this.iteration + ", Diff=" + percentChanged+ ", Invalid="+ this.percentInvalid+", Score=" + this.getScore();
	}

	public void iteration() {
		this.tempUniverse.copy(this.universe);

		this.physics.setSourceUniverse(this.universe);
		this.physics.setTargetUniverse(this.tempUniverse);
		this.physics.iteration();
		
		this.percentChanged = this.tempUniverse.compare(universe);
		this.percentInvalid = this.tempUniverse.calculatePercentInvalid();
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

			if (this.iteration > 5 && this.percentChanged < 0.01)
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
		this.iteration = 0;
		do {
			this.iteration();

		} while( (this.iteration<25 || this.percentChanged>desiredScore) && this.iteration<i);
		
		return this.iteration;
	}

	@Override
	public double getScore() {
		if( this.percentChanged<0.2 || this.percentChanged >0.5 ) {
			return 0;
		}
		
		double score = 1.0 + ( 0.5 - (this.percentChanged-0.2) - this.percentInvalid);
		
		return score;
	}
	
	
}
