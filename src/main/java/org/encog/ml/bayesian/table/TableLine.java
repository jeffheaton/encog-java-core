package org.encog.ml.bayesian.table;

import org.encog.Encog;
import org.encog.util.EngineArray;
import org.encog.util.Format;

public class TableLine {
	
	private final double probability;
	private final double result;
	private final double[] arguments;

	public TableLine(double prob, double result, double[] args) {
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
	public double[] getArguments() {
		return arguments;
	}

	/**
	 * @return the result
	 */
	public double getResult() {
		return result;
	}

	public String toString() {
		StringBuilder r = new StringBuilder();
		r.append("result=");
		r.append(Format.formatDouble(result, 2));
		r.append(",probability=");
		r.append(Format.formatDouble(this.probability, 2));
		r.append("|");
		for(int i=0;i<arguments.length;i++) {
			r.append(Format.formatDouble(this.arguments[i], 2));
			r.append(" ");
		}
		return r.toString();
	}

	public boolean compareArgs(double[] args) {
		
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
