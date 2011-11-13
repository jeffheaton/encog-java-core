package org.encog.ml.world;

public class SuccessorState implements Comparable<SuccessorState> {
	
	private final State state;
	private final double probability;
	private final long serialNumber;
	private static long serialCounter;
	
	public SuccessorState(State state, double probability) {
		super();
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
	
	
}
