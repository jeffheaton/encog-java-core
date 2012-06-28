package org.encog.ca.universe;

public interface Universe {

	int getRows();

	int getColumns();

	UniverseCell get(int otherRow, int otherCol);

	boolean isValid(int otherRow, int otherCol);
	
	Object clone();

	void copy(Universe world);

	double compare(Universe world);

	void randomize();
	
	UniverseCellFactory getCellFactory();

}
