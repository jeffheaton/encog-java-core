package org.encog;

import junit.framework.TestCase;

import org.encog.matrix.MatrixError;
import org.encog.neural.NeuralNetworkError;

public class TestException extends TestCase {
	public void testExceptions()
	{
		NullPointerException npe = new NullPointerException();
		new MatrixError(npe);
		new NeuralNetworkError(npe);
	}
}
