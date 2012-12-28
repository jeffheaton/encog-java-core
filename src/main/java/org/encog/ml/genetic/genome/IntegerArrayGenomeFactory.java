package org.encog.ml.genetic.genome;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;

public class IntegerArrayGenomeFactory implements GenomeFactory {
	
	private int size;
	
	public IntegerArrayGenomeFactory(int theSize) {
		this.size = theSize;
	}

	@Override
	public Genome factor() {
		return new IntegerArrayGenome(this.size);
	}

	@Override
	public void factorRandomPopulation(Random random, Population population,
			CalculateGenomeScore scoreFunction, int maxDepth) {
		// TODO Auto-generated method stub
		
	}

}
