package org.encog.neural.pnn;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public abstract class AbstractPNN {

	// Input neuron count.
	private final int inputCount;

	// Output neuron count.
	private final int outputCount;

	// Kernel type.
	private final PNNKernelType kernel;

	// Output mode.
	private final PNNOutputMode outputMode; // Output model (see OUTMOD_? in
											// CONST.H)

	// Is trained.
	private boolean trained;

	// Network error. (MSE)
	private double error;

	// Confusion work area.
	private int[] confusion;

	// First derivative.
	private final double[] deriv;

	// Second derivative
	private final double[] deriv2;

	// Index of a sample to exclude.
	private int exclude;

	// True, if we are using separate sigmas for each class.
	boolean separateClass;

	/**
	 * Constructor.
	 * @param kernel The kernel type to use.
	 * @param outputMode The output mode to use.
	 * @param inputCount The input count.
	 * @param outputCount The output count.
	 */
	public AbstractPNN(final PNNKernelType kernel, final PNNOutputMode outputMode,
			final int inputCount, final int outputCount) {
		this.kernel = kernel;
		this.outputMode = outputMode;
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		this.trained = false;
		this.error = Double.MIN_VALUE;
		this.confusion = null;
		this.exclude = -1;

		this.deriv = new double[inputCount];
		this.deriv2 = new double[inputCount];

		if (this.outputMode == PNNOutputMode.Classification) {
			this.confusion = new int[this.outputCount + 1];
		}
	}

	/**
	 * Compute the output from the network.
	 * @param input The input to the network.
	 * @return The output from the network.
	 */
	public abstract NeuralData compute(NeuralData input);

	/**
	 * Compute the derivative.
	 * @param input The input. 
	 * @param target The target.
	 * @return The output.
	 */
	public abstract NeuralData computeDeriv(NeuralData input, NeuralData target);

	/**
	 * @return the deriv
	 */
	public double[] getDeriv() {
		return this.deriv;
	}

	/**
	 * @return the deriv2
	 */
	public double[] getDeriv2() {
		return this.deriv2;
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return the exclude
	 */
	public int getExclude() {
		return this.exclude;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return the kernel
	 */
	public PNNKernelType getKernel() {
		return this.kernel;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return the outputMode
	 */
	public PNNOutputMode getOutputMode() {
		return this.outputMode;
	}

	/**
	 * @return the trained
	 */
	public boolean isTrained() {
		return this.trained;
	}

	/**
	 * Privatize data.
	 */
	public abstract void privatize();

	/**
	 * Reset the confusion.
	 */
	public void resetConfusion() {

	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(final double error) {
		this.error = error;
	}

	/**
	 * @param exclude
	 *            the exclude to set
	 */
	public void setExclude(final int exclude) {
		this.exclude = exclude;
	}

	/**
	 * @param trained
	 *            the trained to set
	 */
	public void setTrained(final boolean trained) {
		this.trained = trained;
	}

	public double calculateError(final BasicNeuralDataSet training, final boolean find_deriv) {

		double err, tot_err;
		double diff;
		tot_err = 0.0;

		if (find_deriv) {
			int num = (this.separateClass) ? this.inputCount * this.outputCount
					: this.inputCount;
			for (int i = 0; i < num; i++) {
				this.deriv[i] = 0.0; 
				this.deriv2[i] = 0.0;
			}
		}

		this.exclude = (int) training.getRecordCount();

		final NeuralDataPair pair = BasicNeuralDataPair.createPair(training
				.getInputSize(), training.getIdealSize());

		final double[] out = new double[this.outputCount];

		for (int r = 0; r < training.getRecordCount(); r++) {
			training.getRecord(r, pair);
			this.exclude--;

			err = 0.0; 

			final NeuralData input = pair.getInput();
			final NeuralData target = pair.getIdeal();

			if (this.outputMode == PNNOutputMode.Unsupervised) { 
				if (find_deriv) {
					final NeuralData output = computeDeriv(input, target);
					for (int z = 0; z < this.outputCount; z++) {
						out[z] = output.getData(z);
					}
				} else {
					final NeuralData output = compute(input);
					for (int z = 0; z < this.outputCount; z++) {
						out[z] = output.getData(z);
					}
				}
				for (int i = 0; i < this.outputCount; i++) {
					diff = input.getData(i) - out[i]; 				
					err += diff * diff;
				}
			}

			else if (this.outputMode == PNNOutputMode.Classification) {
				int tclass = (int) target.getData(0);
				if (find_deriv) {
					final NeuralData output = computeDeriv(input, pair.getIdeal());
					int oclass = (int) output.getData(0);
				} else {
					final NeuralData output = compute(input);
					int oclass = (int) output.getData(0);
				}
				for (int i = 0; i < this.outputCount; i++) {
					if (i == tclass) {
						diff = 1.0 - out[i];
						err += diff * diff;
					} else {
						err += out[i] * out[i];
					}
				}
			} 

			else if (this.outputMode == PNNOutputMode.Regression) {
				if (find_deriv) {
					final NeuralData output = compute(input);
					for (int z = 0; z < this.outputCount; z++) {
						out[z] = output.getData(z);
					}
				} else {
					final NeuralData output = compute(input);
					for (int z = 0; z < this.outputCount; z++) {
						out[z] = output.getData(z);
					}
				}
				for (int i = 0; i < this.outputCount; i++) {
					diff = target.getData(i) - out[i];
					err += diff * diff;
				} 
			} 

			tot_err += err;
		} 

		this.exclude = -1;

		this.error = tot_err / training.getRecordCount(); 
		if (find_deriv) {
			for (int i = 0; i < this.deriv.length; i++) {
				this.deriv[i] /= training.getRecordCount(); 
				this.deriv2[i] /= training.getRecordCount();
			}
		}

		if ((this.outputMode == PNNOutputMode.Unsupervised)
				|| (this.outputMode == PNNOutputMode.Regression)) {
			this.error /= this.outputCount; 
			if (find_deriv) {
				for (int i = 0; i < this.inputCount; i++) {
					this.deriv[i] /= this.outputCount; 
					this.deriv2[i] /= this.outputCount;
				}
			}
		}

		return this.error;
	}
	
	/**
	 * @return the separateClass
	 */
	public boolean isSeparateClass() {
		return separateClass;
	}

	/**
	 * @param separateClass the separateClass to set
	 */
	public void setSeparateClass(boolean separateClass) {
		this.separateClass = separateClass;
	}

	public abstract double calcErrorWithSingleSigma(double sigma);


	public abstract double calcErrorWithMultipleSigma(double[] x, double[] direc,
			double[] deriv22, boolean b);
	
}
