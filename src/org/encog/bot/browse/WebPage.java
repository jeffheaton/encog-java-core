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
package org.encog.bot.browse;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.DataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WebPage {
	protected List<DataUnit> data = new ArrayList<DataUnit>();
	protected List<DocumentRange> contents = new ArrayList<DocumentRange>();
	protected DocumentRange title;
	
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void addDataUnit(DataUnit unit)
	{
		data.add(unit);
	}
	
	public DataUnit getDataUnit(int i)
	{
		return data.get(i);
	}

	public List<DataUnit> getData() {
		return data;
	}
	
	public int getDataSize()
	{
		return data.size();
	}

	public List<DocumentRange> getContents() {
		return contents;
	}

	public void setContents(List<DocumentRange> contents) {
		this.contents = contents;
	}
	
	public void addContent(DocumentRange span)
	{
		span.setSource(this);
		contents.add(span);
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		for( DocumentRange span: this.getContents())
		{
			result.append(span.toString());
			result.append("\n");
		}
		return result.toString();
	}
	
	public DocumentRange getTitle() {
		return title;
	}

	public void setTitle(DocumentRange title) {
		this.title = title;
		this.title.setSource(this);	
	}
	
	public Link findLink(String str)
	{
		for( DocumentRange span: this.getContents() )
		{
			if( span instanceof Link )
			{
				Link link = (Link)span;
				if( link.getTextOnly().equals(str))
					return link;
			}
		}
		return null;
	}

	public DocumentRange find(Class<Form> c, int i) {
		for( DocumentRange span: this.getContents() )
		{
			if( span.getClass().getName().equals(c.getName()) )
			{
				if( i<=0 )
					return span;
				i--;
			}
		}
		return null;
		
	}	
}
