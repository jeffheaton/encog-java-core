package org.encog.solve.genetic.genome;

import java.util.List;


public interface Genome extends Comparable<Genome> {
	
	List<Chromosome> getChromosomes();
	int calculateGeneCount();
	void mate(Genome father, Genome child1, Genome child2);
	double getScore();
	void setScore(double score);
	Object getOrganism();
	void encode();
	void decode();
	long getGenomeID();
	void setGenomeID(long genomeID);
	public double getAdjustedScore();
	public void setAdjustedScore(double adjustedScore);
	public double getAmountToSpawn();
	public void setAmountToSpawn(double amountToSpawn);
}
