package org.encog.neural.neat.training.species;

import java.util.Comparator;

import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATTraining;

public class SpeciesComparator implements Comparator<NEATSpecies> {

	private NEATTraining training;
	
	public SpeciesComparator(NEATTraining theTraining) {
		this.training = theTraining;
	}
	
	@Override
	public int compare(NEATSpecies sp1, NEATSpecies sp2) {
		return training.getBestComparator().compare(sp1.getLeader(), sp2.getLeader());
	}

}
