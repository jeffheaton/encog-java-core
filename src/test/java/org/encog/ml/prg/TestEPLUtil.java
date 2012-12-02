package org.encog.ml.prg;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.epl.EPLUtil;

public class TestEPLUtil extends TestCase {
	
	public final static int TEST_OFFSET = 4;
	
	public void testShort() {
		byte[] b = new byte[6];
		
		EPLUtil.shortToBytes((short)15, b, TEST_OFFSET);
		Assert.assertEquals(15,EPLUtil.bytesToShort(b, TEST_OFFSET));
		
		EPLUtil.shortToBytes((short)10000, b, TEST_OFFSET);
		Assert.assertEquals(10000,EPLUtil.bytesToShort(b, TEST_OFFSET));
		
	}
	
	public void testInt() {
		byte[] b = new byte[8];
		EPLUtil.intToBytes(60000, b, TEST_OFFSET);
		Assert.assertEquals(60000,EPLUtil.bytesToInt(b, TEST_OFFSET));
	}
	
	public void testLong() {
		byte[] b = new byte[12];
		EPLUtil.longToBytes(6000000, b, TEST_OFFSET);
		Assert.assertEquals(6000000,EPLUtil.bytesToLong(b, TEST_OFFSET));
	}
	
	public void testDouble() {
		byte[] b = new byte[12];
		EPLUtil.doubleToBytes(6000000.66, b, TEST_OFFSET);
		Assert.assertEquals(6000000.66,EPLUtil.bytesToDouble(b, TEST_OFFSET));
	}
}
