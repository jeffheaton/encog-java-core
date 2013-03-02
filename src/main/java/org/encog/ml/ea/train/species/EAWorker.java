package org.encog.ml.ea.train.species;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;
import org.encog.neural.neat.training.NEATGenome;

public class EAWorker implements Runnable {

	private final Species species;
	private Genome[] parents;
	private Genome[] children;
	private final Random rnd;
	private final Population population;
	private final SpeciesEA train;

	public EAWorker(SpeciesEA theTrain, Species theSpecies) {
		this.train = theTrain;
		this.species = theSpecies;
		this.population = this.train.getPopulation();
		this.rnd = this.train.getRandomNumberFactory().factor();
		
		this.parents = new Genome[this.train.getOperators().maxParents()];
		this.children = new Genome[this.train.getOperators().maxOffspring()];
	}
	
	private Genome chooseParent() {
		int idx = this.train.getSelection().performSelection(rnd, species);
		return this.species.getMembers().get(idx);
	}

	@Override
	public void run() {
		try {
			int numToSpawn = (int) Math.round(this.species.getOffspringCount());

			// Add elite genomes directly
			if (this.species.getMembers().size() > 5) {
				int idealEliteCount = (int)(species.getMembers().size() * this.train.getEliteRate());
				int eliteCount = Math.min(numToSpawn, idealEliteCount);
				for (int i = 0; i < eliteCount; i++) {
					Genome eliteGenome = this.species.getMembers().get(i);
					if (this.train.getOldBestGenome() != eliteGenome) {
						numToSpawn--;
						if (!this.train.addChild(eliteGenome)) {
							return;
						}
					}
				}
			}

			// handle the rest of the offspring
			while (numToSpawn > 0) {
				// choose an evolutionary operation (i.e. crossover or a type of
				// mutation) to use
				EvolutionaryOperator opp = this.train.getOperators()
						.pickMaxParents(this.rnd, species.getMembers().size());

				children[0] = null;

				// prepare for either sexual or asexual reproduction either way,
				// we
				// need at least
				// one parent, which is the first parent.
				//
				// Chose the first parent, there must be at least one genome in
				// this
				// species
				parents[0] = chooseParent();

				// if the number of individuals in this species is only
				// one then we can only clone and perhaps mutate, otherwise use
				// the crossover probability to determine if we are to use
				// sexual reproduction.
				if (opp.parentsNeeded() > 1) {

					int numAttempts = 5;

					parents[1] = chooseParent();
					while ((parents[0] == parents[1]) && ((numAttempts--) > 0)) {
						parents[1] = chooseParent();
					}

					// success, perform crossover
					if (parents[0] != parents[1]) {
						opp.performOperation(rnd, parents, 0, children, 0);
					}
				} else {
					// clone a child (asexual reproduction)
					children[0] = this.population.getGenomeFactory().factor(parents[0]);
					opp.performOperation(rnd, children, 0, children, 0);
				}

				// process the new child
				if (children[0] != null) {
					numToSpawn--;
					children[0].setBirthGeneration(this.train.getIteration());

					this.train.calculateScore(children[0]);
					if (!this.train.addChild(children[0])) {
						return;
					}
				}
			}
		} catch (Throwable t) {
			this.train.reportError(t);
		}

	}
}
