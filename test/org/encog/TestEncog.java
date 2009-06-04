package org.encog;

import java.util.Map;

import junit.framework.TestCase;

import org.encog.util.orm.ORMSession;
import org.junit.Assert;
import org.junit.Test;

public class TestEncog extends TestCase {
	
	@Test
	public void testProperties() throws Throwable {
		Encog instance1 = Encog.getInstance();
		Encog instance2 = Encog.getInstance();
		Assert.assertSame(instance1, instance2);
		Map<String, String> map = instance1.getProperties();
		Assert.assertNotNull(map);
		ORMSession session = instance1.getSession();
		instance1.setSession(null);
		Assert.assertNull(session);
	}
}
