package org.encog.neural.activation;

import org.encog.EncogError;
import org.encog.persist.persistors.ActivationBiPolarPersistor;
import org.encog.persist.persistors.ActivationLOGPersistor;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestActivationLOG extends TestCase {

	@Test
	public void testLog() throws Throwable {
		ActivationLOG activation = new ActivationLOG();
		Assert.assertTrue(activation.hasDerivative());

		ActivationLOG clone = (ActivationLOG) activation.clone();
		Assert.assertNotNull(clone);

		double[] input = { 0.0  };

		activation.activationFunction(input);

		Assert.assertEquals(0.0, input[0], 0.1);		

		// this will throw an error if it does not work
		ActivationLOGPersistor p = (ActivationLOGPersistor) activation
				.createPersistor();

		// test derivative, should throw an error
		activation.derivativeFunction(input);
		Assert.assertEquals(1.0, input[0], 0.1);

		// test name and description
		// names and descriptions are not stored for these
		activation.setName("name");
		activation.setDescription("name");
		Assert.assertEquals(null, activation.getName());
		Assert.assertEquals(null, activation.getDescription());

	}

}
