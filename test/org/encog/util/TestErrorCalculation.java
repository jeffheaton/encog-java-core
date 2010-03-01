/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.util;

import org.encog.mathutil.error.ErrorCalculation;

import junit.framework.TestCase;

public class TestErrorCalculation extends TestCase {

	public void testErrorCalculation()
	{
		double ideal[][] = {
				{1,2,3,4},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16} };
		
		double actual_good[][] = {
				{1,2,3,4},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16} };
		
		double actual_bad[][] = {
				{1,2,3,5},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16} };
		
		ErrorCalculation error = new ErrorCalculation();
		
		for(int i=0;i<ideal.length;i++)
		{
			error.updateError(actual_good[i], ideal[i]);
		}
		TestCase.assertEquals(0.0,error.calculateRMS());
		
		error.reset();
		
		for(int i=0;i<ideal.length;i++)
		{
			error.updateError(actual_bad[i], ideal[i]);
		}
		TestCase.assertEquals(250,(int)(error.calculateRMS()*1000));
		
	}
}
