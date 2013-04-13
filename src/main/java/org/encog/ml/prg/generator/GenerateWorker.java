package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

public class GenerateWorker implements Runnable {

	private final AbstractPrgGenerator owner;
	private final Random rnd;
	private final PrgPopulation population;

	public GenerateWorker(final AbstractPrgGenerator theOwner,
			final PrgPopulation thePopulation) {
		this.owner = theOwner;
		this.population = thePopulation;
		this.rnd = this.owner.getRandomFactory().factor();
	}

	@Override
	public void run() {
		final EncogProgram prg = this.owner.attemptCreateGenome(this.rnd,
				this.population);
		this.owner.addPopulationMember(this.population, prg);
	}

}
