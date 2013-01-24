package org.encog.neural.neat.training.opp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATGenomeFactory;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.NEATTraining;

/**
 * Crossover is performed by mixing the link genes between the parents to produce an offspring.  Only the 
 * link genes are considered for crossover.  The neuron genes are chosen by virtue of which link genes were chosen.  
 * If a neuron gene is present in both parents, then we choose the neuron gene from the more fit of the two parents.
 * 
 * For NEAT, it does not really matter what parent we get the neuron gene from.  However, because HyperNEAT also 
 * encodes a unique activation function into the neuron, the selection of a neuron gene between two parents is more 
 * important.
 * 
 * The crossover operator defines two broad classes of genes.  Matching genes are those genes that are present in 
 * both parents.  Non-matching genes are only present in one person.  Non-matching genes are further divided into 
 * two more groups:
 * 
 * disjoint genes: Genes in the middle of a genome that do not match between the parents.
 * excess genes: Genes at the edge of a genome that do not match between the parents.
 * 
 * Matching genes are inherited randomly, whereas disjoint genes (those that do not match in the middle) and excess genes 
 * (those that do not match in the end) are inherited from the more fit parent. In this case, equal fitnesses are 
 * assumed, so the disjoint and excess genes are also inherited randomly. The disabled genes may become enabled again 
 * in future generations: there’s a preset chance that an inherited gene is disabled if it is disabled in either parent.
 * 
 * This is implemented in this class via the following algorithm.  First, create a counter for each parent.  At each step in 
 * the loop, perform the following.
 * 
 * If both parents have the same innovation number, then randomly choose which parent's gene to use. Increase the parent 
 * counter who contributed the gene.
 * Else if one parent has a lower innovation number than the other, then include the lower innovation gene if its parent 
 * is the most fit. Increase the parent counter who contributed the gene.

 *
 */
public class NEATCrossover implements EvolutionaryOperator {
	
	/**
	 * Init this operator.  This allows the EA to be defined.
	 */
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = (NEATTraining)theOwner;
	}
	
	/**
	 * The owning object.
	 */
	private NEATTraining owner;
	
	/**
	 * Choose a parent to favor.
	 * @param mom The mother.
	 * @param dad The father.
	 * @return The parent to favor.
	 */
	private NEATGenome favorParent(final NEATGenome mom, final NEATGenome dad) {
		
		// first determine who is more fit, the mother or the father?
		// see if mom and dad are the same fitness
		if (mom.getScore() == dad.getScore()) {
			// are mom and dad the same fitness
			if (mom.getNumGenes() == dad.getNumGenes()) {
				// if mom and dad are the same fitness and have the same number of genes, 
				// then randomly pick mom or dad as the most fit.
				if (Math.random() > 0) {
					return mom;
				} else {
					return dad;
				}
			}
			// mom and dad are the same fitness, but different number of genes
			// favor the parent with fewer genes
			else {
				if (mom.getNumGenes() < dad.getNumGenes()) {
					return mom;
				} else {
					return dad;
				}
			}
		} else {
			// mom and dad have different scores, so choose the better score.
			// important to note, better score COULD BE the larger or smaller score.
			if (owner.getSelectionComparator().compare(mom, dad)<0) {
				return mom;
			}

			else {
				return dad;
			}
		}
		
	}
	
	/**
	 * Find the best neuron, between two parents by the specified neuron id.
	 * @param nodeID The neuron id.
	 * @param best The best genome.
	 * @param notBest The non-best (second best) genome. Also the worst, since 
	 * this is the 2nd best of 2.
	 * @return The best neuron genome by id.
	 */
	private NEATNeuronGene findBestNeuron(long nodeID, NEATGenome best, NEATGenome notBest) {
		NEATNeuronGene result = best.findNeuron(nodeID);
		if( result==null ) {
			result = notBest.findNeuron(nodeID);
		}
		return result;
	}
	
	/**
	 * Add a neuron.
	 * 
	 * @param nodeID
	 *            The neuron id.
	 * @param vec
	 *            THe list of id's used.
	 */
	public void addNeuronID(final long nodeID, final List<NEATNeuronGene> vec, NEATGenome best, NEATGenome notBest) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i).getId() == nodeID) {
				return;
			}
		}

		vec.add(findBestNeuron(nodeID, best, notBest));

		return;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		
		NEATGenome mom = (NEATGenome)parents[parentIndex+0];
		NEATGenome dad = (NEATGenome)parents[parentIndex+1];
		
		NEATGenome best = favorParent(mom,dad);
		NEATGenome notBest = (best==mom)?mom:dad;
		
		final List<NEATNeuronGene> babyNeurons = new ArrayList<NEATNeuronGene>();
		final List<NEATLinkGene> babyGenes = new ArrayList<NEATLinkGene>();

		final List<NEATNeuronGene> vecNeurons = new ArrayList<NEATNeuronGene>();

		int curMom = 0; // current gene index from mom
		int curDad = 0; // current gene index from dad
		NEATLinkGene selectedGene = null;
		
		// add in the input and bias, they should always be here
		int alwaysCount = owner.getInputCount() + owner.getOutputCount() + 1;
		for(int i=0;i<alwaysCount;i++) {
			addNeuronID(i, vecNeurons, best, notBest);
		}
		

		while ((curMom < mom.getNumGenes()) || (curDad < dad.getNumGenes())) {
			NEATLinkGene momGene = null; // the mom gene object
			NEATLinkGene dadGene = null; // the dad gene object
			

			// grab the actual objects from mom and dad for the specified indexes
			// if there are none, then null
			if (curMom < mom.getNumGenes()) {
				momGene = (NEATLinkGene) mom.getLinksChromosome().get(curMom);
			} 

			if (curDad < dad.getNumGenes()) {
				dadGene = (NEATLinkGene) dad.getLinksChromosome().get(curDad);
			} 

			// now select a gene for mom or dad.  This gene is for the baby
			if ((momGene == null) && (dadGene != null)) {
				if (best == dad) {
					selectedGene = dadGene;
				}
				curDad++;
			} else if ((dadGene == null) && (momGene != null)) {
				if (best == mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (momGene.getInnovationId() < dadGene.getInnovationId()) {
				if (best == mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (dadGene.getInnovationId() < momGene.getInnovationId()) {
				if (best == dad) {
					selectedGene = dadGene;
				}
				curDad++;
			} else if (dadGene.getInnovationId() == momGene.getInnovationId()) {
				if (Math.random() < 0.5f) {
					selectedGene = momGene;
				}

				else {
					selectedGene = dadGene;
				}
				curMom++;
				curDad++;
			}

			if( selectedGene!=null ) {
				if (babyGenes.size() == 0) {
					babyGenes.add(selectedGene);
				} else {
					if (((NEATLinkGene) babyGenes.get(babyGenes.size() - 1))
							.getInnovationId() != selectedGene.getInnovationId()) {
						babyGenes.add(selectedGene);
					}
				}
	
				// Check if we already have the nodes referred to in SelectedGene.
				// If not, they need to be added.
				addNeuronID(selectedGene.getFromNeuronID(), vecNeurons, best, notBest);
				addNeuronID(selectedGene.getToNeuronID(), vecNeurons, best, notBest);
			}

		}// end while

		// now create the required nodes. First sort them into order
		Collections.sort(vecNeurons);

		for (int i = 0; i < vecNeurons.size(); i++) {
			babyNeurons.add(owner.getInnovations().createNeuronFromID(
					vecNeurons.get(i).getId(),vecNeurons.get(i).getActivationFunction()));
		}

		// finally, create the genome
		NEATGenomeFactory factory = (NEATGenomeFactory)this.owner.getPopulation().getGenomeFactory();
		final NEATGenome babyGenome = factory.factor(owner.getNEATPopulation()
				.assignGenomeID(), babyNeurons, babyGenes, mom.getInputCount(),
				mom.getOutputCount());
		babyGenome.setPopulation(owner.getPopulation());
		
		offspring[offspringIndex] = babyGenome;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int offspringProduced() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int parentsNeeded() {
		return 2;
	}

}
