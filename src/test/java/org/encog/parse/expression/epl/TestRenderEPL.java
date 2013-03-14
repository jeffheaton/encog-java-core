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
		Assert.assertEquals("[#const:0:2][#const:0:6][+:2]", result);
	}
	
	public void testRenderComplex() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);
		Assert.assertEquals("[#var:0:0][#const:0:25][+:2][#const:0:3][^:2][#const:0:25][/:2][#var:0:0][#const:0:3][*:2][#const:0:4][^:2][#const:0:250][/:2][-:2]", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);	
		Assert.assertEquals("[#var:0:0][sin:1][#var:0:0][cos:1][+:2][#const:0:2][/:2]", result);		
	}
}
