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
package org.encog.parse.tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.encog.parse.ParseError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTMLTag: This class holds a single HTML tag. This class subclasses the
 * AttributeList class. This allows the HTMLTag class to hold a collection of
 * attributes, just as an actual HTML tag does.
 */
public class Tag {
	public enum Type {
		BEGIN, END, COMMENT, CDATA
	};
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/*
	 * The attributes
	 */
	private Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * The tag name.
	 */
	private String name = "";

	private Type type;

	public void clear() {
		this.attributes.clear();
		this.name = "";
		this.type = Type.BEGIN;
	}

	/**
	 * Get the value of the specified attribute.
	 * 
	 * @param name
	 *            The name of an attribute.
	 * @return The value of the specified attribute.
	 */
	public String getAttributeValue(String name) {
		return this.attributes.get(name.toLowerCase());
	}

	/**
	 * Get the tag name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set a HTML attribute.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 */
	public void setAttribute(String name, String value) {
		this.attributes.put(name.toLowerCase(), value);
	}

	/**
	 * Set the tag name.
	 */
	public void setName(String s) {
		this.name = s;
	}

	/**
	 * Convert this tag back into string form, with the beginning < and ending >.
	 * 
	 * @param id
	 *            A zero based index that specifies the attribute to retrieve.
	 * @return The Attribute object that was found.
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("<");

		if (type == Type.END)
			buffer.append("/");

		buffer.append(this.name);

		Set<String> set = this.attributes.keySet();
		for (String key : set) {
			String value = this.attributes.get(key);
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

		buffer.append(">");
		return buffer.toString();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public Tag clone()
	{
		Tag result = new Tag();
		result.setName(getName());
		result.setType(getType());
		for(String key: this.attributes.keySet())
		{
			String value = this.attributes.get(key);
			result.setAttribute(key, value);
		}
		return result;
	}

	public int getAttributeInt(String attributeId) {
		try
		{
			String str = getAttributeValue(attributeId);
			return Integer.parseInt(str);
		}
		catch(NumberFormatException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.error("Exception",e);
			}
			throw new ParseError(e);
		}
		
	}

}
