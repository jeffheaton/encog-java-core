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

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class that represents a document range.  A document range is a collection 
 * of tags that all apply to one "concept".  For example, a Form, or a Link.  This
 * allows the form to collect the elements inside the form, or a link to collect
 * the text along with the link tag.
 * @author jheaton
 *
 */
public class DocumentRange {
	private int begin;
	private int end;
	private WebPage source;
	private String idAttribute;
	private String classAttribute;
	private List<DocumentRange> elements = new ArrayList<DocumentRange>();
	private DocumentRange parent;
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public DocumentRange(WebPage source)
	{
		this.source = source;
	}
	
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public WebPage getSource() {
		return source;
	}
	public void setSource(WebPage source) {
		this.source = source;
	}
	
	public String getTextOnly()
	{
		StringBuilder result = new StringBuilder();
		
		for(int i=getBegin();i<getEnd();i++)
		{
			DataUnit du = this.source.getData().get(i);
			if( du instanceof TextDataUnit)
			{
				result.append(du.toString());
				result.append("\n");
			}
		}
		
		return result.toString();
	}
	
	public String toString()
	{
		return getTextOnly();
	}
	public List<DocumentRange> getElements() {
		return elements;
	}
	public void setElements(List<DocumentRange> elements) {
		this.elements = elements;
	}
	public DocumentRange getParent() {
		return parent;
	}
	public void setParent(DocumentRange parent) {
		this.parent = parent;
	}
	
	public void addElement(DocumentRange element)
	{
		this.elements.add(element);
		element.setParent(this);
	}
	
	/**
	 * @return the idAttribute
	 */
	public String getIdAttribute() {
		return idAttribute;
	}
	/**
	 * @param idAttribute the idAttribute to set
	 */
	public void setIdAttribute(String idAttribute) {
		this.idAttribute = idAttribute;
	}

	/**
	 * @return the classAttribute
	 */
	public String getClassAttribute() {
		return classAttribute;
	}
	/**
	 * @param classAttribute the classAttribute to set
	 */
	public void setClassAttribute(String classAttribute) {
		this.classAttribute = classAttribute;
	}

}
