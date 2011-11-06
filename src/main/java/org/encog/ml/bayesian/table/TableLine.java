package org.encog.ml.bayesian.table;

import java.io.Serializable;

import org.encog.Encog;
import org.encog.util.EngineArray;
import org.encog.util.Format;

public class TableLine implements Serializable {
	
	private final double probability;
	private final int result;
	private final int[] arguments;

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
	

}
