package org.encog.ml.genetic;

import java.util.Random;

import org.encog.ml.MLEncodable;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.score.CalculateGenomeScore;

public class MLMethodGenomeFactory implements GenomeFactory {

	private MethodFactory factory;
	
	public MLMethodGenomeFactory(MethodFactory theFactory) {
		this.factory = theFactory;
	}
	
	@Override
	public Genome factor() {
		// TODO Auto-generated method stub
		return new MLMethodGenome((MLEncodable)this.factory.factor());
	}
}
