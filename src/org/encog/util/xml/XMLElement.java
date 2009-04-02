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
package org.encog.util.xml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLElement {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public enum XMLElementType {
		start,
		end,
		statEnd,
		documentBegin,
		documentEnd,
		text,
		cdata
	}
	
	private XMLElementType type;
	private String text;
	private Map<String,String> attributes = new HashMap<String,String>();
	
	public XMLElement(XMLElementType type)
	{
		this.type = type;
	}
	
	public XMLElement(XMLElementType type,String text)
	{
		this.type = type;
		this.text = text;
	}
	
	
	/**
	 * @return the type
	 */
	public XMLElementType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(XMLElementType type) {
		this.type = type;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.type);
		result.append(":");
		result.append(this.text);
		if( this.attributes.size()>0 )
		{
			result.append(',');
			for(String name: this.attributes.keySet() )
			{
				String value = this.attributes.get(name);
				result.append(name);
				result.append('=');
				result.append(value);
				result.append(',');
				
				
			}
		}
		result.append("]");
		return result.toString();
	}
	

}
