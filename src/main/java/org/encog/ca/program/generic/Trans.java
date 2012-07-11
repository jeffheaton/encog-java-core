package org.encog.ca.program.generic;

import java.io.Serializable;

import org.encog.ca.universe.ContinuousCell;
import org.encog.ca.universe.UniverseCell;
import org.encog.ca.universe.UniverseCellFactory;

public class Trans implements Comparable<Trans>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final double limit;
	private final UniverseCell add1;
	private final UniverseCell mult;
	private final UniverseCell add2;
	private final UniverseCellFactory factory;

	public Trans(UniverseCellFactory theFactory, int index, double[] d) {
		this.factory = theFactory;
		int i = index;
		this.limit = d[i++];
		this.add1 = this.factory.factor();
		this.add1.set(i, d);
		i += this.factory.size();
		this.mult = this.factory.factor();
		this.mult.set(i, d);
		i += this.factory.size();
		this.add2 = this.factory.factor();
		this.add2.set(i, d);
	}

	@Override
	public int compareTo(Trans o) {
		return Double.compare(this.limit, o.limit);
	}

	public double getLimit() {
		return limit;
	}

	public UniverseCell getAdd1() {
		return add1;
	}

	public UniverseCell getMult() {
		return mult;
	}

	public UniverseCell getAdd2() {
		return add2;
	}

	public UniverseCell calculate(UniverseCell x) {
		UniverseCell result = this.factory.factor();
		((ContinuousCell)result).add(x);
		((ContinuousCell)result).add(add1);
		((ContinuousCell)result).multiply(mult);
		((ContinuousCell)result).add(add2);
		return result;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[Trans: limit=");
		result.append(this.limit);
		result.append("]");
		return result.toString();
	}
}
