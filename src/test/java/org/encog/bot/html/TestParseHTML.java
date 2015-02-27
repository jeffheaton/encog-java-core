/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.bot.html;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.encog.parse.tags.Tag;
import org.encog.parse.tags.read.ReadHTML;

public class TestParseHTML extends TestCase {
	public void testAttributeLess() throws Throwable
	{
		String html = "12<b>12</b>1";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.getTag().getName().equalsIgnoreCase("b"));
		TestCase.assertTrue(parse.getTag().getType()==Tag.Type.BEGIN);
		TestCase.assertTrue(parse.read()=='1');
		TestCase.assertTrue(parse.read()=='2');
		TestCase.assertTrue(parse.read()==0);
		Tag tag = parse.getTag();
		TestCase.assertTrue(tag.getName().equalsIgnoreCase("b"));
		TestCase.assertTrue(tag.getType()==Tag.Type.END);
		TestCase.assertEquals(tag.toString(),"</b>");
		TestCase.assertTrue(parse.read()=='1');
	}
	
	public void testAttributes() throws Throwable
	{
		String html="<img src=\"picture.gif\" alt=\"A Picture\">";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		Tag tag = parse.getTag();
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
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		Tag tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getName().equals("img"));
		TestCase.assertTrue(tag.getAttributeValue("src").equals("picture.gif"));
		TestCase.assertTrue(tag.getAttributeValue("alt").equals("APicture"));
	}
	
	public void testBoth() throws Throwable
	{
		String html="<br/>";
		String htmlName = "br";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		Tag tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getType()==Tag.Type.BEGIN);
		TestCase.assertTrue(tag.getName().equals(htmlName));
		parse.readToTag();
		tag = parse.getTag();
		TestCase.assertNotNull(tag);
		TestCase.assertTrue(tag.getType()==Tag.Type.END);
		TestCase.assertTrue(tag.getName().equals(htmlName));
	}
	
	public void testBothWithAttributes() throws Throwable
	{
		String html="<img src=\"picture.gif\" alt=\"A Picture\"/>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);
	}
	
	public void testComment() throws Throwable
	{
		String html="a<!-- Hello -->b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()=='a');
		TestCase.assertTrue(parse.read()==0);
		TestCase.assertTrue(parse.read()=='b');
	}
	
	public void testScript() throws Throwable
	{
		String html="a<script>12</script>b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
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
		ReadHTML parse = new ReadHTML(bis);
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
		String html="a<img src=\"picture.gif\" alt=\"A Picture\">b";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		parse.readToTag();	
		TestCase.assertTrue(parse.toString().indexOf("A Picture")!=-1);
	}
	
	public void testTagToString()
	{
		String html="<br/>";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);	
	}
	
	public void testSpecialCharacter() throws Throwable
	{
		String html = "&lt;&gt;&#65;";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()=='<');
		TestCase.assertTrue(parse.read()=='>');
		TestCase.assertTrue(parse.read()=='A');
	}
	
	public void testSimpleAttribute() throws Throwable
	{
		String html = "<!DOCTYPE \"test\">";
		ByteArrayInputStream bis = new ByteArrayInputStream(html.getBytes());
		ReadHTML parse = new ReadHTML(bis);
		TestCase.assertTrue(parse.read()==0);
		Tag tag = parse.getTag();
		TestCase.assertEquals(tag.toString(), html);
	}
}
