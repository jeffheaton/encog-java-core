package org.encog.ml.genetic;

import org.encog.ml.MLEncodable;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;

public class MLMethodGenomeFactory implements GenomeFactory {

	private MethodFactory factory;
	
	public MLMethodGenomeFactory(MethodFactory theFactory) {
		this.factory = theFactory;
	}
	
	@Override
	public Genome factor() {
		return new MLMethodGenome((MLEncodable)this.factory.factor());
	}

	@Override
	public Genome factor(Genome other) {
		MLMethodGenome result = (MLMethodGenome)factor();
		result.copy((MLMethodGenome)other);
		return result;
	}
}
