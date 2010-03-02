/*
 * Encog(tm) Core v2.4
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

import org.encog.parse.PeekableInputStream;

import junit.framework.TestCase;

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
