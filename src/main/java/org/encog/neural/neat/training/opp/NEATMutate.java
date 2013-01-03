package org.encog.neural.neat.training.opp;

import java.util.Random;

import org.encog.EncogError;
import org.encog.mathutil.randomize.RandomChoice;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class NEATMutate implements EvolutionaryOperator {

	private NEATTraining owner;
	private RandomChoice mutateChoices;	
	private RandomChoice mutateAddChoices;
	private final double mutateRate = 0.2;
	private final double probNewMutate = 0.1;
	private final double maxPertubation = 0.1;
	
	
	public NEATMutate() {
		this.mutateChoices = new RandomChoice(new double[] {0.988, 0.001, 0.01, 0.0, 0.001 } );
		this.mutateAddChoices = new RandomChoice(new double[] {0.988, 0.001, 0.01, 0.0 } );
	}
	
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		
		if( parents[0]!=offspring[0] ) {
			throw new EncogError("This mutation only works when the offspring and parents are the same.  That is, it only mutates itself.");
		}
		
		NEATGenome genome = (NEATGenome)parents[0];
		
		int option = this.mutateChoices.generate(new Random());
		
		switch(option) {
			case 0: // mutate weight
				genome.mutateWeights(mutateRate,
						probNewMutate,
						maxPertubation);
				break;
			case 1: // add node
				if (genome.getNeuronsChromosome().size() < this.owner.getMaxIndividualSize() ) {
					genome.addNeuron(owner, 5);
				}
				break;
			case 2: // add connection
				// now there's the chance a link may be added
				genome.addLink(	this.owner, 5, 5);
				break;
			case 3: // adjust curve
				break;
			case 4: // remove connection
				genome.removeLink();
				break;
		}
		
	}

	@Override
	public int offspringProduced() {
		return 1;
	}

	@Override
	public int parentsNeeded() {
		return 1;
	}

	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		if( !(theOwner instanceof NEATTraining) ) {
			throw new EncogError("This operator only works with a NEATTraining trainer.");
		}
		
		this.owner = (NEATTraining)theOwner;		
	}

}
