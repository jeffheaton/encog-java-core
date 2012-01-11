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
package org.encog.ml.hmm.alog;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.basic.BasicMLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class MarkovGenerator
{	
	private final HiddenMarkovModel hmm;
	private int currentState;
		
	public MarkovGenerator(HiddenMarkovModel hmm)
	{	
		this.hmm = hmm;
		newSequence();
	}
	
	public MLDataPair observation()
	{	
		MLDataPair o = hmm.getStateDistribution(currentState).generate();
		double rand = Math.random();
		
		for (int j = 0; j < hmm.getStateCount()-1; j++)
			if ((rand -= hmm.getTransitionProbability(currentState, j)) < 0) {
				currentState = j;
				return o;
			}
		
		currentState = hmm.getStateCount() - 1;
		return o;
	}
	
	public MLDataSet observationSequence(int length)
	{	
		MLDataSet sequence = new BasicMLDataSet();		
		while (length-- > 0)
			sequence.add(observation());
		newSequence();
		
		return sequence;
	}
	
	
	public void newSequence()
	{	
		double rand = Math.random(), current = 0.0;
		
		for (int i = 0; i < hmm.getStateCount() - 1; i++) {
			current += hmm.getPi(i);
			
			if (current > rand) {
				currentState = i;
				return;
			}
		}
		
		currentState = hmm.getStateCount() - 1;
	}
	
	public int getCurrentState()
	{
		return currentState;
	}
	
	public MLSequenceSet generateSequences(int observationCount, int observationLength)
	{
		MLSequenceSet result = new BasicMLSequenceSet();
		
		for (int i = 0; i < observationCount; i++) {
			result.startNewSequence();
			result.add(observationSequence(observationLength));
		}

		return result;
	}
}
