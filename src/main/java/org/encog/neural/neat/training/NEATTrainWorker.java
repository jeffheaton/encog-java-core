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
	
	public NEATTrainWorker(NEATTraining theTrain, NEATSpecies theSpecies, double theCrossoverRate) {
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

		boolean bChosenBestYet = false;

		while ((numToSpawn--) > 0) {
			children[0] = null;

			if (!bChosenBestYet) {
				children[0] = (NEATGenome) this.species.getLeader();

				bChosenBestYet = true;
			}

			else {
				// if the number of individuals in this species is only
				// one
				// then we can only perform mutation
				if (this.species.getMembers().size() == 1) {
					// spawn a child
					children[0] = ((NEATGenomeFactory) this.population
							.getGenomeFactory()).factor((NEATGenome) species
							.chooseParent());
				} else {
					parents[0] = (NEATGenome) this.species.chooseParent();

					if (Math.random() < this.crossoverRate) {
						parents[1] = (NEATGenome) this.species.chooseParent();

						int numAttempts = 5;

						while ((parents[0].getGenomeID() == parents[1]
								.getGenomeID()) && ((numAttempts--) > 0)) {
							parents[1] = (NEATGenome) species.chooseParent();
						}

						if (parents[0].getGenomeID() != parents[1]
								.getGenomeID()) {
							this.crossover.performOperation(rnd, parents, 0,
									children, 0);
						}
					}

					else {
						children[0] = ((NEATGenomeFactory) this.population
								.getGenomeFactory()).factor(parents[0]);
					}
				}

				if (children[0] != null) {
					children[0].setGenomeID(population.assignGenomeID());
					this.mutate.performOperation(rnd, children, 0, children, 0);
				}
			}

			if (children[0] != null) {
				// sort the baby's genes by their innovation numbers
				children[0].sortGenes();

				if (!this.train.addChild(children[0])) {
					return;
				}
			}
		}
	}

}
