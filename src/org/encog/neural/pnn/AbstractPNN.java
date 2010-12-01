package org.encog.neural.pnn;

import org.encog.neural.data.basic.BasicNeuralDataSet;

public class AbstractPNN {

	private final int inputCount;
	private final int outputCount;
	private final PNNKernelType kernelType;
	private final PNNOutputMode outputMode;
	private final BasicNeuralDataSet samples = new BasicNeuralDataSet();
	
	/**
	 * The first derivative.
	 */
	private final double[] deriv; 
	
	/**
	 * The second derivative.
	 */
	private final double[] deriv2; 
	
	
	public AbstractPNN(int inputCount, int outputCount, PNNKernelType kernelType, PNNOutputMode outputMode) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.kernelType = kernelType;
		this.outputMode = outputMode;
		
		deriv = new double[inputCount];
		deriv2 = new double[inputCount];
	}
	
	

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * @return the kernelType
	 */
	public PNNKernelType getKernelType() {
		return kernelType;
	}

	/**
	 * @return the outputMode
	 */
	public PNNOutputMode getOutputMode() {
		return outputMode;
	}

	/**
	 * @return the samples
	 */
	public BasicNeuralDataSet getSamples() {
		return samples;
	}



	/**
	 * @return the deriv
	 */
	public double[] getDeriv() {
		return deriv;
	}



	/**
	 * @return the deriv2
	 */
	public double[] getDeriv2() {
		return deriv2;
	}



	public double calcErrorWithSingleSigma(double xrecent) {
		// TODO Auto-generated method stub
		return 0;
	}



	public double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
