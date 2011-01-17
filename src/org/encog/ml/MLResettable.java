package org.encog.ml;

public interface MLResettable extends MLMethod {
	void reset();
	void reset(int seed);
}
