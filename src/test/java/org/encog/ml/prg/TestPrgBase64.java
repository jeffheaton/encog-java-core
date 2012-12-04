package org.encog.ml.prg;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPrgBase64 extends TestCase {
	public static final String ENCODED = "ADIAAAAFAAAAMgAAAAIAAAADAAAAAAAAADIAAAADAAAABwAAAAAAAAAyAAAAAgAAAAYAAAAAAAA=";
	
	public void testEncodePrg() {
		EncogProgram expression = new EncogProgram("((5+2)^3)/2");
		Assert.assertEquals(ENCODED, expression.toBase64());
	}
	
	public void testDecodePrg() {
		EncogProgram expression = new EncogProgram();
		expression.fromBase64(ENCODED);
		Assert.assertEquals("(((5+2)^3)/2)",expression.dumpAsCommonExpression());
	}
}
