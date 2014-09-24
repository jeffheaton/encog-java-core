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

import org.encog.parse.PeekableInputStream;

public class TestPeekableInputStream extends TestCase {
	public void testSimple() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("test".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.read()=='t');
		TestCase.assertTrue(peek.read()=='e');
		TestCase.assertTrue(peek.read()=='s');
		TestCase.assertTrue(peek.read()=='t');
	}
	
	public void testPeek() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("test".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.peek(0)=='t');
		TestCase.assertTrue(peek.peek(1)=='e');
		TestCase.assertTrue(peek.peek(2)=='s');
		TestCase.assertTrue(peek.peek(3)=='t');
		TestCase.assertTrue(peek.read()=='t');
		TestCase.assertTrue(peek.read()=='e');
		TestCase.assertTrue(peek.read()=='s');
		TestCase.assertTrue(peek.read()=='t');
	}
	
	public void testPeekExtend() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("012345678901234567890".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.peek(0)=='0');
		TestCase.assertTrue(peek.peek(11)=='1');
		// now read past the end of file
		byte junk[] = new byte[100];
		peek.read(junk);
		TestCase.assertTrue(peek.peek()==-1);
		
	}
}
