/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.neat.training.species;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.genetic.GeneticError;
import org.encog.neural.neat.NEATSpecies;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATTraining;

/**
 * The original NEAT Speciation Strategy. This is currently the only speciation
 * strategy implemented by Encog. There are other speciation strategies that
 * have been proposed (and implemented) for NEAT. One example is k-means.
 * 
 * NEAT starts up by creating an initial population of genomes with randomly
 * generated connections between input and output nodes. Not every input neuron
 * is necessarily connected, this allows NEAT to determine which input neurons
 * to use. Once the population has been generated it is speciated by iterating
 * over this population of genomes. The first genome is placed in its own
 * species.
 * 
 * The second genome is then compared to the first genome. If the compatibility
 * is below the threshold then the genome is placed into the same species as the
 * first. If not, the second genome founds a new species of its own. The
 * remaining genomes follow this same process.
 * 
 * 
 */
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

	/**
	 * The NEAT training being used.
	 */
	private NEATTraining owner;

	/**
	 * THe minimum compatibility that two genes must have to be in the same
	 * species.
	 */
	private double compatibilityThreshold = 1.0;

	/**
	 * The maximum number of generations allows with no improvement. After this
	 * the genomes in this species are not allowed to reproduce or continue.
	 * This does not apply to top species.
	 */
	private int numGensAllowedNoImprovement = 15;

	/**
	 * The maximum number of species. This is just a target. If the number of
	 * species goes over this number then the compatibilityThreshold is
	 * increased to decrease the number of species.
	 * 
	 */
	private int maxNumberOfSpecies = 40;

	/**
	 * The method used to sort the genomes in the species. More desirable
	 * genomes should come first for later selection.
	 */
	private SortGenomesForSpecies sortGenomes;

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

		if (this.owner.getSelectionComparator().compare(genome,
				species.getLeader()) < 0) {
			species.setBestScore(genome.getScore());
			species.setGensNoImprovement(0);
			species.setLeader(genome);
		}

		species.getMembers().add(genome);

	}

	/**
	 * Adjust the species compatibility threshold. This prevents us from having
	 * too many species. Dynamically increase or decrease the
	 * compatibilityThreshold.
	 */
	private void adjustCompatibilityThreshold() {

		// has this been disabled (unlimited species)
		if (this.maxNumberOfSpecies < 1) {
			return;
		}

		final double thresholdIncrement = 0.01;

		if (this.owner.getNEATPopulation().getSpecies().size() > this.maxNumberOfSpecies) {
			this.compatibilityThreshold += thresholdIncrement;
		}

		else if (this.owner.getNEATPopulation().getSpecies().size() < 2) {
			this.compatibilityThreshold -= thresholdIncrement;
		}
	}

	/**
	 * Divide up the potential offspring by the most fit species. To do this we
	 * look at the total species score, vs each individual species percent
	 * contribution to that score.
	 * 
	 * @param speciesCollection
	 *            The current species list.
	 * @param totalSpeciesScore
	 *            The total score over all species.
	 */
	private void divideByFittestSpecies(
			final List<NEATSpecies> speciesCollection,
			final double totalSpeciesScore) {
		NEATSpecies bestSpecies = null;

		// determine the best species.
		if (this.owner.getBestGenome() != null) {
			bestSpecies = this.owner.getBestGenome().getSpecies();
		}

		// loop over all species and calculate its share
		final Object[] speciesArray = speciesCollection.toArray();
		for (final Object element : speciesArray) {
			final NEATSpecies species = (NEATSpecies) element;
			// calculate the species share based on the percent of the total
			// species score
			int share = (int) Math
					.round((species.getOffspringShare() / totalSpeciesScore)
							* this.owner.getPopulation().getPopulationSize());

			// do not give the best species a zero-share
			if ((species == bestSpecies) && (share == 0)) {
				share = 1;
			}

			// if the share is zero, then remove the species
			if ((species.getMembers().size() == 0) || (share == 0)) {
				speciesCollection.remove(species);
			}
			// if the species has not improved over the specified number of
			// generations, then remove it.
			else if ((species.getGensNoImprovement() > this.numGensAllowedNoImprovement)
					&& (species != bestSpecies)) {
				speciesCollection.remove(species);
			} else {
				// otherwise assign a share and sort the members.
				species.setOffspringCount(share);
				Collections.sort(species.getMembers(), this.sortGenomes);
			}
		}
	}

	/**
	 * If no species has a good score then divide the potential offspring amount
	 * all species evenly.
	 * 
	 * @param speciesCollection
	 *            The current set of species.
	 */
	private void divideEven(final List<NEATSpecies> speciesCollection) {
		final double ratio = 1.0 / speciesCollection.size();
		for (final NEATSpecies species : speciesCollection) {
			final int share = (int) Math.round(ratio
					* this.owner.getPopulation().getPopulationSize());
			species.setOffspringCount(share);
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
	private double getCompatibilityScore(final NEATGenome genome1,
			final NEATGenome genome2) {
		double numDisjoint = 0;
		double numExcess = 0;
		double numMatched = 0;
		double weightDifference = 0;

		final int genome1Size = genome1.getLinksChromosome().size();
		final int genome2Size = genome2.getLinksChromosome().size();
		final int n = 1;// Math.max(genome1Size, genome2Size);

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
	 * @return the compatibilityThreshold
	 */
	public double getCompatibilityThreshold() {
		return this.compatibilityThreshold;
	}

	/**
	 * @return the constDisjoint
	 */
	public double getConstDisjoint() {
		return this.constDisjoint;
	}

	/**
	 * @return the constExcess
	 */
	public double getConstExcess() {
		return this.constExcess;
	}

	/**
	 * @return the constMatched
	 */
	public double getConstMatched() {
		return this.constMatched;
	}

	/**
	 * @return the maxNumberOfSpecies
	 */
	public int getMaxNumberOfSpecies() {
		return this.maxNumberOfSpecies;
	}

	/**
	 * @return the numGensAllowedNoImprovement
	 */
	public int getNumGensAllowedNoImprovement() {
		return this.numGensAllowedNoImprovement;
	}

	/**
	 * @return the owner
	 */
	public NEATTraining getOwner() {
		return this.owner;
	}

	/**
	 * @return the sortGenomes
	 */
	public SortGenomesForSpecies getSortGenomes() {
		return this.sortGenomes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final EvolutionaryAlgorithm theOwner) {
		this.owner = (NEATTraining) theOwner;
		this.sortGenomes = new SortGenomesForSpecies(this.owner);
	}

	/**
	 * Level off all of the species shares so that they add up to the desired
	 * population size. If they do not add up to the desired species size, this
	 * was a result of rounding the floating point share amounts to integers.
	 */
	private void levelOff() {
		int total = 0;
		final List<NEATSpecies> list = this.owner.getNEATPopulation()
				.getSpecies();
		Collections.sort(list, new SpeciesComparator(this.owner));

		// best species gets at least one offspring
		if (list.get(0).getOffspringCount() == 0) {
			list.get(0).setOffspringCount(1);
		}

		// total up offspring
		for (final NEATSpecies species : list) {
			total += species.getOffspringCount();
		}

		// how does the total offspring count match the target
		int diff = this.owner.getNEATPopulation().getPopulationSize() - total;

		if (diff < 0) {
			// need less offspring
			int index = list.size() - 1;
			while ((diff != 0) && (index > 0)) {
				final NEATSpecies species = list.get(index);
				final int t = Math.min(species.getOffspringCount(),
						Math.abs(diff));
				species.setOffspringCount(species.getOffspringCount() - t);
				if (species.getOffspringCount() == 0) {
					list.remove(index);
				}
				diff += t;
				index--;
			}
		} else {
			// need more offspring
			list.get(0).setOffspringCount(
					list.get(0).getOffspringCount() + diff);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performSpeciation() {
		final List<NEATGenome> genomes = resetSpecies();
		speciateAndCalculateSpawnLevels(genomes);
	}

	/**
	 * Reset for an iteration.
	 * 
	 * @return
	 */
	private List<NEATGenome> resetSpecies() {
		final List<NEATGenome> result = new ArrayList<NEATGenome>();
		final Object[] speciesArray = this.owner.getNEATPopulation()
				.getSpecies().toArray();

		// Add the NEAT genomes
		for (final Genome genome : this.owner.getNEATPopulation().getGenomes()) {
			result.add((NEATGenome) genome);
		}

		for (final Object element : speciesArray) {
			final NEATSpecies s = (NEATSpecies) element;
			s.purge();

			// did the leader die? If so, disband the species. (but don't kill
			// the genomes)
			if (!this.owner.getPopulation().getGenomes()
					.contains(s.getLeader())) {
				this.owner.getNEATPopulation().getSpecies().remove(s);
			} else if ((s.getGensNoImprovement() > this.numGensAllowedNoImprovement)
					&& this.owner.getSelectionComparator().isBetterThan(
							this.owner.getError(), s.getBestScore())) {
				this.owner.getNEATPopulation().getSpecies().remove(s);
			}

			// remove the leader from the list we return. the leader already has
			// a species
			result.remove(s.getLeader());
		}

		return result;
	}

	/**
	 * @param compatibilityThreshold
	 *            the compatibilityThreshold to set
	 */
	public void setCompatibilityThreshold(final double compatibilityThreshold) {
		this.compatibilityThreshold = compatibilityThreshold;
	}

	/**
	 * @param constDisjoint
	 *            the constDisjoint to set
	 */
	public void setConstDisjoint(final double constDisjoint) {
		this.constDisjoint = constDisjoint;
	}

	/**
	 * @param constExcess
	 *            the constExcess to set
	 */
	public void setConstExcess(final double constExcess) {
		this.constExcess = constExcess;
	}

	/**
	 * @param constMatched
	 *            the constMatched to set
	 */
	public void setConstMatched(final double constMatched) {
		this.constMatched = constMatched;
	}

	/**
	 * @param maxNumberOfSpecies
	 *            the maxNumberOfSpecies to set
	 */
	public void setMaxNumberOfSpecies(final int maxNumberOfSpecies) {
		this.maxNumberOfSpecies = maxNumberOfSpecies;
	}

	/**
	 * @param numGensAllowedNoImprovement
	 *            the numGensAllowedNoImprovement to set
	 */
	public void setNumGensAllowedNoImprovement(
			final int numGensAllowedNoImprovement) {
		this.numGensAllowedNoImprovement = numGensAllowedNoImprovement;
	}

	/**
	 * @param sortGenomes
	 *            the sortGenomes to set
	 */
	public void setSortGenomes(final SortGenomesForSpecies sortGenomes) {
		this.sortGenomes = sortGenomes;
	}

	/**
	 * Determine the species.
	 * 
	 * @param genomes
	 *            The genomes to speciate.
	 */
	private void speciateAndCalculateSpawnLevels(final List<NEATGenome> genomes) {
		double maxScore = 0;

		final List<NEATSpecies> speciesCollection = this.owner
				.getNEATPopulation().getSpecies();

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
				currentSpecies = new NEATSpecies(
						this.owner.getNEATPopulation(), genome, this.owner
								.getNEATPopulation().assignSpeciesID());
				this.owner.getNEATPopulation().getSpecies().add(currentSpecies);
			}
		}

		//
		double totalSpeciesScore = 0;
		for (final NEATSpecies species : speciesCollection) {
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

}
