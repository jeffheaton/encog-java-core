package org.encog.neural.neat.training;

import java.util.Random;

import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.opp.NEATCrossover;
import org.encog.neural.neat.training.opp.NEATMutate;

public class NEATTrainWorker implements Runnable {

	NEATSpecies species;
	NEATGenome[] parents = new NEATGenome[2];
	NEATGenome[] children = new NEATGenome[1];
	Random rnd = new Random();
	NEATPopulation population;
	NEATTraining train;
	double crossoverRate;
	NEATCrossover crossover;
	NEATMutate mutate;

	public NEATTrainWorker(NEATTraining theTrain, NEATSpecies theSpecies,
			double theCrossoverRate) {
		this.train = theTrain;
		this.species = theSpecies;
		this.crossoverRate = theCrossoverRate;
		this.crossover = this.train.getCrossover();
		this.mutate = this.train.getMutate();
		this.population = this.train.getNEATPopulation();

	}

	@Override
	public void run() {
		int numToSpawn = (int) Math.round(this.species.getNumToSpawn());

		// first, add the leader to the spawn count.
		numToSpawn--;
		if (!this.train.addChild(this.species.getLeader())) {
			return;
		}

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
			if ((this.species.getMembers().size() > 1)
					|| (Math.random() < this.crossoverRate)) {

				int numAttempts = 5;

				parents[1] = (NEATGenome) species.chooseParent();
				while ((parents[0].getGenomeID() == parents[1].getGenomeID())
						&& ((numAttempts--) > 0)) {
					parents[1] = (NEATGenome) species.chooseParent();
				}

				// success, perform crossover
				if (parents[0].getGenomeID() != parents[1].getGenomeID()) {
					this.crossover.performOperation(rnd, parents, 0, children,
							0);
				}
			} else {
				// clone a child (asexual reproduction)
				children[0] = ((NEATGenomeFactory) this.population
						.getGenomeFactory()).factor((NEATGenome) species
						.chooseParent());
			}

			// process the new child
			if (children[0] != null) {
				children[0].setGenomeID(population.assignGenomeID());
				this.mutate.performOperation(rnd, children, 0, children, 0);
			}

			if (children[0] != null) {
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
