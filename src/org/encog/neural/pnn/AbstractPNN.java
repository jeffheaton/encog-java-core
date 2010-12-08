package org.encog.neural.pnn;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public abstract class AbstractPNN {


	public abstract double calcErrorWithSingleSigma(double sigma);


	public abstract double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b);
	
}
