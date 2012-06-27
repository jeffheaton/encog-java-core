package org.encog.ml.train.strategy.end;

import org.encog.ml.MLError;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

/**
 * Stop early when validation set no longer improves.
 * 
 * Based on the following paper:
 * 
 * @techreport{Prechelt94c,
 * author    = {Lutz Prechelt},
 * title     = {{PROBEN1} --- {A} Set of Benchmarks and Benchmarking
 *              Rules for Neural Network Training Algorithms},
 * institution = {Fakult\"at f\"ur Informatik, Universit\"at Karlsruhe},
 * year      = {1994},
 * number    = {21/94},
 * address   = {D-76128 Karlsruhe, Germany},
 * month     = sep,
 * note      = {Anonymous FTP: /pub/pa\-pers/tech\-reports/1994/1994-21.ps.Z
 *              on ftp.ira.uka.de},
 * }
 */
public class EarlyStoppingStrategy implements EndTrainingStrategy {
	/**
	 * The validation set.
	 */
	private MLDataSet validationSet;

	/**
	 * The test set.
	 */
	private MLDataSet testSet;

	/**
	 * The trainer.
	 */
	private MLTrain train;

	/**
	 * Has training stopped.
	 */
	private boolean stop;

	/**
	 * Current training error.
	 */
	private double trainingError;

	/**
	 * Current test error.
	 */
	private double testError;

	/**
	 * Current validation error.
	 */
	private double validationError;

	/**
	 * The error calculation.
	 */
	private MLError calc;

	/**
	 * eOpt value, calculated for early stopping.  
	 * The lowest validation error so far.
	 */
	private double eOpt;

	/**
	 * gl value, calculated for early stopping.
	 */
	private double gl;

	/**
	 * Alpha value, calculated for early stopping. Once "gl" is above alpha, training will stop.
	 */
	private double alpha;

	/**
	 * The last time the test set was checked.
	 */
	private int lastCheck;

	/**
	 * Validation strip length.
	 */
	private int stripLength;

	private double stripOpt;
	private double stripTotal;
	private double stripEfficiency;
	private double minEfficiency;

	/**
	 * Construct the early stopping strategy.
	 * Use default operating parameters.
	 * @param theValidationSet The validation set.
	 * @param theTestSet The test set.
	 */
	public EarlyStoppingStrategy(MLDataSet theValidationSet,
			MLDataSet theTestSet) {
		this(theValidationSet, theTestSet, 5, 5, 0.1);
	}

	/**
	 * Construct the early stopping strategy.
	 * @param theValidationSet
	 * @param theTestSet
	 * @param theStripLength The number of training set elements to validate.
	 * @param theAlpha Stop once GL is below this value.
	 * @param theMinEfficiency The minimum training efficiency to stop.
	 */
	public EarlyStoppingStrategy(MLDataSet theValidationSet,
			MLDataSet theTestSet, int theStripLength, double theAlpha,
			double theMinEfficiency) {
		this.validationSet = theValidationSet;
		this.testSet = theTestSet;
		this.alpha = theAlpha;
		this.stripLength = theStripLength;
		this.minEfficiency = theMinEfficiency;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain theTrain) {
		this.train = theTrain;
		this.calc = (MLError) train.getMethod();
		this.eOpt = Double.POSITIVE_INFINITY;
		this.stripOpt = Double.POSITIVE_INFINITY;
		this.stop = false;
		this.lastCheck = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		this.lastCheck++;
		this.trainingError = this.train.getError();

		this.stripOpt = Math.min(this.stripOpt, this.trainingError);
		this.stripTotal += this.trainingError;

		if (this.lastCheck > this.stripLength) {
			this.validationError = this.calc.calculateError(this.validationSet);
			this.testError = this.calc.calculateError(this.testSet);
			this.eOpt = Math.min(this.validationError, eOpt);
			this.gl = 100.0 * ((this.validationError / this.eOpt) - 1.0);

			this.stripEfficiency = (this.stripTotal)
					/ (this.stripLength * this.stripOpt);

			//System.out.println("eff=" + this.stripEfficiency + ", gl=" + this.gl);

			// setup for next time
			this.stripTotal = 0;
			this.lastCheck = 0;

			// should we stop?
			stop = (this.gl > this.alpha)
					|| (this.stripEfficiency < this.minEfficiency);
		}
	}

	/**
	 * @return Returns true if we should stop.
	 */
	@Override
	public boolean shouldStop() {
		return stop;
	}

	/**
	 * @return the trainingError
	 */
	public double getTrainingError() {
		return trainingError;
	}

	/**
	 * @return the testError
	 */
	public double getTestError() {
		return testError;
	}

	/**
	 * @return the validationError
	 */
	public double getValidationError() {
		return validationError;
	}

	/**
	 * @return the eOpt
	 */
	public double geteOpt() {
		return eOpt;
	}

	/**
	 * @return the gl
	 */
	public double getGl() {
		return gl;
	}

	/**
	 * @return the stripLength
	 */
	public int getStripLength() {
		return stripLength;
	}

	/**
	 * @return the stripOpt
	 */
	public double getStripOpt() {
		return stripOpt;
	}

	/**
	 * @return the stripEfficiency
	 */
	public double getStripEfficiency() {
		return stripEfficiency;
	}

	/**
	 * @return the minEfficiency
	 */
	public double getMinEfficiency() {
		return minEfficiency;
	}

}
