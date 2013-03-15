package org.encog.ml.prg.species;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;
import org.encog.ml.prg.EncogProgram;

public class TestCompareEncogProgram extends TestCase {
	
	public double eval(String prg1, String prg2) {
		EncogProgram expression1 = new EncogProgram(prg1);
		EncogProgram expression2 = new EncogProgram(prg2);
		CompareEncogProgram comp = new CompareEncogProgram();
		return comp.compare(expression1, expression2);
	}
	
	
	public void testSingle() {
		Assert.assertEquals(2.0, eval("1+x","x+1"), Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
