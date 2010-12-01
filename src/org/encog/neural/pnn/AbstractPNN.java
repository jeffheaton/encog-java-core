package org.encog.neural.pnn;

import org.encog.neural.data.basic.BasicNeuralDataSet;

public abstract class AbstractPNN {

	private final int inputCount;
	private final int outputCount;
	private final PNNKernelType kernelType;
	private final PNNOutputMode outputMode;
	private BasicNeuralDataSet samples = new BasicNeuralDataSet();
	private boolean sharedSamples;
	private boolean trained;
	
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



	public abstract double calcErrorWithSingleSigma(double xrecent);


	public abstract double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b);



	/**
	 * @return the sharedSamples
	 */
	public boolean isSharedSamples() {
		return sharedSamples;
	}



	/**
	 * @param sharedSamples the sharedSamples to set
	 */
	public void setSharedSamples(boolean sharedSamples) {
		this.sharedSamples = sharedSamples;
	}



	/**
	 * @param samples the samples to set
	 */
	public void setSamples(BasicNeuralDataSet samples) {
		this.samples = samples;
		this.sharedSamples = true;
	}



	/**
	 * @return the trained
	 */
	public boolean isTrained() {
		return trained;
	}



	/**
	 * @param trained the trained to set
	 */
	public void setTrained(boolean trained) {
		this.trained = trained;
	}
	
	
	
	
}
