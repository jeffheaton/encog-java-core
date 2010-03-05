package org.encog.solve.genetic.crossover;

import java.util.HashSet;
import java.util.Set;

import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

public class SpliceNoRepeat implements Crossover {
	
	private int cutLength;
	
	public SpliceNoRepeat(int cutLength)
	{
		this.cutLength = cutLength;
	}

	/**
	 * Get a list of the genes that have not been taken before. This is useful
	 * if you do not wish the same gene to appear more than once in a
	 * chromosome.
	 * 
	 * @param source
	 *            The pool of genes to select from.
	 * @param taken
	 *            An array of the taken genes.
	 * @return Those genes in source that are not taken.
	 */
	private static Gene getNotTaken(final Chromosome source,
			final Set<Gene> taken) {
		final int geneLength = source.getGenes().size();

		for (int i = 0; i < geneLength; i++) {
			final Gene trial = source.getGene(i);
			
			boolean found = false;
			for(Gene current: taken )
			{
				if( current.equals(trial) ) {
					found = true;
					break;
				}
			}
			if (!found) {
				taken.add(trial);
				return trial;
			}
		}

		return null;
	}

	/**
	 * Assuming this chromosome is the "mother" mate with the passed in
	 * "father".
	 * 
	 * @param father
	 *            The father.
	 * @param offspring1
	 *            Returns the first offspring
	 * @param offspring2
	 *            Returns the second offspring.
	 */
	public void mate(
			final Chromosome mother,
			final Chromosome father,
			final Chromosome offspring1,
			final Chromosome offspring2) {
		final int geneLength = father.getGenes().size();

		// the chromosome must be cut at two positions, determine them
		final int cutpoint1 = (int) (Math.random() 
				* (geneLength - this.cutLength));
		final int cutpoint2 = cutpoint1 + this.cutLength;

		// keep track of which cities have been taken in each of the two
		// offspring, defaults to false.
		final Set<Gene> taken1 = new HashSet<Gene>();
		final Set<Gene> taken2 = new HashSet<Gene>();

		// handle cut section
		for (int i = 0; i < geneLength; i++) {
			if (!((i < cutpoint1) || (i > cutpoint2))) {
				offspring1.getGene(i).copy(father.getGene(i));
				offspring2.getGene(i).copy(mother.getGene(i));
				taken1.add(offspring1.getGene(i));
				taken2.add(offspring2.getGene(i));
			}
		}

		// handle outer sections
		for (int i = 0; i < geneLength; i++) {
			if ((i < cutpoint1) || (i > cutpoint2)) {
				
					offspring1.getGene(i).copy(getNotTaken(mother, taken1));
					offspring2.getGene(i).copy(getNotTaken(father, taken2));
				
			}
		}
	}
}
