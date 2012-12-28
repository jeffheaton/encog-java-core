package org.encog.ml.prg.epl;

import java.io.Serializable;

public class EPLUtil implements Serializable {
	public static int roundToFrame(int num) {
		return (int) (Math.ceil((double) num / EPLHolder.FRAME_SIZE) * (double) EPLHolder.FRAME_SIZE);
	}

	public static void shortToBytes(short s, byte[] bytes, int offset) {
		bytes[offset] = (byte) (s >> 8);
		bytes[offset + 1] = (byte) (s /* >> 0 */);
	}

	public static short bytesToShort(byte[] bytes, int offset) {
		return (short) (((bytes[offset]&0xff) << 8) + (bytes[offset + 1]&0xff));
	}

	public static void intToBytes(int i, byte[] bytes, int offset) {
		bytes[offset] = (byte) (i >> 24);
		bytes[offset + 1] = (byte) (i >> 16);
		bytes[offset + 2] = (byte) (i >> 8);
		bytes[offset + 3] = (byte) (i /* >> 0 */);
	}

	public static int bytesToInt(byte[] bytes, int offset) {
		return (int) (((bytes[offset]&0xff) << 24) 
				+ ((bytes[offset+1]&0xff) << 16)
				+ ((bytes[offset+2]&0xff) << 8) 
				+ (bytes[offset + 3]&0xff));
	}
	
	public static void longToBytes(long l, byte[] bytes, int offset) {
		bytes[offset] = (byte) (l >> 56);
		bytes[offset + 1] = (byte) (l >> 48);
		bytes[offset + 2] = (byte) (l >> 40);
		bytes[offset + 3] = (byte) (l >> 32);
		
		bytes[offset + 4] = (byte) (l >> 24);
		bytes[offset + 5] = (byte) (l >> 16);
		bytes[offset + 6] = (byte) (l >> 8);
		bytes[offset + 7] = (byte) (l /* >> 0 */);
	}

	public static long bytesToLong(byte[] bytes, int offset) {
		return (long) 
				((((long)bytes[offset]&0xff) << 56) 
				+ (((long)bytes[offset+1]&0xff) << 48)
				+ (((long)bytes[offset+2]&0xff) << 40) 
				+ (((long)bytes[offset + 3]&0xff) <<32)
				+ (((long)bytes[offset+4]&0xff) << 24) 
				+ (((long)bytes[offset+5]&0xff) << 16)
				+ (((long)bytes[offset+6]&0xff) << 8) 
				+ ((long)bytes[offset + 7]&0xff));
	}
	
	public static void doubleToBytes(double d, byte[] bytes, int offset) {
		long l = Double.doubleToLongBits(d);
		longToBytes(l, bytes, offset);
	}

	public static double bytesToDouble(byte[] bytes, int offset) {
		long l = bytesToLong(bytes,offset);
		return Double.longBitsToDouble(l);
	}
}
