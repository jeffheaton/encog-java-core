package org.encog.script.basic;

import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.location.ResourcePersistence;
import org.encog.script.EncogScript;
import org.encog.util.BasicEvaluate;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBasicExecution extends TestCase {

	public void testPrint()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-print");
		Assert.assertEquals("test", str);
	}
	
	public void testExpr()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-expr");
		Assert.assertEquals("140.0", str);
	}
	
	public void testIf()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-if");
		Assert.assertEquals("testelse1test3", str);
	}
	
	public void testVar()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-var");
		Assert.assertEquals("2.0test", str);
	}
	
	public void testCase()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-case");
		Assert.assertEquals("aabcd", str);
	}
	
	public void testGoto()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-goto");
		Assert.assertEquals("b", str);
	}
	
	public void testError()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-onerror");
		Assert.assertEquals("error", str);
	}
}
