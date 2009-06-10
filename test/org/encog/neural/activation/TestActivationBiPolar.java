package org.encog.neural.activation;

import org.encog.EncogError;
import org.encog.persist.persistors.ActivationBiPolarPersistor;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestActivationBiPolar extends TestCase {
	
	@Test
	public void testBiPolar() throws Throwable
	{
		ActivationBiPolar activation = new ActivationBiPolar();
		Assert.assertFalse(activation.hasDerivative());
		
		ActivationBiPolar clone = (ActivationBiPolar)activation.clone();
		Assert.assertNotNull(clone);
		
		double[] input = { 0.5, -0.5 };
		
		activation.activationFunction(input);
		
		Assert.assertEquals(1.0,input[0],0.1);
		Assert.assertEquals(-1.0,input[1],0.1);
		
		// this will throw an error if it does not work
		ActivationBiPolarPersistor p = (ActivationBiPolarPersistor)activation.createPersistor();
		
		// test derivative, should throw an error
		try
		{
			activation.derivativeFunction(input);
			Assert.assertTrue(false);// mark an error
		}
		catch(EncogError e)
		{
			// good, this should happen
		}
		
		// test name and description
		// names and descriptions are not stored for these
		activation.setName("name");
		activation.setDescription("name");
		Assert.assertEquals(null, activation.getName());
		Assert.assertEquals(null, activation.getDescription() );
		
		
	}
}
