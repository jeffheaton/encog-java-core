package org.encog.bot.browse;

import java.net.URL;

import org.junit.Assert;

import junit.framework.TestCase;

public class TestAddress extends TestCase {
	public void testAddressFunctions() throws Throwable
	{
		String a = "http://www.httprecipes.com";
		Address address = new Address(new URL(a));
		Assert.assertEquals( address.getOriginal(), a);
		
	}
}
