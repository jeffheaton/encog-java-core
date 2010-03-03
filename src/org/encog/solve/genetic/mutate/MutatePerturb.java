package org.encog.solve.genetic.mutate;

import org.encog.solve.genetic.Chromosome;
import org.encog.solve.genetic.genes.DoubleGene;
import org.encog.solve.genetic.genes.Gene;

public class MutatePerturb implements Mutate {
	
	private double perturbAmount;
	
	public MutatePerturb(double perturbAmount)
	{
		this.perturbAmount = perturbAmount;
	}
	
	public void performMutation(Chromosome chromosome)
	{
		for( Gene gene: chromosome.getGenes() )
		{
			if( gene instanceof DoubleGene )
			{
				DoubleGene doubleGene = (DoubleGene)gene;
				double value = doubleGene.getValue();
				value+= (this.perturbAmount - (Math.random() * this.perturbAmount * 2));
				doubleGene.setValue(value);
			}
		}
	}
}
