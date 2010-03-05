package org.encog.solve.genetic.genome;

import java.util.List;


public interface Genome extends Comparable<Genome> {
	List<Chromosome> getChromosomes();
	int calculateGeneCount();
	void mate(Genome father, Genome child1, Genome child2);
	double getScore();
	Object getOrganism();
	void calculateScore();
	void encode();
	void decode();
}
