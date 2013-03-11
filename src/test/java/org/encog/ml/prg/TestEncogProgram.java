package org.encog.ml.prg;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestEncogProgram extends TestCase {
	public void testSize() {
		EncogProgram expression = new EncogProgram("1");
		Assert.assertEquals(1, expression.getRootNode().size());
		
		expression = new EncogProgram("1+1");
		Assert.assertEquals(3, expression.getRootNode().size());
		
		expression = new EncogProgram("1+1+1");
		Assert.assertEquals(5, expression.getRootNode().size());
		
		expression = new EncogProgram("(sin(x)+cos(x))/2");
		Assert.assertEquals(7, expression.getRootNode().size());
	}
}
