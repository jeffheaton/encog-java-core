package org.encog.ml.genetic.genome;

import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;

public class DoubleArrayGenomeFactory implements GenomeFactory {
	
	private int size;
	
	public DoubleArrayGenomeFactory(int theSize) {
		this.size = theSize;
	}

	@Override
	public Genome factor() {
		return new DoubleArrayGenome(this.size);
	}	
}
