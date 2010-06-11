/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.bot.html;

import java.io.ByteArrayInputStream;

import org.encog.parse.tags.Tag;
import org.encog.parse.tags.read.ReadHTML;

import junit.framework.TestCase;

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
		StringBuilder result = new StringBuilder();
		
		
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
