package org.encog.ml.prg.epl;

import java.io.Serializable;

public class EPLUtil implements Serializable {
	public static double bytesToDouble(final byte[] bytes, final int offset) {
		final long l = bytesToLong(bytes, offset);
		return Double.longBitsToDouble(l);
	}

	public static int bytesToInt(final byte[] bytes, final int offset) {
		return ((bytes[offset] & 0xff) << 24)
				+ ((bytes[offset + 1] & 0xff) << 16)
				+ ((bytes[offset + 2] & 0xff) << 8)
				+ (bytes[offset + 3] & 0xff);
	}

	public static long bytesToLong(final byte[] bytes, final int offset) {
		return (((long) bytes[offset] & 0xff) << 56)
				+ (((long) bytes[offset + 1] & 0xff) << 48)
				+ (((long) bytes[offset + 2] & 0xff) << 40)
				+ (((long) bytes[offset + 3] & 0xff) << 32)
				+ (((long) bytes[offset + 4] & 0xff) << 24)
				+ (((long) bytes[offset + 5] & 0xff) << 16)
				+ (((long) bytes[offset + 6] & 0xff) << 8)
				+ ((long) bytes[offset + 7] & 0xff);
	}

	public static short bytesToShort(final byte[] bytes, final int offset) {
		return (short) (((bytes[offset] & 0xff) << 8) + (bytes[offset + 1] & 0xff));
	}

	public static void doubleToBytes(final double d, final byte[] bytes,
			final int offset) {
		final long l = Double.doubleToLongBits(d);
		longToBytes(l, bytes, offset);
	}

	public static void intToBytes(final int i, final byte[] bytes,
			final int offset) {
		bytes[offset] = (byte) (i >> 24);
		bytes[offset + 1] = (byte) (i >> 16);
		bytes[offset + 2] = (byte) (i >> 8);
		bytes[offset + 3] = (byte) i;
	}

	public static void longToBytes(final long l, final byte[] bytes,
			final int offset) {
		bytes[offset] = (byte) (l >> 56);
		bytes[offset + 1] = (byte) (l >> 48);
		bytes[offset + 2] = (byte) (l >> 40);
		bytes[offset + 3] = (byte) (l >> 32);

		bytes[offset + 4] = (byte) (l >> 24);
		bytes[offset + 5] = (byte) (l >> 16);
		bytes[offset + 6] = (byte) (l >> 8);
		bytes[offset + 7] = (byte) l;
	}

	public static int roundToFrame(final int num) {
		return (int) (Math.ceil((double) num / EPLHolder.FRAME_SIZE) * EPLHolder.FRAME_SIZE);
	}

	public static void shortToBytes(final short s, final byte[] bytes,
			final int offset) {
		bytes[offset] = (byte) (s >> 8);
		bytes[offset + 1] = (byte) s;
	}
}
