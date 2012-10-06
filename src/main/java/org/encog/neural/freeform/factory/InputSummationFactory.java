package org.encog.neural.freeform.factory;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.freeform.InputSummation;

public interface InputSummationFactory {
	InputSummation factor(ActivationFunction activationFunction);
}
