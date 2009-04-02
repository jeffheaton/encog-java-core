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
package org.encog.bot.browse.range;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.Address;
import org.encog.bot.browse.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Form extends DocumentRange {
	public enum Method { POST, GET };
	protected Address action;
	protected Method method;
	protected List<FormElement> elements = new ArrayList<FormElement>();
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Form(WebPage source)
	{
		super(source);
	}
	
	
	public Address getAction() {
		return action;
	}



	public void setAction(Address action) {
		this.action = action;
	}



	public Method getMethod() {
		return method;
	}



	public void setMethod(Method method) {
		this.method = method;
	}



	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[Form:");
		builder.append("method=");
		builder.append(this.getMethod());
		builder.append(",action=");
		builder.append(this.getAction());
		for(FormElement element:elements)
		{
			builder.append("\n\t");
			builder.append(element.toString());
		}
		builder.append("]");
		return builder.toString();
	}

	public Input findType(String type,int index)
	{
		for(FormElement element: this.elements) {
			if( element instanceof Input ) {
				Input input = (Input)element;
				if( input.getType().equalsIgnoreCase(type))
				{
					if( index<=0)
						return input;
					index--;
				}
			}
		}
		return null;
	}

}
