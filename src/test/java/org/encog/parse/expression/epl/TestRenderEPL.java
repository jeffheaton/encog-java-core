package org.encog.parse.expression.epl;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.parse.expression.rpn.RenderRPN;

public class TestRenderEPL extends TestCase {
	
	public void testRenderBasic() {
		EncogProgram expression = new EncogProgram("(2+6)");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);
		Assert.assertEquals("2 6 [+]", result);
	}
	
	public void testRenderComplex() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);
		Assert.assertEquals("a 25 [+] 3 [^] 25 [/] a 3 [*] 4 [^] 250 [/] [-]", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);		
		Assert.assertEquals("x [sin] x [cos] [+] 2 [/]", result);		
	}
	
	public void testKnownConst() {
		EncogProgram expression = new EncogProgram("x*2*PI");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);		
		Assert.assertEquals("x 2 [*] PI [*]", result);		
	}
}
