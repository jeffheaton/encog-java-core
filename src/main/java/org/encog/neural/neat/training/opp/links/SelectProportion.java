package org.encog.neural.neat.training.opp.links;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public class SelectProportion implements SelectLinks {
	
	private double proportion;
	private NEATTraining trainer;
	
	public SelectProportion(double theProportion) {
		this.proportion = theProportion;
	}
	
	@Override
	public void init(NEATTraining theTrainer) {
		this.trainer = theTrainer;
	}
	
	@Override
	public List<NEATLinkGene> selectLinks(Random rnd, NEATGenome genome) {
		List<NEATLinkGene> result = new ArrayList<NEATLinkGene>();
		
		boolean mutated = false;
		
		for (final NEATLinkGene linkGene : genome.getLinksChromosome()) {
			if (rnd.nextDouble() < this.proportion) {
				mutated = true;
				result.add(linkGene);	
			}
		}
		
		if( !mutated ) {
			int idx = rnd.nextInt(genome.getLinksChromosome().size());
			NEATLinkGene linkGene  = genome.getLinksChromosome().get(idx);
			result.add(linkGene);	
		}
		
		return result;
	}

	/**
	 * @return the trainer
	 */
	@Override
	public NEATTraining getTrainer() {
		return trainer;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":proportion=");
		result.append(this.proportion);
		result.append("]");
		return result.toString();
	}
	
	
}
