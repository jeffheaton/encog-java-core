package org.encog.ml.genetic;

import java.io.Serializable;

import org.encog.ml.MLEncodable;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;

public class MLEncodableCODEC implements GeneticCODEC, Serializable {

	@Override
	public MLMethod decode(Genome genome) {
		MLMethodGenome genome2 = (MLMethodGenome)genome;
		genome2.decode();
		return genome2.getPhenotype();
	}

	@Override
	public Genome encode(MLMethod phenotype) {
		MLEncodable phenotype2 = (MLEncodable)phenotype;
		return new MLMethodGenome(phenotype2);
	}

}
