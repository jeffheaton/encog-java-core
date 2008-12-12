package org.encog.bot.html;

import java.io.ByteArrayInputStream;
import junit.framework.TestCase;

public class TestParseHTML extends TestCase {
	public void testAttributeLess() throws Throwable
	{
		String html = "12<b>12</b>1";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.getTag().getName().equalsIgnoreCase("b"));
		TestCase.assertTrue(parse.getTag().getType()==HTMLTag.Type.BEGIN);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		HTMLTag tag = parse.getTag();
		TestCase.assertTrue(tag.getName().equalsIgnoreCase("b"));
		TestCase.assertTrue(tag.getType()==HTMLTag.Type.END);
		TestCase.assertEquals(tag.toString(),"</b>");
		TestCase.assertTrue(parse.read()=='1');
	}
	
	public void testAttributes() throws Throwable
	{
		String html="<img src=\"picture.gif\" alt=\"A Picture\">";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		HTMLTag tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getName().equals("img"));
		//TestCase.assertTrue(html.equals(tag.toString()));
		TestCase.assertTrue(tag.getAttributeValue("src").equals("picture.gif"));
		TestCase.assertTrue(tag.getAttributeValue("alt").equals("A Picture"));
	}
	
	public void testAttributesNoDELIM() throws Throwable
	{
		String html="<img src=picture.gif alt=APicture>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		HTMLTag tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getName().equals("img"));
		TestCase.assertTrue(tag.getAttributeValue("src").equals("picture.gif"));
		TestCase.assertTrue(tag.getAttributeValue("alt").equals("APicture"));
	}
	
	public void testBoth() throws Throwable
	{
		String html="<br/>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		HTMLTag tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getType()==HTMLTag.Type.BOTH);
		TestCase.assertTrue(tag.toString().equals(html));
	}
	
	public void testBothWithAttributes() throws Throwable
	{
		String html="<img src=\"picture.gif\" alt=\"A Picture\"/>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()==0);
	}
	
	public void testComment() throws Throwable
	{
		String html="a<!-- Hello -->b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='a');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='b');
	}
	
	public void testScript() throws Throwable
	{
		String html="a<script>12</script>b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='a');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='b');	
	}
	
	public void testScript2() throws Throwable
	{
		String html="a<script>1<2</script>b<br>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='a');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='<');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='b');	
		TestCase.assertTrue(parse.read()==0);
	}	
	
	public void testToString()
	{
		StringBuilder result = new StringBuilder();
		
		
		String html="a<img src=\"picture.gif\" alt=\"A Picture\">b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.toString().indexOf("Text:a")!=-1);
	}
	
	public void testTagToString()
	{
		/*String html="<br/>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='a');	*/
	}
	
	public void testSpecialCharacter() throws Throwable
	{
		String html = "&lt;&gt;&#65;";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()=='<');
		TestCase.assertTrue(parse.read()=='>');
		TestCase.assertTrue(parse.read()=='A');
	}
	
	public void testSimpleAttribute() throws Throwable
	{
		String html = "<!DOCTYPE \"test\">";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ParseHTML parse = new ParseHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		HTMLTag tag = parse.getTag();
		TestCase.assertEquals(tag.toString(), html);
	}
}
