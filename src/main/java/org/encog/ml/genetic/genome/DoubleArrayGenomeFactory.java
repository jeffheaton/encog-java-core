package org.encog.ml.genetic.genome;

import java.util.Random;

import org.encog.ml.genetic.population.Population;

public class DoubleArrayGenomeFactory implements GenomeFactory {
	
	private int size;
	
	public DoubleArrayGenomeFactory(int theSize) {
		this.size = theSize;
	}

	@Override
	public Genome factor() {
		return new DoubleArrayGenome(this.size);
	}

	@Override
	public void factorRandomPopulation(Random random, Population population,
			CalculateGenomeScore scoreFunction, int maxDepth) {
		// TODO Auto-generated method stub
	}
		
}
