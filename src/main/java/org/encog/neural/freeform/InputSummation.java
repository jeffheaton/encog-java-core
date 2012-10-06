package org.encog.neural.freeform;

import java.util.List;

import org.encog.engine.network.activation.ActivationFunction;

public interface InputSummation {
	List<FreeformConnection> list();
	double calculate();
	void add(FreeformConnection connection);
	double getSum();
	ActivationFunction getActivationFunction();
}
