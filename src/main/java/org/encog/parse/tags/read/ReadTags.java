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
package org.encog.parse.tags.read;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.encog.parse.PeekableInputStream;
import org.encog.parse.tags.Tag;
import org.encog.parse.tags.TagConst;
import org.encog.parse.tags.Tag.Type;

/**
 * Base class used to read tags. This base class is used by both the XML and
 * HTML parsing.
 *
 * @author jheaton
 *
 */
public class ReadTags {

	/**
	 * The bullet character.
	 */
	public static final int CHAR_BULLET = 149;

	/**
	 * The bullet character.
	 */
	public static final int CHAR_TRADEMARK = 129;

	/**
	 * Maximum length string to read.
	 */
	public static final int MAX_LENGTH = 10000;

	/**
	 * A mapping of certain HTML encoded values(i.e. &nbsp;) to their actual
	 * character values.
	 */
	private static Map<String, Character> charMap;

	/**
	 * The stream that we are parsing from.
	 */
	private final PeekableInputStream source;

	/**
	 * The current HTML tag. Access this property if the read function returns
	 * 0.
	 */
	private final Tag tag = new Tag();

	/**
	 * Are we locked, looking for an end tag? Such as the end of a comment?
	 */
	private String lockedEndTag;

	/**
	 * Does a "fake" end-tag need to be added, because of a compound tag (i.e.
	 * <br/>)? If so, this will hold a string for that tag.
	 */
	private String insertEndTag = null;

	/**
	 * The constructor should be passed an InputStream that we will parse from.
	 *
	 * @param is
	 *            An InputStream to parse from.
	 */
	public ReadTags(final InputStream is) {
		this.source = new PeekableInputStream(is);

		if (ReadTags.charMap == null) {
			ReadTags.charMap = new HashMap<String, Character>();
			ReadTags.charMap.put("nbsp", ' ');
			ReadTags.charMap.put("lt", '<');
			ReadTags.charMap.put("gt", '>');
			ReadTags.charMap.put("amp", '&');
			ReadTags.charMap.put("quot", '\"');
			ReadTags.charMap.put("bull", (char) ReadTags.CHAR_BULLET);
			ReadTags.charMap.put("trade", (char) ReadTags.CHAR_TRADEMARK);
		}
	}

	/**
	 * Remove any whitespace characters that are next in the InputStream.
	 *
	 */
	protected void eatWhitespace() {
		while (Character.isWhitespace((char) this.source.peek())) {
			this.source.read();
		}
	}

	/**
	 * Return the last tag found, this is normally called just after the read
	 * function returns a zero.
	 *
	 * @return The last HTML tag found.
	 */
	public Tag getTag() {
		return this.tag;
	}

	/**
	 * Checks to see if the next tag is the tag specified.
	 *
	 * @param name
	 *            The name of the tag desired.
	 * @param start
	 *            True if a starting tag is desired.
	 * @return True if the next tag matches these criteria.
	 */
	public boolean is(final String name, final boolean start) {
		if (!getTag().getName().equals(name)) {
			return false;
		}

		if (start) {
			return getTag().getType() == Type.BEGIN;
		} else {
			return getTag().getType() == Type.END;
		}
	}

	/**
	 * Parse an attribute name, if one is present.
	 *
	 * @return Return the attribute name, or null if none present.
	 */
	protected String parseAttributeName() {
		eatWhitespace();

		if ("\"\'".indexOf(this.source.peek()) == -1) {
			final StringBuilder buffer = new StringBuilder();
			while (!Character.isWhitespace(this.source.peek())
					&& (this.source.peek() != '=')
					&& (this.source.peek() != '>')
					&& (this.source.peek() != -1)) {
				final int ch = parseSpecialCharacter();
				buffer.append((char) ch);
			}
			return buffer.toString();
		} else {
			return (parseString());
		}
	}

	/**
	 * Parse any special characters(i.e. &nbsp);
	 *
	 * @return The character that was parsed.
	 */
	private char parseSpecialCharacter() {
		char result = (char) this.source.read();
		int advanceBy = 0;

		// is there a special character?
		if (result == '&') {
			int ch = 0;
			final StringBuilder buffer = new StringBuilder();

			// loop through and read special character
			do {
				ch = this.source.peek(advanceBy++);
				if ((ch != '&') && (ch != ';') && !Character.isWhitespace(ch)) {
					buffer.append((char) ch);
				}

			} while ((ch != ';') && (ch != -1) && !Character.isWhitespace(ch));

			final String b = buffer.toString().trim().toLowerCase();

			// did we find a special character?
			if (b.length() > 0) {
				if (b.charAt(0) == '#') {
					try {
						result = (char) Integer.parseInt(b.substring(1));
					} catch (final NumberFormatException e) {
						advanceBy = 0;
					}
				} else {
					if (ReadTags.charMap.containsKey(b)) {
						result = ReadTags.charMap.get(b);
					} else {
						advanceBy = 0;
					}
				}
			} else {
				advanceBy = 0;
			}
		}

		while (advanceBy > 0) {
			read();
			advanceBy--;
		}

		return result;
	}

	/**
	 * Called to parse a double or single quote string.
	 *
	 * @return The string parsed.
	 */
	protected String parseString() {
		final StringBuilder result = new StringBuilder();
		eatWhitespace();
		if ("\"\'".indexOf(this.source.peek()) != -1) {
			final int delim = this.source.read();
			while ((this.source.peek() != delim)
					&& (this.source.peek() != -1)) {
				if (result.length() > ReadTags.MAX_LENGTH) {
					break;
				}
				final int ch = parseSpecialCharacter();
				if ((ch == '\r') || (ch == '\n')) {
					continue;
				}
				result.append((char) ch);
			}
			if ("\"\'".indexOf(this.source.peek()) != -1) {
				this.source.read();
			}
		} else {
			while (!Character.isWhitespace(this.source.peek())
					&& (this.source.peek() != -1)
					&& (this.source.peek() != '>')) {
				result.append(parseSpecialCharacter());
			}
		}

		return result.toString();
	}

	/**
	 * Called when a tag is detected. This method will parse the tag.
	 *
	 */
	protected void parseTag() {
		this.tag.clear();
		this.insertEndTag = null;
		final StringBuilder tagName = new StringBuilder();

		this.source.read();

		// Is it a comment?
		if (this.source.peek(TagConst.COMMENT_BEGIN)) {
			this.source.skip(TagConst.COMMENT_BEGIN.length());
			while (!this.source.peek(TagConst.COMMENT_END)) {
				final int ch = this.source.read();
				if (ch != -1) {
					tagName.append((char) ch);
				} else {
					break;
				}
			}
			this.source.skip(TagConst.COMMENT_END.length());
			this.tag.setType(Type.COMMENT);
			this.tag.setName(tagName.toString());
			return;
		}

		// Is it CDATA?
		if (this.source.peek(TagConst.CDATA_BEGIN)) {
			this.source.skip(TagConst.CDATA_BEGIN.length());
			while (!this.source.peek(TagConst.CDATA_END)) {
				final int ch = this.source.read();
				if (ch != -1) {
					tagName.append((char) ch);
				} else {
					break;
				}

			}
			this.source.skip(TagConst.CDATA_END.length());
			this.tag.setType(Type.CDATA);
			this.tag.setName(tagName.toString());
			return;
		}

		// Find the tag name
		while (this.source.peek() != -1) {
			// if this is the end of the tag, then stop
			if (Character.isWhitespace((char) this.source.peek())
					|| (this.source.peek() == '>')) {
				break;
			}

			// if this is both a begin and end tag then stop
			if ((tagName.length() > 0) && (this.source.peek() == '/')) {
				break;
			}

			tagName.append((char) this.source.read());
		}

		eatWhitespace();

		if (tagName.charAt(0) == '/') {
			this.tag.setName(tagName.substring(1).toString());
			this.tag.setType(Tag.Type.END);
		} else {
			this.tag.setName(tagName.toString());
			this.tag.setType(Tag.Type.BEGIN);
		}
		// get the attributes

		while ((this.source.peek() != '>') && (this.source.peek() != -1)) {
			final String attributeName = parseAttributeName();
			String attributeValue = null;

			if (attributeName.equals("/")) {
				eatWhitespace();
				if (this.source.peek() == '>') {
					this.insertEndTag = this.tag.getName();
					break;
				}
			}

			// is there a value?
			eatWhitespace();
			if (this.source.peek() == '=') {
				this.source.read();
				attributeValue = parseString();
			}

			this.tag.setAttribute(attributeName, attributeValue);
		}
		this.source.read();
	}

	/**
	 * Check to see if the ending tag is present.
	 *
	 * @param name
	 *            The type of end tag being sought.
	 * @return True if the ending tag was found.
	 */
	private boolean peekEndTag(final String name) {
		int i = 0;

		// pass any whitespace
		while ((this.source.peek(i) != -1)
				&& Character.isWhitespace(this.source.peek(i))) {
			i++;
		}

		// is a tag beginning
		if (this.source.peek(i) != '<') {
			return false;
		} else {
			i++;
		}

		// pass any whitespace
		while ((this.source.peek(i) != -1)
				&& Character.isWhitespace(this.source.peek(i))) {
			i++;
		}

		// is it an end tag
		if (this.source.peek(i) != '/') {
			return false;
		} else {
			i++;
		}

		// pass any whitespace
		while ((this.source.peek(i) != -1)
				&& Character.isWhitespace(this.source.peek(i))) {
			i++;
		}

		// does the name match
		for (int j = 0; j < name.length(); j++) {
			if (Character.toLowerCase(this.source.peek(i)) != Character
					.toLowerCase(name.charAt(j))) {
				return false;
			}
			i++;
		}

		return true;
	}

	/**
	 * Read a single character from the HTML source, if this function returns
	 * zero(0) then you should call getTag to see what tag was found. Otherwise
	 * the value returned is simply the next character found.
	 *
	 * @return The character read, or zero if there is an HTML tag. If zero is
	 *         returned, then call getTag to get the next tag.
	 *
	 */
	public int read() {
		// handle inserting a "virtual" end tag
		if (this.insertEndTag != null) {
			this.tag.clear();
			this.tag.setName(this.insertEndTag);
			this.tag.setType(Type.END);
			this.insertEndTag = null;
			return 0;
		}

		// handle locked end tag
		if (this.lockedEndTag != null) {
			if (peekEndTag(this.lockedEndTag)) {
				this.lockedEndTag = null;
			} else {
				return this.source.read();
			}
		}

		// look for next tag
		if (this.source.peek() == '<') {
			parseTag();
			if ((this.tag.getType() == Tag.Type.BEGIN)
					&& (this.tag.getName().equalsIgnoreCase("script")
							|| this.tag
							.getName().equalsIgnoreCase("style"))) {
				this.lockedEndTag = this.tag.getName().toLowerCase();
			}
			return 0;
		} else if (this.source.peek() == '&') {
			return parseSpecialCharacter();
		} else {
			return (this.source.read());
		}
	}

	/**
	 * Read until we reach the next tag.
	 *
	 * @return True if a tag was found, false on EOF.
	 */
	public boolean readToTag() {
		int ch;
		while ((ch = read()) != -1) {
			if (ch == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[ReadTags: currentTag=");
		if (this.tag != null) {
			result.append(this.tag.toString());
		}
		result.append("]");
		return result.toString();

	}
}
