package org.encog.bot.dataunit;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.bot.html.HTMLTag;

public class TagDataUnit extends DataUnit {
	protected HTMLTag tag;
	
	public HTMLTag getTag() {
		return tag;
	}

	public void setTag(HTMLTag tag) {
		this.tag = tag;
	}
	
	public String toString()
	{
		return tag.toString();
	}
}
