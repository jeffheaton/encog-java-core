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
