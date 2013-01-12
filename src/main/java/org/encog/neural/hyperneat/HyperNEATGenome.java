package org.encog.neural.hyperneat;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationSIN;
import org.encog.util.obj.ChooseObject;

public class HyperNEATGenome {

	public static void buildCPPNActivationFunctions(
			ChooseObject<ActivationFunction> activationFunctions) {
		activationFunctions.add(0.25, new ActivationClippedLinear());
		activationFunctions.add(0.25, new ActivationBipolarSteepenedSigmoid());
		activationFunctions.add(0.25, new ActivationGaussian());
		activationFunctions.add(0.25, new ActivationSIN());
		
	}

}
