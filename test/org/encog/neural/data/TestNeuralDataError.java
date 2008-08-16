package org.encog.neural.data;

import java.awt.Graphics;

import junit.framework.TestCase;

public class TestNeuralDataError extends TestCase {
	public void testNeuralDataError()
	{
		try
		{
			int i=0;
			if(i==0)
				throw new NeuralDataError("test");
			TestCase.assertTrue(false);
		}
		catch(NeuralDataError e)
		{
		}
		
		try
		{
			Graphics g = null;
			g.create();
		}
		catch(NullPointerException e)
		{
			try
			{
				int i=0;
				if(i==0)
					throw new NeuralDataError(e);
				TestCase.assertTrue(false);
			}
			catch(NeuralDataError e2)
			{
				
			}
		}
	}
}
