package org.encog.util.obj;

import org.encog.neural.activation.ActivationSigmoid;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestReflectionUtil extends TestCase {
	public void testResolve() {
		Class<?> c = ReflectionUtil.resolveEncogClass("ActivationSigmoid");
		Assert.assertTrue(c==ActivationSigmoid.class);
	}
}
