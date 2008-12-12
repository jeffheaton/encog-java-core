package org.encog.bot.browse.range;

import java.util.ArrayList;
import java.util.List;

import org.encog.bot.browse.Address;

public class Form extends DocumentRange {
	public enum Method { POST, GET };
	protected Address action;
	protected Method method;
	protected List<FormElement> elements = new ArrayList<FormElement>();
	
	
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



	public void addElement(FormElement input) {
		this.elements.add(input);
		input.setOwner(this);
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


	public List<FormElement> getElements() {
		return elements;
	}

}
