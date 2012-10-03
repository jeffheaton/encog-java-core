package org.encog.parse.expression;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBoolean extends TestCase {
	
	public void testBooleanConst() {
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("true") );
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("false") );	
	}
	
	public void testCompare() {
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("3>5") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("3<5") );
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("3=5") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("5=5") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("3<=5") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("5<=5") );
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("3>=5") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("5>=5") );
	}
	
	public void testLogic() {
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("true&true") );
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("true&false") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("true|true") );
		Assert.assertEquals( true, ExpressionHolder.parseBoolean("true|false") );
		Assert.assertEquals( false, ExpressionHolder.parseBoolean("false|false") );
	}
	
	
}
