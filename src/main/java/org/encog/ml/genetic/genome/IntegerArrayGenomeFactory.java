package org.encog.ml.genetic.genome;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;

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
	public Genome factor(Genome other) {
		return new IntegerArrayGenome( ((IntegerArrayGenome)other));
	}
}
