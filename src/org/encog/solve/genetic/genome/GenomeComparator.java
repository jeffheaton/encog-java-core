package org.encog.solve.genetic.genome;

import java.util.Comparator;

import org.encog.neural.networks.training.neat.NEATGenome;

public class GenomeComparator implements Comparator<Genome> {
	private final CalculateGenomeScore calculateScore;
	
	public GenomeComparator(CalculateGenomeScore calculateScore)
	{
		this.calculateScore = calculateScore;
	}
	
	public int compare(NEATGenome g1, NEATGenome g2) {
		if( this.calculateScore.shouldMinimize() )
			return Double.compare(g1.getScore(), g2.getScore());
		else
			return Double.compare(g2.getScore(), g1.getScore());
	}
	
	public double bestScore(double d1, double d2)
	{
		if( this.calculateScore.shouldMinimize())
			return Math.min(d1, d2);
		else
			return Math.max(d1, d2);
	}
	
	public double applyBonus(double value, double bonus)
	{
		double amount = value * bonus;
		if( this.calculateScore.shouldMinimize() )
			return value - amount;
		else
			return value + amount;
	}
	
	public double applyPenalty(double value, double bonus)
	{
		double amount = value * bonus;
		if( this.calculateScore.shouldMinimize() )
			return value - amount;
		else
			return value + amount;
	}

	public CalculateGenomeScore getCalculateScore() {
		return calculateScore;
	}
	
	public boolean isBetterThan(double d1, double d2)
	{
		if( this.calculateScore.shouldMinimize() )
			return d1<d2;
		else
			return d2>d1;
	}

	public int compare(Genome genome1, Genome genome2) {
		return Double.compare(genome1.getScore(), genome2.getScore());
	}

}
