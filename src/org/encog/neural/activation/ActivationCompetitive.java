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
package org.encog.neural.activation;

import org.encog.neural.NeuralNetworkError;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.ActivationCompetitivePersistor;

/**
 * An activation function that only allows a specified number, usually one,  
 * of the out-bound connection to win.  These connections will share in the 
 * sim of the output, whereas the other neurons will recieve zero.
 * 
 *  This activation function can be useful for "winner take all" layers.
 *
 */
public class ActivationCompetitive extends BasicActivationFunction {

	/**
	 * The serial ID
	 */
	private static final long serialVersionUID = 5396927873082336888L;
	
	/**
	 * How many winning neurons are allowed.
	 */
	private int maxWinners = 1;

	/**
	 * Create a competitive activation function with the specified maximum
	 * number of winners.
	 * @param winners The maximum number of winners that this function supports.
	 */
	public ActivationCompetitive(int winners) {
		this.maxWinners = winners;
	}
	
	/**
	 * Create a competitive activation function with one winner allowed.
	 */
	public ActivationCompetitive() {
		this(1);
	}


	/**
	 * @return A cloned copy of this object.
	 */
	@Override
	public Object clone() {
		return new ActivationCompetitive(this.maxWinners);
	}

	/**
	 * Create a persistor for this object.
	 */
	@Override
	public Persistor createPersistor() {
		return ActivationCompetitivePersistor();
	}

	private Persistor ActivationCompetitivePersistor() {
		return new ActivationCompetitivePersistor();
	}

	public void activationFunction(double[] d) {
		boolean[] winners = new boolean[d.length];
		double sumWinners = 0;
		
		// find the desired number of winners
		for(int i=0;i<this.maxWinners;i++)
		{
			double maxFound = Double.NEGATIVE_INFINITY;
			int winner = -1;
			
			// find one winner
			for(int j=0;j<d.length;j++)
			{
				if( !winners[j] && d[j]>maxFound )
				{
					winner = j;
					maxFound = d[j];
				}
			}
			sumWinners+=maxFound;
			winners[winner] = true;
		}
		
		// adjust weights for winners and non-winners
		for(int i=0;i<d.length;i++)
		{
			if( winners[i] )
			{
				d[i] = d[i]/sumWinners;
			}
			else
			{
				d[i] = 0.0;
			}
		}
	}

	/**
	 * Implements the activation function.  The array is modified according
	 * to the activation function being used.  See the class description
	 * for more specific information on this type of activation function.
	 * @param d The input array to the activation function.
	 */
	public void derivativeFunction(double[] d) {
		throw new NeuralNetworkError(
				"Can't use the competitive activation function "
						+ "where a derivative is required.");
		
	}

	/**
	 * @return False, indication that no derivative is available for htis
	 * function.
	 */
	public boolean hasDerivative() {
		return false;
	}

	/**
	 * @return The maximum number of winners this function supports.
	 */
	public int getMaxWinners() {
		return this.maxWinners;
	}

}
