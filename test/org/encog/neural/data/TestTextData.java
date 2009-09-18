package org.encog.neural.data;

import org.encog.persist.persistors.PropertyDataPersistor;
import org.encog.persist.persistors.TextDataPersistor;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestTextData extends TestCase {

	public void testClone() throws Exception
	{
		TextData data = new TextData();
		data.setName("name");
		data.setDescription("description");
		data.setText("text");
		TextData data2 = (TextData)data.clone();
		Assert.assertEquals("name",data2.getName());
		Assert.assertEquals("description",data2.getDescription());
		Assert.assertEquals("text",data2.getText());
		Assert.assertTrue(data.createPersistor() instanceof TextDataPersistor);
	}
}
