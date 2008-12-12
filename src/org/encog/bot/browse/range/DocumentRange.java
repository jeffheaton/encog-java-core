package org.encog.bot.browse.range;

import org.encog.bot.browse.WebPage;
import org.encog.bot.dataunit.DataUnit;
import org.encog.bot.dataunit.TextDataUnit;

public class DocumentRange {
	private int begin;
	private int end;
	private WebPage source;
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
			}
		}
		
		return result.toString();
	}
	
	public String toString()
	{
		return getTextOnly();
	}
}
