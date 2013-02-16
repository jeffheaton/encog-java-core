package org.encog.neural.neat.training.species;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

public class OriginalNEATSpeciation implements Speciation {

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
	private double compatibilityThreshold = 1.0;
	private int numGensAllowedNoImprovement = 15;
	private int maxNumberOfSpecies = 40;
	private SortGenomesForSpecies sortGenomes;

	@Override
	public void init(NEATTraining theOwner) {
		this.owner = theOwner;
		this.sortGenomes = new SortGenomesForSpecies(theOwner);
	}

	@Override
	public void performSpeciation() {
		List<NEATGenome> genomes = resetSpecies();
		speciateAndCalculateSpawnLevels(genomes);
	}

	/**
	 * Determine the species.
	 * 
	 * @param genomes
	 */
	private void speciateAndCalculateSpawnLevels(List<NEATGenome> genomes) {
		double maxScore = 0;

		List<NEATSpecies> speciesCollection = this.owner.getNEATPopulation()
				.getSpecies();

		// calculate compatibility between genomes and species
		adjustCompatibilityThreshold();

		// assign genomes to species (if any exist)
		for (final Genome g : genomes) {
			NEATSpecies currentSpecies = null;
			final NEATGenome genome = (NEATGenome) g;

			if (!Double.isNaN(genome.getScore())
					&& !Double.isInfinite(genome.getScore())) {
				maxScore = Math.max(genome.getScore(), maxScore);
			}

			for (final NEATSpecies s : speciesCollection) {
				final double compatibility = getCompatibilityScore(genome,
						s.getLeader());

				if (compatibility <= this.compatibilityThreshold) {
					currentSpecies = s;
					addSpeciesMember(s, genome);
					genome.setSpecies(s);
					break;
				}
			}

			// if this genome did not fall into any existing species, create a
			// new species
			if (currentSpecies == null) {
				currentSpecies = new NEATSpecies(owner.getNEATPopulation(),
						genome, owner.getNEATPopulation().assignSpeciesID());
				owner.getNEATPopulation().getSpecies().add(currentSpecies);
			}
		}

		//
		double totalSpeciesScore = 0;
		for (NEATSpecies species : speciesCollection) {
			totalSpeciesScore += species.calculateShare(this.owner
					.getScoreFunction().shouldMinimize(), maxScore);
		}

		if (totalSpeciesScore < Encog.DEFAULT_DOUBLE_EQUAL) {
			// This should not happen much, or if it does, only in the
			// beginning.
			// All species scored zero. So they are all equally bad. Just divide
			// up the right to produce offspring evenly.
			divideEven(speciesCollection);
		} else {
			// Divide up the number of offspring produced to the most fit
			// species.
			divideByFittestSpecies(speciesCollection, totalSpeciesScore);
		}
		
		levelOff();

	}

	private void divideEven(List<NEATSpecies> speciesCollection) {
		double ratio = 1.0 / speciesCollection.size();
		for (NEATSpecies species : speciesCollection) {
			int share = (int) Math.round(ratio
					* this.owner.getPopulation().getPopulationSize());
			species.setOffspringCount(share);
		}
	}

	private void divideByFittestSpecies(List<NEATSpecies> speciesCollection,
			double totalSpeciesScore) {
		NEATSpecies bestSpecies = null;

		if (this.owner.getBestGenome() != null) {
			bestSpecies = this.owner.getBestGenome().getSpecies();
		}

		Object[] speciesArray = speciesCollection.toArray();
		for (int i = 0; i < speciesArray.length; i++) {
			NEATSpecies species = (NEATSpecies) speciesArray[i];
			int share = (int) Math
					.round((species.getOffspringShare() / totalSpeciesScore)
							* this.owner.getPopulation().getPopulationSize());

			if (species == bestSpecies && share == 0) {
				share = 1;
			}

			if (species.getMembers().size() == 0 || share == 0) {
				speciesCollection.remove(species);
			} else if ((species.getGensNoImprovement() > this.numGensAllowedNoImprovement)
					&& species != bestSpecies) {
				speciesCollection.remove(species);
			} else {
				species.setOffspringCount(share);
				Collections.sort(species.getMembers(), this.sortGenomes);
			}
		}
	}
	
	private void levelOff() {
		int total = 0;
		List<NEATSpecies> list = this.owner.getNEATPopulation().getSpecies();
		Collections.sort(list, new SpeciesComparator(this.owner));
		
		// best species gets at least one offspring
		if( list.get(0).getOffspringCount()==0 ) {
			list.get(0).setOffspringCount(1);
		}
		
		// total up offspring
		for(NEATSpecies species: list) {
			total+=species.getOffspringCount();
		}
		
		// how does the total offspring count match the target
		int diff = this.owner.getNEATPopulation().getPopulationSize() - total;
		
		
		if( diff<0 ) {
			// need less offspring
			int index = list.size()-1;
			while(diff!=0 && index>0) {
				NEATSpecies species = list.get(index);
				int t = Math.min(species.getOffspringCount(), Math.abs(diff));
				species.setOffspringCount(species.getOffspringCount()-t);
				if( species.getOffspringCount()==0 ) {
					list.remove(index);
				}
				diff+=t;
				index--;
			}
		} else {
			// need more offspring
			list.get(0).setOffspringCount(list.get(0).getOffspringCount()+diff);
		}
	}

	

	/**
	 * Reset for an iteration.
	 * 
	 * @return
	 */
	private List<NEATGenome> resetSpecies() {
		final List<NEATGenome> result = new ArrayList<NEATGenome>();
		final Object[] speciesArray = owner.getNEATPopulation().getSpecies()
				.toArray();

		// Add the NEAT genomes
		for (Genome genome : this.owner.getNEATPopulation().getGenomes()) {
			result.add((NEATGenome) genome);
		}

		for (final Object element : speciesArray) {
			final NEATSpecies s = (NEATSpecies) element;
			s.purge();

			// did the leader die? If so, disband the species. (but don't kill
			// the genomes)
			if (!owner.getPopulation().getGenomes().contains(s.getLeader())) {
				owner.getNEATPopulation().getSpecies().remove(s);
			} else if ((s.getGensNoImprovement() > this.numGensAllowedNoImprovement)
					&& owner.getSelectionComparator().isBetterThan(
							this.owner.getError(), s.getBestScore())) {
				owner.getNEATPopulation().getSpecies().remove(s);
			}

			// remove the leader from the list we return. the leader already has
			// a species
			result.remove(s.getLeader());
		}

		return result;
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
		// System.out.println( this.compatibilityThreshold + ", species count="
		// + this.owner.getNEATPopulation().getSpecies().size());
	}

	/**
	 * Get the compatibility score with another genome. Used to determine
	 * species.
	 * 
	 * @param genome
	 *            The other genome.
	 * @return The score.
	 */
	private double getCompatibilityScore(final NEATGenome genome1,
			final NEATGenome genome2) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		int genome1Size = genome1.getLinksChromosome().size();
		int genome2Size = genome2.getLinksChromosome().size();
		int n = 1;// Math.max(genome1Size, genome2Size);

		int g1 = 0;
		int g2 = 0;

		while ((g1 < genome1Size) || (g2 < genome2Size)) {

			if (g1 == genome1Size) {
				g2++;
				numExcess++;
				continue;
			}

			if (g2 == genome2Size) {
				g1++;
				numExcess++;
				continue;
			}

			// get innovation numbers for each gene at this point
			final long id1 = genome1.getLinksChromosome().get(g1)
					.getInnovationId();
			final long id2 = genome2.getLinksChromosome().get(g2)
					.getInnovationId();

			// innovation numbers are identical so increase the matched score
			if (id1 == id2) {

				// get the weight difference between these two genes
				weightDifference += Math.abs(genome1.getLinksChromosome()
						.get(g1).getWeight()
						- genome2.getLinksChromosome().get(g2).getWeight());
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

		if (this.owner.isValidationMode()) {
			if (species.getMembers().contains(genome)) {
				throw new GeneticError("Species already contains genome: "
						+ genome.toString());
			}
		}

		if (owner.getSelectionComparator().compare(genome, species.getLeader()) < 0) {
			species.setBestScore(genome.getScore());
			species.setGensNoImprovement(0);
			species.setLeader(genome);
		}

		species.getMembers().add(genome);

	}

	/**
	 * @return the constDisjoint
	 */
	public double getConstDisjoint() {
		return constDisjoint;
	}

	/**
	 * @param constDisjoint
	 *            the constDisjoint to set
	 */
	public void setConstDisjoint(double constDisjoint) {
		this.constDisjoint = constDisjoint;
	}

	/**
	 * @return the constExcess
	 */
	public double getConstExcess() {
		return constExcess;
	}

	/**
	 * @param constExcess
	 *            the constExcess to set
	 */
	public void setConstExcess(double constExcess) {
		this.constExcess = constExcess;
	}

	/**
	 * @return the constMatched
	 */
	public double getConstMatched() {
		return constMatched;
	}

	/**
	 * @param constMatched
	 *            the constMatched to set
	 */
	public void setConstMatched(double constMatched) {
		this.constMatched = constMatched;
	}

	/**
	 * @return the compatibilityThreshold
	 */
	public double getCompatibilityThreshold() {
		return compatibilityThreshold;
	}

	/**
	 * @param compatibilityThreshold
	 *            the compatibilityThreshold to set
	 */
	public void setCompatibilityThreshold(double compatibilityThreshold) {
		this.compatibilityThreshold = compatibilityThreshold;
	}

	/**
	 * @return the numGensAllowedNoImprovement
	 */
	public int getNumGensAllowedNoImprovement() {
		return numGensAllowedNoImprovement;
	}

	/**
	 * @param numGensAllowedNoImprovement
	 *            the numGensAllowedNoImprovement to set
	 */
	public void setNumGensAllowedNoImprovement(int numGensAllowedNoImprovement) {
		this.numGensAllowedNoImprovement = numGensAllowedNoImprovement;
	}

	/**
	 * @return the maxNumberOfSpecies
	 */
	public int getMaxNumberOfSpecies() {
		return maxNumberOfSpecies;
	}

	/**
	 * @param maxNumberOfSpecies
	 *            the maxNumberOfSpecies to set
	 */
	public void setMaxNumberOfSpecies(int maxNumberOfSpecies) {
		this.maxNumberOfSpecies = maxNumberOfSpecies;
	}

	/**
	 * @return the sortGenomes
	 */
	public SortGenomesForSpecies getSortGenomes() {
		return sortGenomes;
	}

	/**
	 * @param sortGenomes
	 *            the sortGenomes to set
	 */
	public void setSortGenomes(SortGenomesForSpecies sortGenomes) {
		this.sortGenomes = sortGenomes;
	}

	/**
	 * @return the owner
	 */
	public NEATTraining getOwner() {
		return owner;
	}

	@Override
	public void addChild(NEATGenome genome) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ageSpecies(NEATSpecies species) {
		// TODO Auto-generated method stub

	}

}
