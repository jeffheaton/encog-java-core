package org.encog.ml.ea.train.basic;

import java.util.Random;
import java.util.concurrent.Callable;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.species.Species;

public class EAWorker implements Callable<Object> {

	private final Species species;
	private final Genome[] parents;
	private final Genome[] children;
	private final Random rnd;
	private final Population population;
	private final BasicEA train;

	public EAWorker(final BasicEA theTrain, final Species theSpecies) {
		this.train = theTrain;
		this.species = theSpecies;
		this.population = this.train.getPopulation();
		this.rnd = this.train.getRandomNumberFactory().factor();

		this.parents = new Genome[this.train.getOperators().maxParents()];
		this.children = new Genome[this.train.getOperators().maxOffspring()];
	}

	private Genome chooseParent() {
		final int idx = this.train.getSelection().performSelection(this.rnd,
				this.species);
		return this.species.getMembers().get(idx);
	}

	@Override
	public Object call() {
		boolean success = false;
		do {
			try {
				// choose an evolutionary operation (i.e. crossover or a type of
				// mutation) to use
				final EvolutionaryOperator opp = this.train.getOperators()
						.pickMaxParents(this.rnd,
								this.species.getMembers().size());

				this.children[0] = null;

				// prepare for either sexual or asexual reproduction either way,
				// we
				// need at least
				// one parent, which is the first parent.
				//
				// Chose the first parent, there must be at least one genome in
				// this
				// species
				this.parents[0] = chooseParent();

				// if the number of individuals in this species is only
				// one then we can only clone and perhaps mutate, otherwise use
				// the crossover probability to determine if we are to use
				// sexual reproduction.
				if (opp.parentsNeeded() > 1) {

					int numAttempts = 5;

					this.parents[1] = chooseParent();
					while (this.parents[0] == this.parents[1]
							&& numAttempts-- > 0) {
						this.parents[1] = chooseParent();
					}

					// success, perform crossover
					if (this.parents[0] != this.parents[1]) {
						opp.performOperation(this.rnd, this.parents, 0,
								this.children, 0);
						for( Genome child : this.children )
							child.setPopulation(this.parents[0].getPopulation());
					}
				} else {
					// clone a child (asexual reproduction)
					opp.performOperation(this.rnd, this.parents, 0,
							this.children, 0);
				}

				// process the new child
				if (this.children[0] != null) {
					success = true;
					this.children[0].setBirthGeneration(this.train
							.getIteration());

					this.train.calculateScore(this.children[0]);
					if (!this.train.addChild(this.children[0])) {
						return null;
					}
				}
			} catch (final Throwable t) {
				System.out.println("error");
				if (!this.train.getShouldIgnoreExceptions()) {
					this.train.reportError(t);
				}
			}

		} while (!success);
		return null;
	}
}
