package org.encog.util;

import junit.framework.Assert;

import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.location.ResourcePersistence;
import org.encog.script.EncogScript;
import org.encog.script.basic.BasicTestConsole;

public class BasicEvaluate {
	static public String evaluate(String resource)
	{
		PersistenceLocation location = new ResourcePersistence("org/encog/data/testbasic.eg");
		EncogPersistedCollection encog = new EncogPersistedCollection(location);
		EncogScript script = (EncogScript)encog.find(resource);
		Assert.assertNotNull(script);
		BasicTestConsole console = new BasicTestConsole();
		script.load();
		script.setConsole(console);
		script.call();
		return console.toString();
	}
}
