package org.encog.ca.runner;

import org.encog.ca.program.CAProgram;
import org.encog.ca.universe.Universe;
import org.encog.ca.universe.UniverseListener;

public interface CARunner {

	void addListener(UniverseListener worldViewer);

	void iteration();

	void start();

	void stop();

	void reset();

	boolean isRunning();

	Universe getUniverse();

	CAProgram getPhysics();

	void init(Universe universe, CAProgram physics);

	int runToConverge(int i, double desiredScore);

	double getScore();

}
