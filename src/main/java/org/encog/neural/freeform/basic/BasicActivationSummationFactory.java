package org.encog.neural.freeform.basic;

import java.io.Serializable;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.freeform.InputSummation;
import org.encog.neural.freeform.factory.InputSummationFactory;

public class BasicActivationSummationFactory implements InputSummationFactory, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public InputSummation factor(ActivationFunction theActivationFunction) {
		return new BasicActivationSummation(theActivationFunction);
	}

}
