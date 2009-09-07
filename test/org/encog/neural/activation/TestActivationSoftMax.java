package org.encog.neural.activation;

import junit.framework.TestCase;

import org.encog.EncogError;
import org.encog.persist.persistors.ActivationBiPolarPersistor;
import org.encog.persist.persistors.ActivationSoftMaxPersistor;
import org.junit.Assert;
import org.junit.Test;

public class TestActivationSoftMax extends TestCase {

	@Test
	public void testSoftMax() throws Throwable
	{
		ActivationSoftMax activation = new ActivationSoftMax();
		Assert.assertTrue(activation.hasDerivative());
		
		ActivationSoftMax clone = (ActivationSoftMax)activation.clone();
		Assert.assertNotNull(clone);
		
		double[] input = {1.0,1.0,1.0,1.0 };
		
		activation.activationFunction(input);
		
		Assert.assertEquals(0.25,input[0],0.1);
		Assert.assertEquals(0.25,input[1],0.1);
		
		// this will throw an error if it does not work
		ActivationSoftMaxPersistor p = (ActivationSoftMaxPersistor)activation.createPersistor();
		
		// test derivative
		activation.derivativeFunction(input);
		
		// test name and description
		// names and descriptions are not stored for these
		activation.setName("name");
		activation.setDescription("name");
		Assert.assertEquals(null, activation.getName());
		Assert.assertEquals(null, activation.getDescription() );
		
		
	}
	
}
