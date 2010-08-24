package org.encog.script;

import junit.framework.TestCase;

public class TestJavaScript extends TestCase {

	public void testHelloWorld()
	{
		EncogScript script = new EncogScript();
		script.setSource("print(\'Hello World\')\n");
		script.run(new StandardConsole());
	}
}
