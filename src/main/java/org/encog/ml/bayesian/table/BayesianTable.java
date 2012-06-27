/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.bayesian.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;

/**
 * Holds a Bayesian truth table.
 */
public class BayesianTable implements Serializable {
	
	/**
	 * The event that owns this truth table.
	 */
	private final BayesianEvent event;
	
	/**
	 * The lines of the truth table.
	 */
	private final List<TableLine> lines = new ArrayList<TableLine>();

	public BayesianTable(BayesianEvent theEvent) {
		this.event = theEvent;
		reset();
	}

	/**
	 * Reset the truth table to zero.
	 */
	public void reset() {
		this.lines.clear();
		List<BayesianEvent> parents = this.event.getParents();
		int l = parents.size();

		int[] args = new int[l];

		do {
			for (int k = 0; k < event.getChoices().size(); k++) {
				addLine(0, k, args);
			}
		} while (EnumerationQuery.roll(parents, args));
	}

	/**
	 * Add a new line.
	 * @param prob The probability.
	 * @param result The resulting probability.
	 * @param args The arguments.
	 */
	public void addLine(double prob, boolean result, boolean... args) {
		int[] d = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			d[i] = args[i] ? 0 : 1;
		}

		addLine(prob, result ? 0 : 1, d);
		addLine(1.0 - prob, result ? 1 : 0, d);
	}

	/**
	 * Add a new line.
	 * @param prob The probability.
	 * @param result The resulting probability.
	 * @param args The arguments.
	 */
	public void addLine(double prob, int result, boolean... args) {
		int[] d = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			d[i] = args[i] ? 0 : 1;
		}

		addLine(prob, result, d);
	}

	/**
	 * Add a new line.
	 * @param prob The probability.
	 * @param result The resulting probability.
	 * @param args The arguments.
	 */
	public void addLine(double prob, int result, int... args) {
		if (args.length != this.event.getParents().size()) {
			throw new BayesianError("Truth table line with " + args.length
					+ ", specied for event with "
					+ this.event.getParents().size()
					+ " parents.  These numbers must be the same");
		}

		TableLine line = findLine(result, args);

		if (line == null) {
			if (this.lines.size() == this.getMaxLines()) {
				throw new BayesianError("This truth table is already full.");
			}

			line = new TableLine(prob, result, args);
			this.lines.add(line);
		} else {
			line.setProbability(prob);
		}
	}

	/**
	 * Validate the truth table.
	 */
	public void validate() {
		if (this.lines.size() != this.getMaxLines()) {
			throw new BayesianError("Truth table for " + this.event.toString()
					+ " only has " + this.lines
					+ " line(s), should have " + this.getMaxLines()
					+ " line(s).");
		}

	}

	/**
	 * Generate a random sampling based on this truth table.
	 * @param args The arguemtns.
	 * @return The result.
	 */
	public int generateRandom(int... args) {
		double r = Math.random();
		double limit = 0;

		for (TableLine line : this.lines) {
			if (line != null && line.compareArgs(args)) {
				limit += line.getProbability();
				if (r < limit) {
					return line.getResult();
				}
			}
		}

		throw new BayesianError("Incomplete logic table for event: "
				+ this.event.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (TableLine line : this.lines) {
			result.append(line.toString());
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * @return The lines of this truth table.
	 */
	public List<TableLine> getLines() {
		return this.lines;
	}

	/**
	 * Find the specified truth table line.
	 * @param result The result sought.
	 * @param args The arguments.
	 * @return The line that matches.
	 */
	public TableLine findLine(int result, int[] args) {

		for (TableLine line : this.lines) {
			if (line != null && line.compareArgs(args)) {
				if (Math.abs(line.getResult() - result) < Encog.DEFAULT_DOUBLE_EQUAL) {
					return line;
				}
			}
		}

		return null;
	}
	
	/**
	 * @return The maximum number of lines this truth table would have.
	 */
	public int getMaxLines() {
		return event.calculateParameterCount() * event.getChoices().size();
	}
}
