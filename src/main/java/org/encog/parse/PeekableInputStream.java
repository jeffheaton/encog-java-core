/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.parse;

import java.io.IOException;
import java.io.InputStream;

import org.encog.bot.BotError;

/**
 * This is a special input stream that allows the program to peek one or more
 * characters ahead in the file.
 */
public class PeekableInputStream extends InputStream {

	/**
	 * The depth to peek.
	 */
	public static final int INITIAL_DEPTH = 10;

	/**
	 * The underlying stream.
	 */
	private final InputStream stream;

	/**
	 * Bytes that have been peeked at.
	 */
	private byte[] peekBytes;

	/**
	 * How many bytes have been peeked at.
	 */
	private int peekLength;

	/**
	 * The constructor accepts an InputStream to setup the object.
	 * 
	 * @param is
	 *            The InputStream to parse.
	 */
	public PeekableInputStream(final InputStream is) {
		this.stream = is;
		this.peekBytes = new byte[PeekableInputStream.INITIAL_DEPTH];
		this.peekLength = 0;
	}

	/**
	 * Peek at the next character from the stream.
	 * 
	 * @return The next character.
	 */
	public int peek() {
		return peek(0);
	}

	/**
	 * Peek at a specified depth.
	 * 
	 * @param depth
	 *            The depth to check.
	 * @return The character peeked at.
	 */
	public int peek(final int depth) {
		try {
			// does the size of the peek buffer need to be extended?
			if (this.peekBytes.length <= depth) {
				final byte[] temp = new byte[depth
						+ PeekableInputStream.INITIAL_DEPTH];
				for (int i = 0; i < this.peekBytes.length; i++) {
					temp[i] = this.peekBytes[i];
				}
				this.peekBytes = temp;
			}

			// does more data need to be read?
			if (depth >= this.peekLength) {
				final int offset = this.peekLength;
				final int length = depth - this.peekLength + 1;
				final int lengthRead = this.stream.read(this.peekBytes, offset,
						length);

				if (lengthRead == -1) {
					return -1;
				}

				this.peekLength = depth + 1;
			}
		} catch (final IOException e) {
			throw new BotError(e);
		}

		return this.peekBytes[depth];
	}

	/**
	 * Peek ahead and see if the specified string is present.
	 * 
	 * @param str
	 *            The string we are looking for.
	 * @return True if the string was found.
	 */
	public boolean peek(final String str) {
		for (int i = 0; i < str.length(); i++) {
			if (peek(i) != str.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Read a single byte from the stream.
	 * 
	 * @return The character that was read from the stream.
	 */
	@Override
	public int read() {

		try {

			if (this.peekLength == 0) {
				return this.stream.read();
			}

			final int result = this.peekBytes[0];
			this.peekLength--;
			for (int i = 0; i < this.peekLength; i++) {
				this.peekBytes[i] = this.peekBytes[i + 1];
			}

			return result;
		} catch (final IOException e) {
			throw new BotError(e);
		}
	}

	/**
	 * Skip the specified number of bytes.
	 * 
	 * @param count
	 *            The number of bytes to skip.
	 * @return The actual number of bytes skipped.
	 */
	@Override
	public long skip(final long count) {
		long count2 = count;
		while (count2 > 0) {
			this.read();
			count2--;
		}
		return count;
	}

}
