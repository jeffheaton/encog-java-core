package org.encog.solve.genetic.genes;

public interface Gene extends Comparable<Gene> {
	public void copy(Gene gene);
}
