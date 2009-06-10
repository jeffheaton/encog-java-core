package org.encog.neural.activation;

import junit.framework.TestCase;

import org.encog.persist.persistors.ActivationSINPersistor;
import org.encog.persist.persistors.ActivationSigmoidPersistor;
import org.junit.Assert;
import org.junit.Test;

public class TestActivationSIN extends TestCase {

	@Test
	public void testSIN() throws Throwable {
		ActivationSIN activation = new ActivationSIN();
		Assert.assertTrue(activation.hasDerivative());

		ActivationSIN clone = (ActivationSIN) activation.clone();
		Assert.assertNotNull(clone);

		double[] input = { 0.0  };

		activation.activationFunction(input);

		Assert.assertEquals(0.0, input[0], 0.1);		

		// this will throw an error if it does not work
		ActivationSINPersistor p = (ActivationSINPersistor) activation
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
