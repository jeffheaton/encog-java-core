package org.encog.util;

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
