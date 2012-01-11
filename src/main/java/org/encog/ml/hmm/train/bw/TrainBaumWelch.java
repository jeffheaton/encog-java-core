package org.encog.ml.hmm.train.bw;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.MLSequenceSet;
import org.encog.ml.hmm.HiddenMarkovModel;
import org.encog.ml.hmm.alog.ForwardBackwardCalculator;


public class TrainBaumWelch extends BaseBaumWelch
{	
	public TrainBaumWelch(HiddenMarkovModel hmm, MLSequenceSet training) {
		super(hmm, training);
	}

	public ForwardBackwardCalculator
	generateForwardBackwardCalculator(MLDataSet sequence, HiddenMarkovModel hmm)
	{	
		return new ForwardBackwardCalculator(sequence, hmm, 
				EnumSet.allOf(ForwardBackwardCalculator.Computation.class));
	}
	
	public double[][][]
	estimateXi(MLDataSet sequence, ForwardBackwardCalculator fbc,
			HiddenMarkovModel hmm)
	{	
		if (sequence.size() <= 1)
			throw new IllegalArgumentException("Must have more than one observation");
		
		double xi[][][] = 
			new double[sequence.size()-1][hmm.getStateCount()][hmm.getStateCount()];
		double probability = fbc.probability();
		
		Iterator<MLDataPair> seqIterator = sequence.iterator();
		seqIterator.next();
		
		for (int t = 0; t < sequence.size() - 1; t++) {
			MLDataPair o = seqIterator.next();
			
			for (int i = 0; i < hmm.getStateCount(); i++)
				for (int j = 0; j < hmm.getStateCount(); j++)
					xi[t][i][j] = fbc.alphaElement(t, i) *
					hmm.getTransitionProbability(i, j) *
					hmm.getStateDistribution(j).probability(o) *
					fbc.betaElement(t+1, j) / probability;
		}
		
		return xi;
	}
	
	protected double[][]
	estimateGamma(double[][][] xi, ForwardBackwardCalculator fbc)
	{
		double[][] gamma = new double[xi.length + 1][xi[0].length];
		
		for (int t = 0; t < xi.length + 1; t++)
			Arrays.fill(gamma[t], 0.);
		
		for (int t = 0; t < xi.length; t++)
			for (int i = 0; i < xi[0].length; i++)
				for (int j = 0; j < xi[0].length; j++)
					gamma[t][i] += xi[t][i][j];
		
		for (int j = 0; j < xi[0].length; j++)
			for (int i = 0; i < xi[0].length; i++)
				gamma[xi.length][j] += xi[xi.length - 1][i][j];
		
		return gamma;
	}
	
}
