package org.encog.ml.prg.train;

import java.util.Random;

import org.encog.ml.ea.genome.CalculateGenomeScore;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;

public class PrgGenomeFactory implements GenomeFactory {

	private EncogProgramContext context;
	
	public PrgGenomeFactory(EncogProgramContext theContext) {
		this.context = theContext;
	}
	
	@Override
	public Genome factor() {
		EPLHolder newHolder = this.context.getHolderFactory().factor(1, this.context.getParams().getMaxIndividualSize());
		EncogProgram result = new EncogProgram(this.context, new EncogProgramVariables(), newHolder,0);
		return result;
	}
	
	@Override
	public void factorRandomPopulation(Random random, Population population, CalculateGenomeScore scoreFunction, int maxDepth) {
		CreateRandom rnd = new CreateRandom(this.context, maxDepth);
		EPLHolder holder = ((PrgPopulation)population).getHolder();

		population.getGenomes().clear();
		for (int i = 0; i < this.context.getParams().getPopulationSize(); i++) {
			EncogProgram prg = new EncogProgram(this.context, new EncogProgramVariables(), holder, i);
			population.getGenomes().add(prg);

			boolean done = false;
			do {
				prg.clear();
				rnd.createNode(random, prg, 0);
				double score = scoreFunction.calculateScore(prg);
				if (!Double.isInfinite(score) && !Double.isNaN(score)) {
					prg.setScore(score);
					done = true;
				}
			} while (!done);
		
			population.rewrite(prg);
		}
	}
	
}
