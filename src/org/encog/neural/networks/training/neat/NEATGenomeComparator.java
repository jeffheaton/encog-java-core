/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training.neat;

import java.util.Comparator;

import org.encog.neural.networks.training.CalculateScore;

public class NEATGenomeComparator implements Comparator<NEATGenome> {

	private final CalculateScore calculateScore;
	
	public NEATGenomeComparator(CalculateScore calculateScore)
	{
		this.calculateScore = calculateScore;
	}
	
	public int compare(NEATGenome g1, NEATGenome g2) {
		if( this.calculateScore.shouldMinimize() )
			return Double.compare(g1.getScore(), g2.getScore());
		else
			return Double.compare(g2.getScore(), g1.getScore());
	}
	
	public double bestScore(double d1, double d2)
	{
		if( this.calculateScore.shouldMinimize())
			return Math.min(d1, d2);
		else
			return Math.max(d1, d2);
	}
	
	public double applyBonus(double value, double bonus)
	{
		double amount = value * bonus;
		if( this.calculateScore.shouldMinimize() )
			return value - amount;
		else
			return value + amount;
	}
	
	public double applyPenalty(double value, double bonus)
	{
		double amount = value * bonus;
		if( this.calculateScore.shouldMinimize() )
			return value - amount;
		else
			return value + amount;
	}

	public CalculateScore getCalculateScore() {
		return calculateScore;
	}
	
	public boolean isBetterThan(double d1, double d2)
	{
		if( this.calculateScore.shouldMinimize() )
			return d1<d2;
		else
			return d2>d1;
	}
	
	

}
