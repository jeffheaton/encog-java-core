package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

public class GenerateWorker implements Runnable {

	private AbstractGenerator owner;
	private final Random rnd;
	private PrgPopulation population;
	
	public GenerateWorker(AbstractGenerator theOwner, PrgPopulation thePopulation) {
		this.owner = theOwner;
		this.population = thePopulation;
		this.rnd = this.owner.getRandomFactory().factor();
	}

	@Override
	public void run() {
		final EncogProgram prg = owner.attemptCreateGenome(rnd, this.population);
		this.owner.addPopulationMember(this.population,prg);
	}

}
