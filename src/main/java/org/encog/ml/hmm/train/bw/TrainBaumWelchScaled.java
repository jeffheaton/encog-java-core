package org.encog.ml.hmm.train.bw;

import java.util.EnumSet;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;
import org.encog.ml.hmm.alog.ForwardBackwardScaledCalculator;

public class TrainBaumWelchScaled
extends BaseBaumWelch
{	
	public TrainBaumWelchScaled(HiddenMarkovModel hmm, MLSequenceSet training) {
		super(hmm, training);
	}


	public ForwardBackwardCalculator
	generateForwardBackwardCalculator(MLDataSet sequence,
			HiddenMarkovModel hmm)
	{
		return new ForwardBackwardScaledCalculator(sequence, hmm, 
				EnumSet.allOf(ForwardBackwardCalculator.Computation.class));
	}
		
	public double[][][]
	estimateXi(MLDataSet sequence, ForwardBackwardCalculator fbc,
			HiddenMarkovModel hmm)
	{	
		if (sequence.size() <= 1)
			throw new IllegalArgumentException("Must have more than one observation");
		
		double xi[][][] = 
			new double[sequence.size() - 1][hmm.getStateCount()][hmm.getStateCount()];
		
		Iterator<MLDataPair> seqIterator = sequence.iterator();
		seqIterator.next();
		
		for (int t = 0; t < sequence.size() - 1; t++) {
			MLDataPair observation = seqIterator.next();
			
			for (int i = 0; i < hmm.getStateCount(); i++)
				for (int j = 0; j < hmm.getStateCount(); j++)
					xi[t][i][j] = fbc.alphaElement(t, i) *
					hmm.getTransitionProbability(i, j) * 
					hmm.getStateDistribution(j).probability(observation) *
					fbc.betaElement(t + 1, j);
		}
		
		return xi;
	}
}
