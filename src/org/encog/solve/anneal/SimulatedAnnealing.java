/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.solve.anneal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulated annealing is a common training method. This class implements a
 * simulated annealing algorithm that can be used both for neural networks, as
 * well as more general cases. This class is abstract, so a more specialized
 * simulated annealing subclass will need to be created for each intended use.
 * This book demonstrates how to use the simulated annealing algorithm to train
 * feedforward neural networks, as well as find a solution to the traveling
 * salesman problem.
 * 
 * The name and inspiration come from annealing in metallurgy, a technique
 * involving heating and controlled cooling of a material to increase the size
 * of its crystals and reduce their defects. The heat causes the atoms to become
 * unstuck from their initial positions (a local minimum of the internal energy)
 * and wander randomly through states of higher energy; the slow cooling gives
 * them more chances of finding configurations with lower internal energy than
 * the initial one.
 * 
 * @param <UNIT_TYPE>
 *            What type of data makes up the solution.
 */
public abstract class SimulatedAnnealing<UNIT_TYPE> {

	/**
	 * The starting temperature.
	 */
	private double startTemperature;
	/**
	 * The ending temperature.
	 */
	private double stopTemperature;
	/**
	 * The number of cycles that will be used.
	 */
	private int cycles;
	/**
	 * The current score.
	 */
	private double score;

	/**
	 * The current temperature.
	 */
	private double temperature;
	
	private boolean shouldMinimize = true;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Subclasses should provide a method that evaluates the score for the
	 * current solution. Those solutions with a lower score are better.
	 * 
	 * @return Return the score.
	 */
	public abstract double calculateScore();

	/**
	 * Subclasses must provide access to an array that makes up the solution.
	 * 
	 * @return An array that makes up the solution.
	 */
	public abstract UNIT_TYPE[] getArray();

	/**
	 * Get a copy of the array.
	 * 
	 * @return A copy of the array.
	 */
	public abstract UNIT_TYPE[] getArrayCopy();

	/**
	 * @return the cycles
	 */
	public int getCycles() {
		return this.cycles;
	}

	/**
	 * @return the globalScore
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @return the startTemperature
	 */
	public double getStartTemperature() {
		return this.startTemperature;
	}

	/**
	 * @return the stopTemperature
	 */
	public double getStopTemperature() {
		return this.stopTemperature;
	}

	/**
	 * @return the temperature
	 */
	public double getTemperature() {
		return this.temperature;
	}

	/**
	 * Called to perform one cycle of the annealing process.
	 */
	public void iteration() {
		UNIT_TYPE[] bestArray;

		setScore(calculateScore());
		bestArray = this.getArrayCopy();

		this.temperature = this.getStartTemperature();

		for (int i = 0; i < this.cycles; i++) {
			double curScore;
			randomize();
			curScore = calculateScore();
			
			if (!this.shouldMinimize) {
				if (curScore < getScore()) {
					bestArray = this.getArrayCopy();
					setScore(curScore);
				}
			}
			else {
				if (curScore > getScore()) {
					bestArray = this.getArrayCopy();
					setScore(curScore);
				}
			}

			this.putArray(bestArray);
			final double ratio = Math.exp(Math.log(getStopTemperature()
					/ getStartTemperature())
					/ (getCycles() - 1));
			this.temperature *= ratio;
		}
	}

	/**
	 * Store the array.
	 * 
	 * @param array
	 *            The array to be stored.
	 */
	public abstract void putArray(UNIT_TYPE[] array);

	/**
	 * Randomize the weight matrix.
	 */
	public abstract void randomize();

	/**
	 * @param cycles
	 *            the cycles to set
	 */
	public void setCycles(final int cycles) {
		this.cycles = cycles;
	}

	/**
	 * Set the score.
	 * 
	 * @param score
	 *            The score to set.
	 */
	public void setScore(final double score) {
		this.score = score;
	}

	/**
	 * @param startTemperature
	 *            the startTemperature to set
	 */
	public void setStartTemperature(final double startTemperature) {
		this.startTemperature = startTemperature;
	}

	/**
	 * @param stopTemperature
	 *            the stopTemperature to set
	 */
	public void setStopTemperature(final double stopTemperature) {
		this.stopTemperature = stopTemperature;
	}

	/**
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(final double temperature) {
		this.temperature = temperature;
	}

	public boolean isShouldMinimize() {
		return shouldMinimize;
	}

	public void setShouldMinimize(boolean shouldMinimize) {
		this.shouldMinimize = shouldMinimize;
	}
	
	

}
