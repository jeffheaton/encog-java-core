package org.encog.ca.universe;

public interface UniverseCell {

	void randomize();

	void copy(UniverseCell universeCell);

	double getAvg();

	double get(int i);

	void set(int i, double d);

}
