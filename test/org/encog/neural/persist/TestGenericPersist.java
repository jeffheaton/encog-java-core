package org.encog.neural.persist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.encog.bot.spider.SpiderOptions;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestGenericPersist extends TestCase {
	
	private EncogPersistedObject roundTrip(EncogPersistedObject obj) throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream memory = new ByteArrayOutputStream();
		EncogPersistedCollection encog = new EncogPersistedCollection();
		encog.add(obj);
		encog.save(memory);
		memory.close();
		ByteArrayInputStream memoryInput = new ByteArrayInputStream(memory.toByteArray());
		EncogPersistedCollection encog2 = new EncogPersistedCollection();
		encog2.load(memoryInput);
		EncogPersistedObject result = encog2.getList().get(0);
		memoryInput.close();
		return result;
	}
	
	public void testGenericPersist() throws Exception
	{
		SpiderOptions spiderOptions = new SpiderOptions();
		spiderOptions.setCorePoolSize(11);
		spiderOptions.setDbClass("test");
		spiderOptions.getFilter().add("test");
		spiderOptions = (SpiderOptions)roundTrip(spiderOptions);
		Assert.assertEquals(11, spiderOptions.getCorePoolSize());
		Assert.assertTrue(spiderOptions.getDbClass().equalsIgnoreCase("test"));
		String str = spiderOptions.getFilter().get(0);
		Assert.assertTrue(str.equals("test"));
		
	}
}
