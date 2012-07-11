package org.encog.ca.universe;

public interface ContinuousCell extends UniverseCell {

	void add(UniverseCell x);

	void multiply(UniverseCell mult);

	void set(int i, double[] d);

	void clamp(double low, double high);
	
}
