package org.encog;

import org.encog.matrix.MatrixError;
import org.encog.neural.NeuralNetworkError;

import junit.framework.TestCase;

public class TestException extends TestCase {
	public void testExceptions()
	{
		NullPointerException npe = new NullPointerException();
		new MatrixError(npe);
		new NeuralNetworkError(npe);
	}
}
