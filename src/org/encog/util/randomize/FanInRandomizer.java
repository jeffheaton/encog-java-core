package org.encog.util.randomize;

import org.encog.EncogError;
import org.encog.matrix.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * From:
 * 
 * Neural Networks - A Comprehensive Foundation, Haykin, chapter 6.7
 * 
 * @author jheaton
 * 
 */
public class FanInRandomizer extends BasicRandomizer {

	final static String ERROR = "To use FanInRandomizer you must present a Matrix or 2D array type value."; 
	final Logger logger = LoggerFactory.getLogger(FanInRandomizer.class);
	
	/** The lower bound. */
	private final double lowerBound;

	/** The upper bound. */
	private final double upperBound;

	private final boolean sqrt;

	public FanInRandomizer() {
		this(2.4,2.4,false);
	}

	public FanInRandomizer(double boundary, boolean sqrt) {
		this(-boundary,boundary,sqrt);
		
	}

	public FanInRandomizer(double aLowerBound, double anUpperBound, boolean sqrt) {
		this.lowerBound = aLowerBound;
		this.upperBound = anUpperBound;
		this.sqrt = sqrt;
	}
	
	private void causeError()
	{
		if( logger.isErrorEnabled() )
		{
			logger.error(FanInRandomizer.ERROR);			
		}	
		throw new EncogError(FanInRandomizer.ERROR);
	}

	@Override
	public double randomize(double d) {		
		causeError();
		return 0;
	}

	@Override
	public void randomize(double[] d) {
		causeError();
	}

	@Override
	public void randomize(Double[] d) {
		causeError();
	}

	@Override
	public void randomize(double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}

	@Override
	public void randomize(Double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}
	
	private double calculateValue(int rows)
	{
		double rowValue;
		
		if(this.sqrt )			
			rowValue = Math.sqrt((double) rows);
		else
			rowValue = (double) rows;
		
		return (lowerBound / rowValue)
				+ Math.random()
				* ((upperBound - lowerBound) / rowValue );
	}

	@Override
	public void randomize(Matrix m) {
		for (int row = 0; row < m.getRows(); row++) {
			for (int col = 0; col < m.getCols(); col++) {
				m.set(row, col, calculateValue(m.getRows()));
			}
		}
	}

}
