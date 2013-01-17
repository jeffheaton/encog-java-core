package org.encog.neural.hyperneat;

import java.util.List;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationSIN;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.util.obj.ChooseObject;

public class HyperNEATGenome extends NEATGenome {

	public static void buildCPPNActivationFunctions(
			ChooseObject<ActivationFunction> activationFunctions) {
		activationFunctions.add(0.25, new ActivationClippedLinear());
		activationFunctions.add(0.25, new ActivationBipolarSteepenedSigmoid());
		activationFunctions.add(0.25, new ActivationGaussian());
		activationFunctions.add(0.25, new ActivationSIN());	
		activationFunctions.finalizeStructure();
	}
	
	public HyperNEATGenome(final HyperNEATGenome other) {
		super(other);
	}

	public HyperNEATGenome(final long genomeID,
			final List<NEATNeuronGene> neurons, final List<NEATLinkGene> links,
			final int inputCount, final int outputCount) {
		super(genomeID,neurons,links,inputCount,outputCount);
	}

	public HyperNEATGenome(final NEATPopulation pop, final long id,
			final int inputCount, final int outputCount) {
		super(pop,id,inputCount,outputCount);

	}
	
	public HyperNEATGenome() {
		
	}
}
