package org.encog.ml.prg;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestEncogProgram extends TestCase {
	public void testSize() {
		EncogProgram expression = new EncogProgram("1");
		Assert.assertEquals(1, expression.size());
		
		expression = new EncogProgram("1+1");
		Assert.assertEquals(3, expression.size());
		
		expression = new EncogProgram("1+1+1");
		Assert.assertEquals(5, expression.size());
		
		expression = new EncogProgram("(sin(1)+cos(1))/2");
		Assert.assertEquals(7, expression.size());
	}
	
	public void testSimpleFindNodeStart0() {
		EncogProgram prg = new EncogProgram("1+2");
		Assert.assertEquals(0, prg.findNodeStart(2));
	}
	
	public void testSimpleFindNodeStart1() {
		EncogProgram prg = new EncogProgram("1+2");
		Assert.assertEquals(0, prg.findNodeStart(0));
	}
	
	public void testSimpleFindNodeStart2() {
		EncogProgram prg = new EncogProgram("1+2");
		Assert.assertEquals(1, prg.findNodeStart(1));
	}
	
	public void testString() {
		EncogProgram prg = new EncogProgram("abs(length(\"abc\"))");
		Assert.assertEquals("[EncogProgram: size=3, score=0.0, Code: {OpCode:sconst,p1=0,p1=3}{OpCode:length,p1=0,p1=0}{OpCode:abs,p1=0,p1=0}]",prg.toString());
		Assert.assertEquals(3, prg.evaluate().toIntValue());
	}
	
	public void testComplexFindNodeStart0() {
		EncogProgram prg = new EncogProgram("(1+2)*(3+4)");
		System.out.println(prg.toString());
		Assert.assertEquals(0, prg.findNodeStart(0));
		Assert.assertEquals(1, prg.findNodeStart(1));
		Assert.assertEquals(0, prg.findNodeStart(2));
		Assert.assertEquals(3, prg.findNodeStart(3));
		Assert.assertEquals(4, prg.findNodeStart(4));
		Assert.assertEquals(3, prg.findNodeStart(5));
	}
	
}
