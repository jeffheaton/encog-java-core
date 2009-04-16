/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.util.randomize;

import org.encog.EncogError;
import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A randomizer that attempts to create starting weight values that are
 * conducive to back propagation training.
 * 
 * From:
 * 
 * Neural Networks - A Comprehensive Foundation, Haykin, chapter 6.7
 * 
 * @author jheaton
 * 
 */
public class FanInRandomizer extends BasicRandomizer {

	final static String ERROR = "To use FanInRandomizer you must present a Matrix or 2D array type value."; 
	
	/** The lower bound. */
	private final double lowerBound;

	/** The upper bound. */
	private final double upperBound;

	private final boolean sqrt;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public FanInRandomizer() {
		this(-2.4,2.4,false);
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
	
	public void randomize(BasicNetwork network) {
		
		// randomize the weight matrix
		for(Synapse synapse: network.getStructure().getSynapses())
		{
			if( synapse.getMatrix()!=null )
				randomize(synapse.getMatrix());
		}
		
		// clear the thresholds
		for(Layer layer: network.getStructure().getLayers() )
		{
			for(int i=0;i<layer.getNeuronCount();i++)
			{
				layer.setThreshold(i, 0);
			}
		}
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
