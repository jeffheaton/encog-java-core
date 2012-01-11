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
package org.encog.ml.world;

import org.encog.util.Format;

public class SuccessorState implements Comparable<SuccessorState> {
	
	private final State state;
	private final double probability;
	private final long serialNumber;
	private static long serialCounter;
	
	public SuccessorState(State state, double probability) {
		super();
		if( state==null ) {
			throw new WorldError("Can't create null successor state");
		}
		this.state = state;
		this.probability = probability;
		synchronized(SuccessorState.class) {
			this.serialNumber = serialCounter++;
		}
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	@Override
	public int compareTo(SuccessorState o) {
		if( o.getProbability()==this.getProbability()) {
			return o.serialNumber<this.serialNumber?1:-1;
		}
		if( o.getProbability()<this.getProbability())
			return 1;
		return -1;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SuccessorState: state=");
		result.append(this.state.toString());
		result.append(", prob=");
		result.append(Format.formatPercent(this.probability));
		result.append("]");
		return result.toString();
	}
	
}
