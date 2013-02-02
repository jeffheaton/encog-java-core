package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATNeuronGene;

public class NEATMutateAddLink extends NEATMutation{

	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		int countTrysToAddLink = this.getOwner().getMaxTries();
		
		NEATGenome target = this.obtainGenome(rnd, parents, parentIndex, offspring, offspringIndex);
		
		
		// the link will be between these two neurons
				long neuron1ID = -1;
				long neuron2ID = -1;

				// try to add a link
				while ((countTrysToAddLink--) > 0) {
					final NEATNeuronGene neuron1 = chooseRandomNeuron(target, true);
					final NEATNeuronGene neuron2 = chooseRandomNeuron(target, false);
					
					if( neuron1==null || neuron2==null ) {
						return;
					}

					if (!isDuplicateLink(target, neuron1.getId(), neuron2.getId()) // no duplicates
							&& (neuron2.getNeuronType() != NEATNeuronType.Bias) // do not go to a bias neuron
							&& (neuron1.getNeuronType() != NEATNeuronType.Output) // do not go from an output neuron
							&& (neuron2.getNeuronType() != NEATNeuronType.Input)) { // do not go to an input neuron

						neuron1ID = neuron1.getId();
						neuron2ID = neuron2.getId();
						break;
					}
				}

				// did we fail to find a link
				if ((neuron1ID < 0) || (neuron2ID < 0)) {
					return;
				}

				double r = ((NEATPopulation)target.getPopulation()).getWeightRange();
				createLink(target, neuron1ID, neuron2ID, RangeRandomizer.randomize(-r,r));
	}
	
}
