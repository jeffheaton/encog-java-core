package org.encog.neural.neat.training.opp.links;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

public class SelectFixed implements SelectLinks {
	
	private int linkCount;
	private NEATTraining trainer;
	
	public SelectFixed(int theLinkCount) {
		this.linkCount = theLinkCount;
	}
	
	
	
	/**
	 * @return the trainer
	 */
	@Override
	public NEATTraining getTrainer() {
		return trainer;
	}



	@Override
	public void init(NEATTraining theTrainer) {
		this.trainer = theTrainer;
	}
	
	@Override
	public List<NEATLinkGene> selectLinks(Random rnd, NEATGenome genome) {
		List<NEATLinkGene> result = new ArrayList<NEATLinkGene>();
		int cnt = Math.min(this.linkCount, genome.getLinksChromosome().size());
		
		while(result.size()<cnt) {
			int idx = rnd.nextInt(genome.getLinksChromosome().size());
			NEATLinkGene link = genome.getLinksChromosome().get(idx);
			if( !result.contains(link)) {
				result.add(link);
			}
		}
		return result;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(":linkCount=");
		result.append(this.linkCount);
		result.append("]");
		return result.toString();
	}
}
