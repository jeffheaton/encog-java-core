package org.encog.ml.genetic.genome;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;

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
	public Genome factor(Genome other) {
		// TODO Auto-generated method stub
		return new DoubleArrayGenome((DoubleArrayGenome)other);
	}	
}
