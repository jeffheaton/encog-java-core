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
