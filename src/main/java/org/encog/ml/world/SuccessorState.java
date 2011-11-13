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
			System.out.println("Danger");
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
