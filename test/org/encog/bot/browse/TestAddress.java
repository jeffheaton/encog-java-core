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

package org.encog.bot.browse;

import java.net.URL;

import org.junit.Assert;

import junit.framework.TestCase;

public class TestAddress extends TestCase {
	public void testAddressFunctions() throws Throwable
	{
		String a = "http://www.httprecipes.com";
		Address address = new Address(new URL(a));
		address.toString();
		Assert.assertEquals( address.getOriginal(), a);
		Assert.assertEquals( address.getUrl().getHost(), "www.httprecipes.com");
		Address address2 = new Address(new URL(a),a);
		Assert.assertEquals( address2.getOriginal(), a);
		Address address3 = new Address(null,a);
		Assert.assertEquals( address3.getOriginal(), a);
		
	}
}
