package org.encog.ml.ea.opp.selection;

import java.io.Serializable;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.ml.ea.train.basic.BasicEA;
import org.encog.ml.prg.train.PrgGenetic;

public class TournamentSelection implements PrgSelection, Serializable {
	private EvolutionaryAlgorithm trainer;
	private int rounds;
	
	public TournamentSelection(EvolutionaryAlgorithm theTrainer, int theRounds) {
		this.trainer = theTrainer;
		this.rounds = theRounds;
	}
	
	@Override
	public EvolutionaryAlgorithm getTrainer() {
		return trainer;
	}
	
	public void setTrainer(PrgGenetic trainer) {
		this.trainer = trainer;
	}
	
	public int getRounds() {
		return rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	@Override
	public int performSelection() {
		Population population = trainer.getPopulation();
		int bestIndex = RangeRandomizer.randomInt(0, population.size()-1);
	    Genome best = population.get(bestIndex);
	    BasicEA.calculateScoreAdjustment(best, this.trainer.getScoreAdjusters());
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, population.size()-1);
	      Genome competitor = population.get(competitorIndex);
	      
	      // only evaluate valid genomes
	      if (!Double.isInfinite(competitor.getScore()) && !Double.isNaN(competitor.getScore())) {
	    	  BasicEA.calculateScoreAdjustment(competitor, this.trainer.getScoreAdjusters());
		      if ( this.trainer.getSelectionComparator().isBetterThan(competitor, best) ) {
		        best = competitor;
		        bestIndex = competitorIndex;
		      }
	      }
	    }
	    return( bestIndex );
	}


	@Override
	public int performAntiSelection() {
		Population population = trainer.getPopulation();
		int worstIndex = RangeRandomizer.randomInt(0, population.size()-1);
	    Genome worst = population.get(worstIndex);
	    BasicEA.calculateScoreAdjustment(worst, this.trainer.getScoreAdjusters());
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, population.size()-1);
	      Genome competitor = population.get(competitorIndex);
	      
	      // force an invalid genome to lose
	      if (Double.isInfinite(competitor.getScore()) || Double.isNaN(competitor.getScore())) {
	    	  return competitorIndex;
	      }
	      
	      BasicEA.calculateScoreAdjustment(competitor, this.trainer.getScoreAdjusters());
	      if ( !this.trainer.getSelectionComparator().isBetterThan(competitor, worst) ) {
	        worst = competitor;
	        worstIndex = competitorIndex;
	      }
	    }
	    return( worstIndex );
	}
	
	
}
