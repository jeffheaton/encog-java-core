package org.encog.bot.browse.range;

import java.net.URL;

import org.encog.bot.browse.Address;


public class Link extends DocumentRange {
	private Address target;

	public Address getTarget() {
		return target;
	}

	public void setTarget(Address target) {
		this.target = target;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Link:");
		result.append(target);
		result.append("|");
		result.append(this.getTextOnly());
		result.append("]");
		return result.toString();
	}
	
}
