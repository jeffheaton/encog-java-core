package org.encog.neural.neat.training.species;

import java.util.Collections;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class SimpleNEATSpeciation implements Speciation {
	
	private NEATTraining owner;
	private double compatibilityThreshold = 0.26;
	private int numGensAllowedNoImprovement = 15;
	private int maxNumberOfSpecies = 40;
	
	/**
	 * The total fit adjustment.
	 */
	private double totalFitAdjustment;

	/**
	 * The average fit adjustment.
	 */
	private double averageFitAdjustment;
	
	@Override
	public void init(NEATTraining theOwner) {
		this.owner = theOwner;
	}
	
	@Override
	public void performSpeciation() {
		resetSpecies();
		speciateAndCalculateSpawnLevels();
	}
	
	
	/**
	 * Determine the species.
	 */
	private void speciateAndCalculateSpawnLevels() {

		// calculate compatibility between genomes and species
		adjustCompatibilityThreshold();

		// assign genomes to species (if any exist)
		for (final Genome g : owner.getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			boolean added = false;

			for (final NEATSpecies s : owner.getNEATPopulation().getSpecies()) {
				final double compatibility = getCompatibilityScore(genome, s.getLeader());

				if (compatibility <= this.compatibilityThreshold) {
					addSpeciesMember(s, genome);
					genome.setSpeciesID(s.getSpeciesID());
					added = true;
					break;
				}
			}

			// if this genome did not fall into any existing species, create a
			// new species
			if (!added) {
				owner.getNEATPopulation().getSpecies().add(
						new NEATSpecies(owner.getNEATPopulation(), genome,
								owner.getNEATPopulation().assignSpeciesID()));
			}
		}

		adjustSpeciesScore();

		for (final Genome g : owner.getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			this.totalFitAdjustment += genome.getAdjustedScore();
		}

		this.averageFitAdjustment = this.totalFitAdjustment
				/ owner.getPopulation().size();

		for (final Genome g : owner.getPopulation().getGenomes()) {
			final NEATGenome genome = (NEATGenome) g;
			final double toSpawn = genome.getAdjustedScore()
					/ this.averageFitAdjustment;
			genome.setAmountToSpawn(toSpawn);
		}

		for (final NEATSpecies species : owner.getNEATPopulation().getSpecies()) {
			species.calculateSpawnAmount();
			Collections.sort(species.getMembers(), this.owner.getBestComparator());
		}
	}
	
	/**
	 * Reset for an iteration.
	 */
	private void resetSpecies() {
		this.totalFitAdjustment = 0;
		this.averageFitAdjustment = 0;

		final Object[] speciesArray = owner.getNEATPopulation().getSpecies().toArray();

		for (final Object element : speciesArray) {
			final NEATSpecies s = (NEATSpecies) element;
			s.purge();

			// did the leader die?  If so, disband the species.
			if( !owner.getPopulation().getGenomes().contains(s.getLeader())) {
				owner.getNEATPopulation().getSpecies().remove(s);
			}
			else if ((s.getGensNoImprovement() > this.numGensAllowedNoImprovement)
					&& owner.getSelectionComparator().isBetterThan(this.owner.getError(),
							s.getBestScore())) {
				owner.getNEATPopulation().getSpecies().remove(s);
			}
		}
	}
	
	/**
	 * Adjust each species score.
	 */
	private void adjustSpeciesScore() {
		Object[] a = owner.getNEATPopulation().getSpecies().toArray();
		
		for(int i=0;i<a.length;i++) {
			NEATSpecies s = (NEATSpecies)a[i];
			
			// is species now empty
			if( s.getMembers().size()<1 ) {
				owner.getNEATPopulation().getSpecies().remove(s);
			}

			// loop over all genomes and adjust scores as needed
			for (final Genome member : s.getMembers()) {
				double score = member.getScore();

				// apply a youth bonus
				if (s.getAge() < owner.getNEATPopulation().getYoungBonusAgeThreshold()) {
					score = owner.getSelectionComparator().applyBonus(score,
							owner.getNEATPopulation().getYoungScoreBonus());
				}

				// apply an old age penalty
				if (s.getAge() > owner.getNEATPopulation().getOldAgeThreshold()) {
					score = owner.getSelectionComparator().applyPenalty(score,
							owner.getNEATPopulation().getOldAgePenalty());
				}

				final double adjustedScore = score / s.getMembers().size();

				member.setAdjustedScore(adjustedScore);

			}
		}
	}
	
	/**
	 * Adjust the species compatibility threshold. This prevents us from having
	 * too many species.
	 */
	private void adjustCompatibilityThreshold() {

		// has this been disabled (unlimited species)
		if (this.maxNumberOfSpecies < 1) {
			return;
		}

		final double thresholdIncrement = 0.01;

		if (owner.getNEATPopulation().getSpecies().size() > this.maxNumberOfSpecies) {
			this.compatibilityThreshold += thresholdIncrement;
		}

		else if (owner.getNEATPopulation().getSpecies().size() < 2) {
			this.compatibilityThreshold -= thresholdIncrement;
		}
	}
	
	/**
	 * Get the compatibility score with another genome. Used to determine
	 * species.
	 * 
	 * @param genome
	 *            The other genome.
	 * @return The score.
	 */
	private double getCompatibilityScore(final NEATGenome genome1, final NEATGenome genome) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int genome1Size = genome1.getLinksChromosome().size();
		int g1 = 0;
		int g2 = 0;

		while ((g1 < genome1Size )
				|| (g2 < genome1Size )) {

			if (g1 == genome1Size ) {
				g2++;
				numExcess++;

				continue;
			}

			if (g2 == genome.getLinksChromosome().size() ) {
				g1++;
				numExcess++;

				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = genome1.getLinksChromosome().get(g1).getInnovationId();
			final long id2 = genome.getLinksChromosome().get(g2).getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {

				// get the weight difference between these two genes
				weightDifference += Math
						.abs(genome1.getLinksChromosome().get(g1).getWeight()
								- genome.getLinksChromosome()
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

		int longest = this.owner.getMaxGeneLength();

		final double score = (NEATGenome.TWEAK_EXCESS * numExcess / longest)
				+ (NEATGenome.TWEAK_DISJOINT * numDisjoint / longest)
				+ (NEATGenome.TWEAK_MATCHED * weightDifference / numMatched);

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

}
