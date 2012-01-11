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

import org.encog.ml.data.MLDataSet;
import org.encog.ml.hmm.HiddenMarkovModel;

public class KullbackLeiblerDistanceCalculator
{	
	private int len = 1000;
	private int sequenceCount = 10;
	
	
	public double 
	distance(HiddenMarkovModel hmm1, HiddenMarkovModel hmm2)
	{			
		double distance = 0.;
		
		for (int i = 0; i < sequenceCount; i++) {
			
			MLDataSet oseq = new MarkovGenerator(hmm1).
			observationSequence(len);
			
			distance += (new ForwardBackwardScaledCalculator(oseq, hmm1).
					lnProbability() -
					new ForwardBackwardScaledCalculator(oseq, hmm2).
					lnProbability()) / len;
		}
		
		return distance / sequenceCount;
	}


	public int getLen() {
		return len;
	}


	public void setLen(int len) {
		this.len = len;
	}


	public int getSequenceCount() {
		return sequenceCount;
	}


	public void setSequenceCount(int sequenceCount) {
		this.sequenceCount = sequenceCount;
	}

	

}
