package org.encog.neural.networks.training.neat;

import java.util.Comparator;

import org.encog.neural.networks.training.CalculateScore;

public class NEATGenomeComparator implements Comparator<NEATGenome> {

	private final CalculateScore calculateScore;
	
	public NEATGenomeComparator(CalculateScore calculateScore)
	{
		this.calculateScore = calculateScore;
	}
	
	public int compare(NEATGenome g1, NEATGenome g2) {
		if( this.calculateScore.shouldMinimize() )
			return Double.compare(g1.getFitness(), g2.getFitness());
		else
			return Double.compare(g2.getFitness(), g1.getFitness());
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

	public CalculateScore getCalculateScore() {
		return calculateScore;
	}
	
	public boolean isBetterThan(double d1, double d2)
	{
		if( this.calculateScore.shouldMinimize() )
			return d1<d2;
		else
			return d2>d1;
	}
	
	

}
