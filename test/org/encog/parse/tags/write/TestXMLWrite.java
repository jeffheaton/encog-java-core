package org.encog.parse.tags.write;

import java.io.ByteArrayOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestXMLWrite extends TestCase {
	public void testXMLWrite() throws Throwable
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		WriteXML write = new WriteXML(bos);
		write.beginDocument();
		write.addAttribute("name", "value");
		write.beginTag("tag");
		write.endTag();
		write.endDocument();
		bos.close();
		
		String str = bos.toString();
		Assert.assertEquals("<tag name=\"value\"></tag>",str);
		
	}
}
