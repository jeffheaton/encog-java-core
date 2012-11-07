package org.encog.parse.expression;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestRender extends TestCase {
	public void testRenderBasic() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderCommonExpression render = new RenderCommonExpression();
		String result = render.render(expression);
		Assert.assertEquals("((((a+25)^3)/25)-(((a*3)^4)/250))", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderCommonExpression render = new RenderCommonExpression();
		String result = render.render(expression);		
		Assert.assertEquals("((sin(x)+cos(x))/2)", result);		
	}
	
	public void testKnownConst() {
		EncogProgram expression = new EncogProgram("x*2*PI");
		RenderCommonExpression render = new RenderCommonExpression();
		String result = render.render(expression);		
		Assert.assertEquals("(x*(2*PI))", result);		
	}
}
