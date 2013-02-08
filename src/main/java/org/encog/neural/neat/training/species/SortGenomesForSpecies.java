package org.encog.neural.neat.training.species;

import java.util.Comparator;

import org.encog.Encog;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class SortGenomesForSpecies implements Comparator<NEATGenome> {

	private NEATTraining train;
	
	public SortGenomesForSpecies(NEATTraining theTrain) {
		this.train = theTrain;
	}
	
	@Override
	public int compare(NEATGenome g1, NEATGenome g2) {
		if( Math.abs(g1.getAdjustedScore()-g2.getAdjustedScore())<Encog.DEFAULT_DOUBLE_EQUAL ) {
			return g2.getBirthGeneration()-g1.getBirthGeneration();
		} else {
			return train.getSelectionComparator().compare(g1, g2);
		}
	}

}
