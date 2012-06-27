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

import org.encog.Encog;
import org.encog.util.EngineArray;
import org.encog.util.Format;

/**
 * A line from a Bayesian truth table.
 *
 */
public class TableLine implements Serializable {
	/**
	 * The probability.
	 */
	private double probability;
	
	/**
	 * The result.
	 */
	private final int result;
	
	/**
	 * The arguments.
	 */
	private final int[] arguments;

	/**
	 * Construct a truth table line.
	 * @param prob The probability.
	 * @param result The result.
	 * @param args The arguments.
	 */
	public TableLine(double prob, int result, int[] args) {
		this.probability = prob;
		this.result = result;
		this.arguments = EngineArray.arrayCopy(args);
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @return the arguments
	 */
	public int[] getArguments() {
		return arguments;
	}

	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("result=");
		r.append(result);
		r.append(",probability=");
		r.append(Format.formatDouble(this.probability, 2));
		r.append("|");
		for(int i=0;i<arguments.length;i++) {
			r.append(Format.formatDouble(this.arguments[i], 2));
			r.append(" ");
		}
		return r.toString();
	}

	/**
	 * Compare this truth line's arguments to others.
	 * @param args The other arguments to compare to.
	 * @return True if the same.
	 */
	public boolean compareArgs(int[] args) {
		
		if( args.length!=this.arguments.length) {
			return false;
		}
		
		for(int i=0;i<args.length;i++) {
			if( Math.abs(this.arguments[i] - args[i])>Encog.DEFAULT_DOUBLE_EQUAL) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Set the probability of this line.
	 * @param probability The probability of this line.
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	

}
