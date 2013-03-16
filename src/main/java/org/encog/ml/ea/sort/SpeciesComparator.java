package org.encog.ml.ea.sort;

import java.util.Comparator;

import org.encog.ml.ea.species.Species;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

public class SpeciesComparator implements Comparator<Species> {

	private EvolutionaryAlgorithm training;
	
	public SpeciesComparator(EvolutionaryAlgorithm theTraining) {
		this.training = theTraining;
	}
	
	@Override
	public int compare(Species sp1, Species sp2) {
		return training.getBestComparator().compare(sp1.getLeader(), sp2.getLeader());
	}

}
