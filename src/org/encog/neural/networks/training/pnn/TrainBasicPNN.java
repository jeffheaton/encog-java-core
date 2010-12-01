package org.encog.neural.networks.training.pnn;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNOutputMode;

public class TrainBasicPNN {
	
	
	public static final double DEFAULT_QUIT_ERROR = 0.0;
	public static final double DEFAULT_QUIT_TOL = 0.0001;
	public static final double DEFAULT_LOW_SIGMA = 0.0001 ;
	public static final double DEFAULT_HIGH_SIGMA = 10.0 ;       // And highest
	public static final int DEFAULT_NUMBER_SIGMAS = 10;


	private final BasicPNN network;
	private final NeuralDataSet training;
	private double error;

	/**
	 * The error to quit at.
	 */
	private final double quitError;

	/**
	 * The quit tolerance.
	 */
	private final double quitTolerance;

	/**
	 * The low sigma.
	 */
	private final double sigmaLow;

	/**
	 * The high sigma.
	 */
	private final double sigmaHigh;

	/**
	 * The number of trial sigmas.
	 */
	private final int numberTrialSigmas;

	
	
	
	public TrainBasicPNN(BasicPNN network, NeuralDataSet training,
			double quitError, double quitTolerance,
			double sigmaLow, double sigmaHigh, int numberTrialSigmas) {
		super();
		this.network = network;
		this.training = training;
		this.quitError = quitError;
		this.quitTolerance = quitTolerance;
		this.sigmaLow = sigmaLow;
		this.sigmaHigh = sigmaHigh;
		this.numberTrialSigmas = numberTrialSigmas;
	}

	public TrainBasicPNN(BasicPNN network, NeuralDataSet training) {
		this(network,training,
				DEFAULT_QUIT_ERROR,
				DEFAULT_QUIT_TOL,
				DEFAULT_LOW_SIGMA,
				DEFAULT_HIGH_SIGMA,
				DEFAULT_NUMBER_SIGMAS);
						
	}

	public void learn() {

		int ivar, k;
		GlobalMinimumSearch globalMinimum = new GlobalMinimumSearch();
		DeriveMinimum dermin = new DeriveMinimum();

		if (this.network.getOutputMode() == PNNOutputMode.Classification) { 
			k = this.network.getOutputCount();
		} else {			
			k = this.network.getOutputCount() + 1;
		}

		double[] dsqr = new double[this.network.getInputCount()];
		double[] v = new double[this.network.getInputCount() * k];
		double[] w = new double[this.network.getInputCount() * k];
		double[] x = new double[this.network.getInputCount()];
		double[] base = new double[this.network.getInputCount()];
		double[] direc = new double[this.network.getInputCount()];
		double[] g = new double[this.network.getInputCount()];
		double[] h = new double[this.network.getInputCount()];
		double[] dwk2 = new double[this.network.getInputCount()];

		network.setSamples((BasicNeuralDataSet) this.training);
		network.setSharedSamples(true);

		// Is the network alrady trained?
		if (this.network.isTrained()) {
			k = 0; 
			for (int i = 0; i < this.network.getInputCount(); i++)
				x[i] = this.network.getSigma()[i];
			globalMinimum.setY2(1.e30);
		} else {
			globalMinimum.findBestRange(this.sigmaLow, this.sigmaHigh,
					this.numberTrialSigmas, true, this.quitError, this.network);

			for (ivar = 0; ivar < this.network.getInputCount(); ivar++) {
				x[ivar] = globalMinimum.getX2(); 
			}
		}

		double d = dermin.calculate(32767, this.quitError, 1.e-8,
				this.quitTolerance, this.network, this.network.getInputCount(),
				x, globalMinimum.getY2(), base, direc, g, h, dwk2);
		globalMinimum.setY2(d);

		// Training complete

		for (ivar = 0; ivar < this.network.getInputCount(); ivar++)
			this.network.getSigma()[ivar] = x[ivar];

		this.error = Math.abs(globalMinimum.getY2()); 
		this.network.setTrained(true);
	}

	/**
	 * @return the error
	 */
	public double getError() {
		return error;
	}
	
	

}
