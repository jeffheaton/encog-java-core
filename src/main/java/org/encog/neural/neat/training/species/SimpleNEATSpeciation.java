package org.encog.neural.neat.training.species;

import java.util.List;

import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class SimpleNEATSpeciation implements Speciation {

	/**
	 * The adjustment factor for disjoint genes.
	 */
	private double constDisjoint = 1;

	/**
	 * The adjustment factor for excess genes.
	 */
	private double constExcess = 1;

	/**
	 * The adjustment factor for matched genes.
	 */
	private double constMatched = 0.4;
	
	private NEATTraining owner;
	private int numGensAllowedNoImprovement = 15;
	private int maxSpecies;
	private SortGenomesForSpecies sortGenomes;
	
	public SimpleNEATSpeciation() {
		this(40);
	}
	
	public SimpleNEATSpeciation(int theMaxSpecies) {
		this.maxSpecies = theMaxSpecies;
	}
	
	@Override
	public void init(NEATTraining theOwner) {
		this.owner = theOwner;
		this.sortGenomes = new SortGenomesForSpecies(theOwner);
	}
	
	/**
	 * Get the compatibility score with another genome. Used to determine
	 * species.
	 * 
	 * @param genome
	 *            The other genome.
	 * @return The score.
	 */
	private double getCompatibilityScore(final NEATGenome genome1, final NEATGenome genome2) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int genome1Size = genome1.getLinksChromosome().size();
		int genome2Size = genome2.getLinksChromosome().size();
		int n = Math.max(genome1Size, genome2Size);
		
		int g1 = 0;
		int g2 = 0;

		while ((g1 < genome1Size )
				|| (g2 < genome2Size )) {

			if (g1 == genome1Size ) {
				g2++;
				numExcess++;
				continue;
			}

			if (g2 == genome2Size ) {
				g1++;
				numExcess++;
				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = genome1.getLinksChromosome().get(g1).getInnovationId();
			final long id2 = genome2.getLinksChromosome().get(g2).getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {

				// get the weight difference between these two genes
				weightDifference += Math
						.abs(genome1.getLinksChromosome().get(g1).getWeight()
								- genome2.getLinksChromosome()
										.get(g2).getWeight());
				g1++;
				g2++;
				numMatched++;
			}

			// innovation numbers are different so increment the disjoint score
			if (id1 < id2) {
				numDisjoint++;
				g1++;
			}

			if (id1 > id2) {
				++numDisjoint;
				++g2;
			}

		}

		final double score = ((this.constExcess * numExcess) / n)
				+ ((this.constDisjoint * numDisjoint) / n)
				+ (this.constMatched * (weightDifference / numMatched));

		return score;
	}
	
	/**
	 * Add a genome.
	 * 
	 * @param species
	 *            The species to add.
	 * @param genome
	 *            The genome to add.
	 */
	private void addSpeciesMember(final NEATSpecies species, 
			final NEATGenome genome) {

		if (owner.getSelectionComparator().compare(genome,species.getLeader())<0) {
			species.setBestScore(genome.getScore());
			species.setGensNoImprovement(0);
			species.setLeader(genome);
		}

		species.getMembers().add(genome);

	}

	@Override
	public void addChild(NEATGenome genome) {
		synchronized(this) {
			List<NEATSpecies> speciesList = this.owner.getNEATPopulation().getSpecies();
			if( speciesList.size()<this.maxSpecies ) {
				NEATSpecies currentSpecies = new NEATSpecies(owner.getNEATPopulation(), genome,
						owner.getNEATPopulation().assignSpeciesID());
				
				owner.getNEATPopulation().getSpecies().add(currentSpecies);
			} else {
				NEATSpecies targetSpecies = null;
				double bestCompatibility = Double.POSITIVE_INFINITY;
				
				for (final NEATSpecies s : speciesList) {
					final double compatibility = getCompatibilityScore(genome, s.getLeader());

					if( compatibility<bestCompatibility ) {
						bestCompatibility = compatibility;
						targetSpecies = s;
					}
				}
				
				this.addSpeciesMember(targetSpecies, genome);
			}
		}
		
	}

	@Override
	public void performSpeciation() {
		// nothing to do at this point
		
	}

	@Override
	public void ageSpecies(NEATSpecies species) {
		species.setAge(species.getAge()+1);
		species.setGensNoImprovement(species.getGensNoImprovement()+1);
		species.setOffspringCount(0);
	}
	
}
