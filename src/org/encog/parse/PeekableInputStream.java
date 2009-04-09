/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.parse;

import java.io.IOException;
import java.io.InputStream;

import org.encog.bot.BotError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PeekableInputStream: This is a special input stream that allows the program
 * to peek one or more characters ahead in the file.
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
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

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
		try
		{
		// does the size of the peek buffer need to be extended?
		if (this.peekBytes.length <= depth) {
			final byte[] temp = new byte[depth + INITIAL_DEPTH];
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
		}
		catch(IOException e)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Exception",e);
			}
			throw new BotError(e);
		}

		return this.peekBytes[depth];
	}

	/**
	 * Read a single byte from the stream. 
	 * @return The character that was read from the stream.
	 */
	@Override
	public int read()  {
		
		try
		{
		
		if (this.peekLength == 0) {
			return this.stream.read();
		}

		final int result = this.peekBytes[0];
		this.peekLength--;
		for (int i = 0; i < this.peekLength; i++) {
			this.peekBytes[i] = this.peekBytes[i + 1];
		}

		return result;
		}
		catch(IOException e)
		{
			throw new BotError(e);
		}
	}

}
