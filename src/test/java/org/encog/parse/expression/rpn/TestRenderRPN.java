package org.encog.parse.expression.rpn;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.parse.expression.rpn.RenderRPN;

public class TestRenderRPN extends TestCase {
	
	public void testRenderBasic() {
		EncogProgram expression = new EncogProgram("(2+6)");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);
		Assert.assertEquals("2 6 [+]", result);
	}
	
	public void testRenderComplex() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);
		Assert.assertEquals("a 25 [+] 3 [^] 25 [/] a 3 [*] 4 [^] 250 [/] [-]", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);		
		Assert.assertEquals("x [sin] x [cos] [+] 2 [/]", result);		
	}
	
	public void testKnownConst() {
		EncogProgram expression = new EncogProgram("x*2*PI");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);		
		Assert.assertEquals("x 2 [*] PI [*]", result);		
	}
}
