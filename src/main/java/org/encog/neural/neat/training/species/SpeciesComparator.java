package org.encog.neural.neat.training.species;

import java.util.Comparator;

import org.encog.ml.ea.species.Species;
import org.encog.neural.neat.training.NEATTraining;

public class SpeciesComparator implements Comparator<Species> {

	private NEATTraining training;
	
	public SpeciesComparator(NEATTraining theTraining) {
		this.training = theTraining;
	}
	
	@Override
	public int compare(Species sp1, Species sp2) {
		return training.getBestComparator().compare(sp1.getLeader(), sp2.getLeader());
	}

}
