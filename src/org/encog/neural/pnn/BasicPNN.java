package org.encog.neural.pnn;

import org.encog.mathutil.EncogMath;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;

public class BasicPNN extends AbstractPNN {

	/**
	 * The sigma values(or widths) of the kernels.
	 */
	private double[] sigma;
	private double[] priors;
	private int[] classCount;


	
	
	/**
	 * @return the sigma
	 */
	public double[] getSigma() {
		return sigma;
	}


	public BasicPNN(int inputCount, int outputCount, PNNKernelType kernelType,
			PNNOutputMode outputMode) {
		super(inputCount, outputCount, false, kernelType, outputMode);

		sigma = new double[inputCount];
		
		if( outputMode==PNNOutputMode.Classification )
		{
			classCount = new int[outputCount];
			priors = new double[outputCount];			
		}
		
		switch(outputMode)
		{
			case Classification:
				break;
			case Unsupervised:
				break;
			case Regression:
				break;
		}
	}
	
	public NeuralData computeDeriv(NeuralData input) {
		return null;
	}
	

	public NeuralData compute(NeuralData input) {

		double[] output = new double[this.getOutputCount()];

		double psum = 0.0; // Denominator sum if AUTO or GENERAL

		int trainingIndex = -1;
		for (NeuralDataPair pair : this.getSamples()) {

			// should this sample be excluded?
			trainingIndex++;
			if( trainingIndex==this.getExclude() )
				continue;
			
			double distance = 0;

			for (int i = 0; i < getInputCount(); i++) {
				double diff = input.getData(i) - pair.getInput().getData()[i];
				diff /= this.sigma[i];
				distance += diff * diff;
			}

			if (getKernelType() == PNNKernelType.Gaussian)
				distance = Math.exp(-distance);
			else if (getKernelType() == PNNKernelType.Reciprocal)
				distance = 1.0 / (1.0 + distance);

			distance = Math.max(distance, 1.e-40);

			switch (getOutputMode()) {
			case Classification:
				int i = (int) pair.getIdeal().getData()[0];
				output[i] += distance;
				break;
			case Unsupervised:
				for (int j = 0; j < getInputCount(); j++) {
					// Outputs same as inputs
					output[j] += distance * pair.getIdeal().getData(j);
				}
				psum += distance;
				break;
			case Regression:
				for (int k = 0; k < getOutputCount(); k++) {
					output[k] += distance * pair.getIdeal().getData(k);
				}
				psum += distance;
			}
		}

		switch (getOutputMode()) {
		case Classification:
			psum = 0.0;
			for (int i = 0; i < getOutputCount(); i++) {
				if (this.priors[i] >= 0.0)
					output[i] *= this.priors[i] / this.classCount[i];
				psum += output[i];
			}

			if (psum < 1.e-40) // If this test case is far from all
				psum = 1.e-40; // prevent division by zero

			for (int i = 0; i < getOutputCount(); i++)
				output[i] /= psum;

			NeuralData result = new BasicNeuralData(1);
			result.setData(0, (int) EncogMath.maxIndex(output));
			return result;

		case Unsupervised:
			for (int i = 0; i < getInputCount(); i++)
				output[i] /= psum;
			return new BasicNeuralData(output);

		case Regression:
			for (int i = 0; i < getOutputCount(); i++)
				output[i] /= psum;
			return new BasicNeuralData(output);
		default:
			return null;

		}
	}

	@Override
	public double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b) {
		// TODO Auto-generated method stub
		return 0;
	}


	public double calcErrorWithSingleSigma(double sigma) {
		   int ivar ;

		   for (ivar=0 ; ivar<getInputCount() ; ivar++)
		      getSigma()[ivar] = sigma ;

		   return calculateError ( this.getSamples() , false ) ;
	}


	@Override
	public NeuralData computeDeriv(NeuralData input, NeuralData ideal) {
		return null;
	}
	
	
}
