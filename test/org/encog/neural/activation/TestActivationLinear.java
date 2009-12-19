package org.encog.neural.activation;

import junit.framework.TestCase;

import org.encog.EncogError;
import org.encog.persist.persistors.ActivationBiPolarPersistor;
import org.encog.persist.persistors.ActivationLinearPersistor;
import org.junit.Assert;
import org.junit.Test;

public class TestActivationLinear extends TestCase {
	@Test
	public void testLinear() throws Throwable
	{
		ActivationLinear activation = new ActivationLinear();
		Assert.assertTrue(activation.hasDerivative());
		
		ActivationLinear clone = (ActivationLinear)activation.clone();
		Assert.assertNotNull(clone);
		
		double[] input = { 1,2,3 };
		
		activation.activationFunction(input);
		
		Assert.assertEquals(1.0,input[0],0.1);
		Assert.assertEquals(2.0,input[1],0.1);
		Assert.assertEquals(3.0,input[2],0.1);
		
		// this will throw an error if it does not work
		ActivationLinearPersistor p = (ActivationLinearPersistor)activation.createPersistor();
		
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
