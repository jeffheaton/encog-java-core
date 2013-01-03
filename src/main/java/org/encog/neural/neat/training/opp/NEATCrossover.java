package org.encog.neural.neat.training.opp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.neural.neat.training.NEATTraining;

public class NEATCrossover implements EvolutionaryOperator {
	
	/**
	 * The two parents.
	 */
	enum NEATParent {
		/**
		 * The father.
		 */
		Dad,

		/**
		 * The mother.
		 */
		Mom
	};
	
	@Override
	public void init(EvolutionaryAlgorithm theOwner) {
		this.owner = (NEATTraining)theOwner;
	}
	
	private NEATTraining owner;
	
	/**
	 * Choose a parent to favor.
	 * @param mom The mother.
	 * @param dad The father.
	 * @return The parent to favor.
	 */
	private NEATParent favorParent(final NEATGenome mom, final NEATGenome dad) {
		
		// first determine who is more fit, the mother or the father?
		// see if mom and dad are the same fitness
		if (mom.getScore() == dad.getScore()) {
			// are mom and dad the same fitness
			if (mom.getNumGenes() == dad.getNumGenes()) {
				// if mom and dad are the same fitness and have the same number of genes, 
				// then randomly pick mom or dad as the most fit.
				if (Math.random() > 0) {
					return NEATParent.Mom;
				} else {
					return NEATParent.Dad;
				}
			}
			// mom and dad are the same fitness, but different number of genes
			// favor the parent with fewer genes
			else {
				if (mom.getNumGenes() < dad.getNumGenes()) {
					return NEATParent.Mom;
				} else {
					return NEATParent.Dad;
				}
			}
		} else {
			// mom and dad have different scores, so choose the better score.
			// important to note, better score COULD BE the larger or smaller score.
			if (owner.getSelectionComparator().compare(mom, dad)<0) {
				return NEATParent.Mom;
			}

			else {
				return NEATParent.Dad;
			}
		}
		
	}
	
	/**
	 * Add a neuron.
	 * 
	 * @param nodeID
	 *            The neuron id.
	 * @param vec
	 *            THe list of id's used.
	 */
	public void addNeuronID(final long nodeID, final List<Long> vec) {
		for (int i = 0; i < vec.size(); i++) {
			if (vec.get(i) == nodeID) {
				return;
			}
		}

		vec.add(nodeID);

		return;
	}



	@Override
	public void performOperation(Random rnd, Genome[] parents, int parentIndex,
			Genome[] offspring, int offspringIndex) {
		
		NEATGenome mom = (NEATGenome)parents[parentIndex+0];
		NEATGenome dad = (NEATGenome)parents[parentIndex+1];
		
		NEATParent best = favorParent(mom,dad);
		
		final List<NEATNeuronGene> babyNeurons = new ArrayList<NEATNeuronGene>();
		final List<NEATLinkGene> babyGenes = new ArrayList<NEATLinkGene>();

		final List<Long> vecNeurons = new ArrayList<Long>();

		int curMom = 0; // current gene index from mom
		int curDad = 0; // current gene index from dad
		NEATLinkGene selectedGene = null;
		
		// add in the input and bias, they should always be here
		int alwaysCount = owner.getInputCount() + owner.getOutputCount() + 1;
		for(int i=0;i<alwaysCount;i++) {
			addNeuronID(i, vecNeurons);
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
				if (best == NEATParent.Dad) {
					selectedGene = dadGene;
				}
				curDad++;
			} else if ((dadGene == null) && (momGene != null)) {
				if (best == NEATParent.Mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (momGene.getInnovationId() < dadGene.getInnovationId()) {
				if (best == NEATParent.Mom) {
					selectedGene = momGene;
				}
				curMom++;
			} else if (dadGene.getInnovationId() < momGene.getInnovationId()) {
				if (best == NEATParent.Dad) {
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
				addNeuronID(selectedGene.getFromNeuronID(), vecNeurons);
				addNeuronID(selectedGene.getToNeuronID(), vecNeurons);
			}

		}// end while

		// now create the required nodes. First sort them into order
		Collections.sort(vecNeurons);

		for (int i = 0; i < vecNeurons.size(); i++) {
			babyNeurons.add(owner.getInnovations().createNeuronFromID(
					vecNeurons.get(i)));
		}

		// finally, create the genome
		final NEATGenome babyGenome = new NEATGenome(owner.getPopulation()
				.assignGenomeID(), babyNeurons, babyGenes, mom.getInputCount(),
				mom.getOutputCount());
		babyGenome.setPopulation(owner.getPopulation());
		
		offspring[offspringIndex] = babyGenome;
	}

	@Override
	public int offspringProduced() {
		return 1;
	}

	@Override
	public int parentsNeeded() {
		return 2;
	}

}
