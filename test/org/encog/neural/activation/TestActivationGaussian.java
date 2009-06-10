package org.encog.neural.activation;

import junit.framework.TestCase;

import org.encog.EncogError;
import org.encog.persist.persistors.ActivationBiPolarPersistor;
import org.encog.persist.persistors.ActivationGaussianPersistor;
import org.junit.Assert;
import org.junit.Test;

public class TestActivationGaussian extends TestCase {
	
	@Test
	public void testGaussian() throws Throwable
	{
		ActivationGaussian activation = new ActivationGaussian(0.0,0.5,1.0);
		Assert.assertTrue(activation.hasDerivative());
		
		ActivationGaussian clone = (ActivationGaussian)activation.clone();
		Assert.assertNotNull(clone);
		
		double[] input = { 0.0  };
		
		activation.activationFunction(input);
		
		Assert.assertEquals(0.5,input[0],0.1);
		
		// this will throw an error if it does not work
		ActivationGaussianPersistor p = (ActivationGaussianPersistor)activation.createPersistor();
		
		// test derivative, should throw an error
		
		activation.derivativeFunction(input);
		Assert.assertEquals(-33,(int)(input[0]*100),0.1);		
		
		// test name and description
		// names and descriptions are not stored for these
		activation.setName("name");
		activation.setDescription("name");
		Assert.assertEquals(null, activation.getName());
		Assert.assertEquals(null, activation.getDescription() );
		
		
	}
}
