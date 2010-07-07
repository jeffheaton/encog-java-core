package org.encog.script.basic;

import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.location.ResourcePersistence;
import org.encog.script.EncogScript;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBasicExecution extends TestCase {

	public void testPrint()
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/testbasic.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		EncogScript script = (EncogScript)encog.find("test-print");
		Assert.assertNotNull(script);
		BasicTestConsole console = new BasicTestConsole();
		script.load();
		script.setConsole(console);
		script.call();
		Assert.assertEquals("test", console.toString());
	}
	
	public void testExpr()
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/testbasic.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		EncogScript script = (EncogScript)encog.find("test-expr");
		Assert.assertNotNull(script);
		BasicTestConsole console = new BasicTestConsole();
		script.load();
		script.setConsole(console);
		script.call();
		Assert.assertEquals("140.0", console.toString());
	}
	
	public void testIf()
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/testbasic.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		EncogScript script = (EncogScript)encog.find("test-if");
		Assert.assertNotNull(script);
		BasicTestConsole console = new BasicTestConsole();
		script.load();
		script.setConsole(console);
		script.call();
		Assert.assertEquals("testelse1test3", console.toString());
	}
	
}
