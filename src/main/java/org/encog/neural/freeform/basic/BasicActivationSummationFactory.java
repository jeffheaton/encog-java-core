package org.encog.neural.freeform.basic;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.freeform.InputSummation;
import org.encog.neural.freeform.factory.InputSummationFactory;

public class BasicActivationSummationFactory implements InputSummationFactory {

	@Override
	public InputSummation factor(ActivationFunction theActivationFunction) {
		return new BasicActivationSummation(theActivationFunction);
	}

}
