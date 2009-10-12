package org.encog.util.file;

import java.io.RandomAccessFile;

/**
 * @author Jeff Heaton(http://www.jeffheaton.com)
 * @version 1.0
 */
public class BinaryFile {

	/**
	 * Are numbers big or little endian?
	 */
	public enum EndianType {
		BIG_ENDIAN, LITTLE_ENDIAN
	}

	/**
	 * The underlying file.
	 */
	protected RandomAccessFile file;

	/**
	 * Are we in LITTLE_ENDIAN or BIG_ENDIAN mode.
	 */
	protected EndianType endian;

	/**
	 * Are we reading signed or unsigned numbers.
	 */
	protected boolean signed;

	/**
	 * The constructor. Use to specify the underlying file.
	 * 
	 * @param f
	 *            The file to read/write from/to.
	 */
	public BinaryFile(final RandomAccessFile f) {
		this.file = f;
		this.endian = EndianType.LITTLE_ENDIAN;
		this.signed = false;
	}

	/**
	 * Allows the file to be aligned to a specified byte boundary. For example,
	 * if a 4(double word) is specified, the file pointer will be moved to the
	 * next double word boundary.
	 * 
	 * @param a
	 *            The byte-boundary to align to.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void align(final int a) throws java.io.IOException {
		if ((this.file.getFilePointer() % a) > 0) {
			final long pos = this.file.getFilePointer() / a;
			this.file.seek((pos + 1) * a);
		}
	}

	/**
	 * Returns the endian mode. Will be either BIG_ENDIAN or LITTLE_ENDIAN.
	 * 
	 * @return BIG_ENDIAN or LITTLE_ENDIAN to specify the current endian mode.
	 */
	public EndianType getEndian() {
		return this.endian;
	}

	/**
	 * Returns the signed mode.
	 * 
	 * @return Returns true for signed, false for unsigned.
	 */
	public boolean getSigned() {
		return this.signed;
	}

	/**
	 * Reads an 8-bit byte. Can be signed or unsigned depending on the signed
	 * property.
	 * 
	 * @return A byte stored in a short.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public short readByte() throws java.io.IOException {
		if (this.signed) {
			return this.file.readByte();
		} else {
			return (short) this.file.readUnsignedByte();
		}
	}

	/**
	 * Reads a 32-bit double word. Can be signed or unsigned depending on the
	 * signed property. Can be little or big endian depending on the endian
	 * property.
	 * 
	 * @return A double world stored in a long.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public long readDWord() throws java.io.IOException {
		short a, b, c, d;
		long result;

		a = readUnsignedByte();
		b = readUnsignedByte();
		c = readUnsignedByte();
		d = readUnsignedByte();

		if (this.endian == EndianType.BIG_ENDIAN) {
			result = ((a << 24) | (b << 16) | (c << 8) | d);
		} else {
			result = (a | (b << 8) | (c << 16) | (d << 24));
		}

		if (this.signed) {
			if ((result & 0x80000000L) == 0x80000000L) {
				result = -(0x100000000L - result);
			}
		}

		return result;
	}

	/**
	 * Reads a fixed length ASCII string.
	 * 
	 * @param length
	 *            How long of a string to read.
	 * @return The number of bytes read.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public String readFixedString(final int length) throws java.io.IOException {
		String rtn = "";

		for (int i = 0; i < length; i++) {
			rtn += (char) this.file.readByte();
		}
		return rtn;
	}

	/**
	 * Reads a fixed length string that is zero(NULL) terminated. This is a type
	 * of string used by C/C++. For example char str[80].
	 * 
	 * @param length
	 *            The length of the string.
	 * @return The string that was read.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public String readFixedZeroString(final int length)
			throws java.io.IOException {
		String rtn = readFixedString(length);
		final int i = rtn.indexOf(0);
		if (i != -1) {
			rtn = rtn.substring(0, i);
		}
		return rtn;
	}

	/**
	 * Reads a string that stores one length byte before the string. This string
	 * can be up to 255 characters long. Pascal stores strings this way.
	 * 
	 * @return The string that was read.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public String readLengthPrefixString() throws java.io.IOException {
		final short len = readUnsignedByte();
		return readFixedString(len);
	}

	/**
	 * Internal function used to read an unsigned byte. External classes should
	 * use the readByte function.
	 * 
	 * @return The byte, unsigned, as a short.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	protected short readUnsignedByte() throws java.io.IOException {
		return (short) (this.file.readByte() & 0xff);
	}

	/**
	 * Reads a 16-bit word. Can be signed or unsigned depending on the signed
	 * property. Can be little or big endian depending on the endian property.
	 * 
	 * @return A word stored in an int.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public int readWord() throws java.io.IOException {
		short a, b;
		int result;

		a = readUnsignedByte();
		b = readUnsignedByte();

		if (this.endian == EndianType.BIG_ENDIAN) {
			result = ((a << 8) | b);
		} else {
			result = (a | (b << 8));
		}

		if (this.signed) {
			if ((result & 0x8000) == 0x8000) {
				result = -(0x10000 - result);
			}
		}

		return result;
	}

	/**
	 * Reads an unlimited length zero(null) terminated string.
	 * 
	 * @return The string that was read.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public String readZeroString() throws java.io.IOException {
		String rtn = "";
		char ch;

		do {
			ch = (char) this.file.read();
			if (ch != 0) {
				rtn += ch;
			}
		} while (ch != 0);
		return rtn;
	}

	/**
	 * Set the endian mode for reading integers.
	 * 
	 * @param i
	 *            Specify either LITTLE_ENDIAN or BIG_ENDIAN.
	 */
	public void setEndian(final EndianType i) throws Exception {
		this.endian = i;
	}

	/**
	 * Sets the signed or unsigned mode for integers. true for signed, false for
	 * unsigned.
	 * 
	 * @param b
	 *            True if numbers are to be read/written as signed, false if
	 *            unsigned.
	 */
	public void setSigned(final boolean b) {
		this.signed = b;
	}

	/**
	 * Writes a single byte to the file.
	 * 
	 * @param b
	 *            The byte to be written.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeByte(final short b) throws java.io.IOException {
		this.file.write(b & 0xff);
	}

	/**
	 * Writes a double word to the file.
	 * 
	 * @param d
	 *            The double word to be written to the file.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeDWord(final long d) throws java.io.IOException {
		if (this.endian == EndianType.BIG_ENDIAN) {
			this.file.write((int) (d & 0xff000000) >> 24);
			this.file.write((int) (d & 0xff0000) >> 16);
			this.file.write((int) (d & 0xff00) >> 8);
			this.file.write((int) (d & 0xff));
		} else {
			this.file.write((int) (d & 0xff));
			this.file.write((int) (d & 0xff00) >> 8);
			this.file.write((int) (d & 0xff0000) >> 16);
			this.file.write((int) (d & 0xff000000) >> 24);
		}
	}

	/**
	 * Writes a fixed length ASCII string. Will truncate the string if it does
	 * not fit in the specified buffer.
	 * 
	 * @param str
	 *            The string to be written.
	 * @param length
	 *            The length of the area to write to. Should be larger than the
	 *            length of the string being written.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeFixedString(String str, final int length)
			throws java.io.IOException {
		int i;

		// trim the string back some if needed

		if (str.length() > length) {
			str = str.substring(0, length);
		}

		// write the string

		for (i = 0; i < str.length(); i++) {
			this.file.write(str.charAt(i));
		}

		// buffer extra space if needed

		i = length - str.length();
		while ((i--) > 0) {
			this.file.write(0);
		}
	}

	/**
	 * Writes a fixed length string that is zero terminated. This is the format
	 * generally used by C/C++ for string storage.
	 * 
	 * @param str
	 *            The string to be written.
	 * @param length
	 *            The length of the buffer to receive the string.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeFixedZeroString(final String str, final int length)
			throws java.io.IOException {
		writeFixedString(str, length);
	}

	/**
	 * Writes a string that is prefixed by a single byte that specifies the
	 * length of the string. This is how Pascal usually stores strings.
	 * 
	 * @param str
	 *            The string to be written.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeLengthPrefixString(final String str)
			throws java.io.IOException {
		writeByte((byte) str.length());
		for (int i = 0; i < str.length(); i++) {
			this.file.write(str.charAt(i));
		}
	}

	/**
	 * Write a word to the file.
	 * 
	 * @param w
	 *            The word to be written to the file.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */

	public void writeWord(final int w) throws java.io.IOException {
		if (this.endian == EndianType.BIG_ENDIAN) {
			this.file.write((w & 0xff00) >> 8);
			this.file.write(w & 0xff);
		} else {
			this.file.write(w & 0xff);
			this.file.write((w & 0xff00) >> 8);
		}
	}

	/**
	 * Writes an unlimited zero(NULL) terminated string to the file.
	 * 
	 * @param str
	 *            The string to be written.
	 * @exception java.io.IOException
	 *                If an IO exception occurs.
	 */
	public void writeZeroString(final String str) throws java.io.IOException {
		for (int i = 0; i < str.length(); i++) {
			this.file.write(str.charAt(i));
		}
		writeByte((byte) 0);
	}
}
