package org.encog.neural.neat.training;

import java.util.Random;

import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATSpecies;

public class NEATTrainWorker implements Runnable {

	NEATSpecies species;
	NEATGenome[] parents = new NEATGenome[2];
	NEATGenome[] children = new NEATGenome[1];
	Random rnd = new Random();
	NEATPopulation population;
	NEATTraining train;

	public NEATTrainWorker(NEATTraining theTrain, NEATSpecies theSpecies) {
		this.train = theTrain;
		this.species = theSpecies;
		this.population = this.train.getNEATPopulation();
	}

	@Override
	public void run() {
		int numToSpawn = (int) Math.round(this.species.getOffspringCount());

		// Add elite genomes directly
		if (this.species.getMembers().size() > 5) {
			int eliteCount = Math.min(numToSpawn,  species.getEliteSize());
			//System.out.println("Elite Spwan: " + this.species.getEliteSize() + " of " + this.species.getMembers().size());
			for(int i=0;i<eliteCount;i++) {
				numToSpawn--;
				if (!this.train.addChild(this.species.getMembers().get(i))) {
					return;
				}	
			}
		}

		EvolutionaryOperator opp = this.train.getOperators().pickMaxParents(
				this.rnd, species.getMembers().size());

		while ((numToSpawn--) > 0) {
			children[0] = null;

			// prepare for either sexual or asexual reproduction either way, we
			// need at least
			// one parent, which is the first parent.
			//
			// Chose the first parent, there must be at least one genome in this
			// species
			parents[0] = (NEATGenome) this.species.chooseParent();

			// if the number of individuals in this species is only
			// one then we can only clone and perhaps mutate, otherwise use
			// the crossover probability to determine if we are to use
			// sexual reproduction.
			if (opp.parentsNeeded() > 1) {

				int numAttempts = 5;

				parents[1] = (NEATGenome) species.chooseParent();
				while ((parents[0].getGenomeID() == parents[1].getGenomeID())
						&& ((numAttempts--) > 0)) {
					parents[1] = (NEATGenome) species.chooseParent();
				}

				// success, perform crossover
				if (parents[0].getGenomeID() != parents[1].getGenomeID()) {
					opp.performOperation(rnd, parents, 0, children, 0);
				}
			} else {
				// clone a child (asexual reproduction)
				children[0] = ((NEATGenomeFactory) this.population
						.getGenomeFactory()).factor(parents[0]);
				opp.performOperation(rnd, children, 0, children, 0);
			}

			// process the new child
			if (children[0] != null) {
				children[0].setGenomeID(population.assignGenomeID());
				children[0].setBirthGeneration(this.train.getIteration());

				// sort the baby's genes by their innovation numbers
				children[0].sortGenes();

				this.train.calculateScore(children[0]);
				if (!this.train.addChild(children[0])) {
					return;
				}
			}
		}
		
	}
}
