package org.encog.bot.browse;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.DataUnit;



public class WebPage {
	protected List<DataUnit> data = new ArrayList<DataUnit>();
	protected List<DocumentRange> contents = new ArrayList<DocumentRange>();
	protected DocumentRange title;
	
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
