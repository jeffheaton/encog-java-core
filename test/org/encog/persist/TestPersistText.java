package org.encog.persist;

import org.encog.neural.art.ART1;
import org.encog.neural.data.TextData;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPersistText extends TestCase {
	
	public static final String TEST_TEXT = "Hello World";
	
	public final String EG_FILENAME = "encogtest.eg";
	public final String EG_RESOURCE = "test";
	public final String SERIAL_FILENAME = "encogtest.ser";
	
	public void testText()
	{
		TextData data =new TextData();
		data.setText(TEST_TEXT);
		
		EncogMemoryCollection encog = new EncogMemoryCollection();
		encog.add(EG_RESOURCE, data);
		encog.save(EG_FILENAME);
		
		EncogMemoryCollection encog2 = new EncogMemoryCollection();
		encog2.load(EG_FILENAME);
		TextData data2 = (TextData)encog2.find(EG_RESOURCE);
		Assert.assertEquals(TEST_TEXT, data2.getText());
		
	}
}
