package org.encog.ca.runner;

import org.encog.ca.universe.UniverseListener;

public interface CARunner {

	void addListener(UniverseListener worldViewer);

	void iteration();

	void start();

	void stop();

	void reset();

}
