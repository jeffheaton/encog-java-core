package org.encog.solve.genetic.mutate;

import org.encog.solve.genetic.Chromosome;
import org.encog.solve.genetic.IntegerGene;
import org.encog.solve.genetic.genes.Gene;

public class MutateShuffle implements Mutate {

	@Override
	public void performMutation(Chromosome chromosome) {
		final int length = chromosome.getGenes().size();
		int iswap1 = (int) (Math.random() * length);
		int iswap2 = (int) (Math.random() * length);
		
		// can't be equal
		if( iswap1==iswap2 )
		{
			// move to the next, but
			// don't go out of bounds
			if( iswap1>0)
				iswap1--;
			else
				iswap1++;
			
		}
		
		
		// make sure they are in the right order
		if( iswap1 > iswap2 )
		{
			int temp = iswap1;
			iswap1=iswap2;
			iswap2=temp;
		}
		
		Gene gene1 = chromosome.getGenes().get(iswap1);
		Gene gene2 = chromosome.getGenes().get(iswap2);
		
		// remove the two genes
		chromosome.getGenes().remove(gene1);
		chromosome.getGenes().remove(gene2);
		
		// insert them back in, opposit order
		chromosome.getGenes().add(iswap1, gene2);
		chromosome.getGenes().add(iswap2, gene1);
	}

}
