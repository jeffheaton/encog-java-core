package org.encog.ml.genetic;

import org.encog.ml.MLEncodable;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;

public class MLMethodGenomeFactory implements GenomeFactory {

	private MethodFactory factory;
	private Population population;
	
	public MLMethodGenomeFactory(MethodFactory theFactory, Population thePopulation) {
		this.factory = theFactory;
		this.population = thePopulation;
	}
	
	@Override
	public Genome factor() {
		Genome result = new MLMethodGenome((MLEncodable)this.factory.factor());
		result.setPopulation(this.population);
		return result;
	}

	@Override
	public Genome factor(Genome other) {
		MLMethodGenome result = (MLMethodGenome)factor();
		result.copy((MLMethodGenome)other);
		result.setPopulation(this.population);
		return result;
	}
}
