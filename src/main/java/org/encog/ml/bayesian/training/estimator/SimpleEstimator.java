package org.encog.ml.bayesian.training.estimator;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.table.TableLine;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class SimpleEstimator implements BayesEstimator {
	
	private MLDataSet data;
	private BayesianNetwork network;
	private TrainBayesian trainer;
	private int index;
	
	@Override
	public void init(TrainBayesian theTrainer,BayesianNetwork theNetwork, MLDataSet theData) {
		this.network = theNetwork;
		this.data = theData;
		this.trainer = theTrainer;
		this.index = 0;
	} 
	
	
	public double calculateProbability(BayesianEvent event, int result, int[] args) {
		int eventIndex = this.network.getEvents().indexOf(event);
		int x = 0;
		int y = 0;
		
		// calculate overall probability
		for( MLDataPair pair : this.data ) {
			int[] d = this.network.determineClasses( pair.getInput() );
			
			if( args.length==0 ) {
				x++;
				if( d[eventIndex]==result ) {
					y++;
				}
			}
			else if( d[eventIndex]==result ) {
				x++;
				
				int i = 0;
				boolean givenMatch = true;
				for(BayesianEvent givenEvent : event.getParents()) {
					int givenIndex = this.network.getEventIndex(givenEvent);
					if( args[i]!=d[givenIndex] ) {
						givenMatch = false;
						break;
					}
					i++;
				}
				
				if( givenMatch ) {
					y++;
				}
			}
		}
		
		double num = y + 1;
		double den = x + event.getChoices().size();
		
		
		return num/den;
	}
	
	@Override
	public boolean iteration() {
		BayesianEvent event = this.network.getEvents().get(this.index);
		for(TableLine line : event.getTable().getLines() ) {
			line.setProbability(calculateProbability(event,line.getResult(),line.getArguments()));
		}
		index++;
		
		return index<this.network.getEvents().size();
	}
}
