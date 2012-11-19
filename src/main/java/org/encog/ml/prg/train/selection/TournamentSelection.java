package org.encog.ml.prg.train.selection;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

public class TournamentSelection implements PrgSelection {
	private PrgPopulation population;
	private int rounds;
	
	public TournamentSelection(PrgPopulation thePopulation, int rounds) {
		this.population = thePopulation;
	}
	
	
	public PrgPopulation getPopulation() {
		return population;
	}



	public void setPopulation(PrgPopulation population) {
		this.population = population;
	}



	public int getRounds() {
		return rounds;
	}



	public void setRounds(int rounds) {
		this.rounds = rounds;
	}



	@Override
	public int performSelection() {
		int bestIndex = RangeRandomizer.randomInt(0, this.population.size());
	    EncogProgram best = this.population.getMembers()[bestIndex];
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, this.population.size());
	      EncogProgram competitor = this.population.getMembers()[competitorIndex];
	      if ( competitor.getScore() > best.getScore() ) {
	        best = competitor;
	        bestIndex = competitorIndex;
	      }
	    }
	    return( bestIndex );
	}


	@Override
	public int performAntiSelection() {
		int worstIndex = RangeRandomizer.randomInt(0, this.population.size());
	    EncogProgram worst = this.population.getMembers()[worstIndex];
	    
	    for ( int i = 0; i < this.rounds; i ++ ) {
	    	int competitorIndex = RangeRandomizer.randomInt(0, this.population.size());
	      EncogProgram competitor = this.population.getMembers()[competitorIndex];
	      if ( competitor.getScore() > worst.getScore() ) {
	        worst = competitor;
	        worstIndex = competitorIndex;
	      }
	    }
	    return( worstIndex );
	}
	
	
}
