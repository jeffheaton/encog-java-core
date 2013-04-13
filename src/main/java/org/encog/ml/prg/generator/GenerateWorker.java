package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

/**
 * Used to thread the generation process.
 */
public class GenerateWorker implements Runnable {

	/**
	 * The owner.
	 */
	private final AbstractPrgGenerator owner;
	
	/**
	 * A random number generator.
	 */
	private final Random rnd;
	
	/**
	 * The population.
	 */
	private final PrgPopulation population;

	/**
	 * Construct the worker.
	 * @param theOwner The owner.
	 * @param thePopulation The target population.
	 */
	public GenerateWorker(final AbstractPrgGenerator theOwner,
			final PrgPopulation thePopulation) {
		this.owner = theOwner;
		this.population = thePopulation;
		this.rnd = this.owner.getRandomFactory().factor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		final EncogProgram prg = this.owner.attemptCreateGenome(this.rnd,
				this.population);
		this.owner.addPopulationMember(this.population, prg);
	}

}
