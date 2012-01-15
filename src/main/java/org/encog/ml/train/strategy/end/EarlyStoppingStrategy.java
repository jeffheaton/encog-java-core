package org.encog.ml.train.strategy.end;

import org.encog.ml.MLError;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

public class EarlyStoppingStrategy implements EndTrainingStrategy {
	
	private MLDataSet validationSet; 
	private MLDataSet testSet;
	private MLTrain train;
	private boolean stop;
	private double trainingError;
	private double testError;
	private double validationError;
	private MLError calc;
	private double eOpt;
	private double gl;
	private double alpha;
	private int lastCheck;
	private int stripLength;
	private double stripOpt;
	private double stripTotal;
	private double stripEfficiency;
	private double minEfficiency;
	
	public EarlyStoppingStrategy(MLDataSet theValidationSet, MLDataSet theTestSet) {
		this(theValidationSet,theTestSet,5,5,0.1);
	}
	
	public EarlyStoppingStrategy(MLDataSet theValidationSet, MLDataSet theTestSet, int theStripLength, double theAlpha, double theMinEfficiency ) {
		this.validationSet = theValidationSet;
		this.testSet = theTestSet;		
		this.alpha = theAlpha;
		this.stripLength = theStripLength;
		this.minEfficiency = theMinEfficiency;
	}

	@Override
	public void init(MLTrain theTrain) {
		this.train = theTrain;
		this.calc = (MLError) train.getMethod();
		this.eOpt = Double.POSITIVE_INFINITY;
		this.stripOpt = Double.POSITIVE_INFINITY;
		this.stop = false;
		this.lastCheck = 0;
	}

	@Override
	public void preIteration() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postIteration() {
		this.lastCheck++;
		this.trainingError = this.train.getError();
		
		this.stripOpt = Math.min(this.stripOpt, this.trainingError);
		this.stripTotal += this.trainingError;
		
		if( this.lastCheck>this.stripLength ) {
			this.validationError = this.calc.calculateError(this.validationSet);
			this.testError = this.calc.calculateError(this.testSet);
			this.eOpt = Math.min(this.validationError, eOpt);
			this.gl = 100.0*((this.validationError/this.eOpt)-1.0);
			
			this.stripEfficiency = (this.stripTotal)/(this.stripLength*this.stripOpt);
			
			//System.out.println("eff=" + this.stripEfficiency + ", gl=" + this.gl);
			
			// setup for next time
			this.stripTotal = 0;
			this.lastCheck = 0;
			
			// should we stop?
			stop = (this.gl>this.alpha) || (this.stripEfficiency<this.minEfficiency);
		}
	}

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
