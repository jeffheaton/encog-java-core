/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.bayesian.training.estimator;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.table.TableLine;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * A simple probability estimator.
 */
public class SimpleEstimator implements BayesEstimator {
	
	private MLDataSet data;
	private BayesianNetwork network;
	private TrainBayesian trainer;
	private int index;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(TrainBayesian theTrainer,BayesianNetwork theNetwork, MLDataSet theData) {
		this.network = theNetwork;
		this.data = theData;
		this.trainer = theTrainer;
		this.index = 0;
	} 
	
	
	/**
	 * Calculate the probability.
	 * @param event The event.
	 * @param result The result.
	 * @param args The arguments.
	 * @return The probability.
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
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
