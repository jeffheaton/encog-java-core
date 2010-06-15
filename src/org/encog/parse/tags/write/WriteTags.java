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

package org.encog.parse.tags.write;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.encog.EncogError;
import org.encog.neural.NeuralNetworkError;
import org.encog.parse.ParseError;
import org.encog.parse.tags.TagConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to write out tags, such as XML or HTML.
 * 
 * @author jheaton
 */
public class WriteTags {

	/**
	 * The output stream to write to.
	 */
	private final OutputStream output;

	/**
	 * Stack to keep track of beginning and ending tags.
	 */
	private final Stack<String> tagStack;

	/**
	 * The attributes for the current tag.
	 */
	private final Map<String, String> attributes;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct an object to write tags.
	 * 
	 * @param output
	 *            THe output stream.
	 */
	public WriteTags(final OutputStream output) {
		this.output = output;
		this.tagStack = new Stack<String>();
		this.attributes = new HashMap<String, String>();
	}

	/**
	 * Add an attribute to be written with the next tag.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 */
	public void addAttribute(final String name, final String value) {
		this.attributes.put(name, value);
	}

	/**
	 * Add CDATA to the output stream. XML allows a large block of unformatted
	 * text to be added as a CDATA tag.
	 * 
	 * @param text
	 *            The text to add.
	 */
	public void addCDATA(final String text) {
		final StringBuilder builder = new StringBuilder();
		builder.append('<');
		builder.append(TagConst.CDATA_BEGIN);
		builder.append(text);
		builder.append(TagConst.CDATA_END);
		builder.append('>');
		try {
			this.output.write(builder.toString().getBytes());
		} catch (final IOException e) {
			throw new ParseError(e);
		}
	}

	/**
	 * Add a property as a double. A property is a value enclosed in two tags.
	 * 
	 * @param name
	 *            The name of the enclosing tags.
	 * @param d
	 *            The value to store.
	 */
	public void addProperty(final String name, final double d) {
		beginTag(name);
		addText("" + d);
		endTag();
	}

	/**
	 * Add a property as an integer. A property is a value enclosed in two tags.
	 * 
	 * @param name
	 *            The name of the enclosing tags.
	 * @param i
	 *            The value to store.
	 */
	public void addProperty(final String name, final int i) {
		addProperty(name, "" + i);

	}

	/**
	 * Add a property as a string. A property is a value enclosed in two tags.
	 * 
	 * @param name
	 *            The name of the enclosing tags.
	 * @param str
	 *            The value to store.
	 */
	public void addProperty(final String name, final String str) {
		beginTag(name);
		addText(str);
		endTag();
	}

	public void addProperty(final String name, final double[] array, int len) {
		if (array != null) {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < len; i++) {
				if (i != 0)
					str.append(' ');
				str.append(array[i]);
			}
			addProperty("label", str.toString());
		}
	}
	
	public void addProperty(final String name, final int[] array, int len) {
		if (array != null) {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < len; i++) {
				if (i != 0)
					str.append(' ');
				str.append(array[i]);
			}
			addProperty("label", str.toString());
		}
	}

	/**
	 * Add text.
	 * 
	 * @param text
	 *            The text to add.
	 */
	public void addText(final String text) {
		try {
			this.output.write(text.getBytes());
		} catch (final IOException e) {
			throw new ParseError(e);
		}
	}

	/**
	 * Called to begin the document.
	 */
	public void beginDocument() {
	}

	/**
	 * Begin a tag with the specified name.
	 * 
	 * @param name
	 *            The tag to begin.
	 */
	public void beginTag(final String name) {
		final StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(name);
		if (this.attributes.size() > 0) {
			for (final String key : this.attributes.keySet()) {
				final String value = this.attributes.get(key);
				builder.append(' ');
				builder.append(key);
				builder.append('=');
				builder.append("\"");
				builder.append(value);
				builder.append("\"");
			}
		}
		builder.append(">");

		try {
			this.output.write(builder.toString().getBytes());
		} catch (final IOException e) {
			throw new ParseError(e);
		}
		this.attributes.clear();
		this.tagStack.push(name);
	}

	/**
	 * Close this object.
	 */
	public void close() {
		try {
			this.output.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * End the document.
	 */
	public void endDocument() {
	}

	/**
	 * End the current tag.
	 */
	public void endTag() {
		if (this.tagStack.isEmpty()) {
			throw new NeuralNetworkError(
					"Can't create end tag, no beginning tag.");
		}
		final String tag = this.tagStack.pop();

		final StringBuilder builder = new StringBuilder();
		builder.append("</");
		builder.append(tag);
		builder.append(">");

		try {
			this.output.write(builder.toString().getBytes());
		} catch (final IOException e) {
			throw new ParseError(e);
		}

	}

	/**
	 * End a tag, require that we are ending the specified tag.
	 * 
	 * @param name
	 *            The tag to be ending.
	 */
	public void endTag(final String name) {
		if (!this.tagStack.peek().equals(name)) {
			final String str = "End tag mismatch, should be ending: "
					+ this.tagStack.peek() + ", but trying to end: " + name
					+ ".";
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new ParseError(str);
		} else {
			endTag();
		}

	}
}
