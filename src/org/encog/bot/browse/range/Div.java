package org.encog.bot.browse.range;

public class Div extends HierarchyElement {
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Div:class=");
		result.append(this.getClassAttribute());
		result.append(",id=");
		result.append(this.getIdAttribute());
		result.append(",elements=");
		result.append(this.getElements().size());
		result.append("]");
		return result.toString();
	}
}
