/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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
package org.encog.bot.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTMLTag: This class holds a single HTML tag. This class subclasses the
 * AttributeList class. This allows the HTMLTag class to hold a collection of
 * attributes, just as an actual HTML tag does.
 */
public class HTMLTag {
	
	/**
	 * The attributes.
	 */
	private final Map<String, String> attributes = 
		new HashMap<String, String>();

	/**
	 * The tag name.
	 */
	private String name = "";

	/**
	 * Is this both a beginning and ending tag.
	 */
	private boolean ending;

	/**
	 * Clear the HTML tag.
	 */
	public void clear() {
		this.attributes.clear();
		this.name = "";
		this.ending = false;
	}

	/**
	 * Get the value of the specified attribute.
	 * 
	 * @param name
	 *            The name of an attribute.
	 * @return The value of the specified attribute.
	 */
	public String getAttributeValue(final String name) {
		return this.attributes.get(name.toLowerCase());
	}

	/**
	 * Get the tag name.
	 * @return The tag name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the ending
	 */
	public boolean isEnding() {
		return this.ending;
	}

	/**
	 * Set a HTML attribute.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 */
	public void setAttribute(final String name, final String value) {
		this.attributes.put(name.toLowerCase(), value);
	}

	/**
	 * @param ending
	 *            The ending to set.
	 */
	public void setEnding(final boolean ending) {
		this.ending = ending;
	}

	/**
	 *  Set the tag name.
	 * @param s The name.
	 */
	public void setName(final String s) {
		this.name = s;
	}

	/**
	 * Convert this tag back into string form, with the beginning < and 
	 * ending >.
	 * 
	 * @return The Attribute object that was found.
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder("<");
		buffer.append(this.name);

		final Set<String> set = this.attributes.keySet();
		for (final String key : set) {
			final String value = this.attributes.get(key);
			buffer.append(' ');

			if (value == null) {
				buffer.append("\"");
				buffer.append(key);
				buffer.append("\"");
			} else {
				buffer.append(key);
				buffer.append("=\"");
				buffer.append(value);
				buffer.append("\"");
			}

		}

		if (this.ending) {
			buffer.append('/');
		}
		buffer.append(">");
		return buffer.toString();
	}
}
