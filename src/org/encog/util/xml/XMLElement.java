package org.encog.util.xml;

import java.util.HashMap;
import java.util.Map;

public class XMLElement {
	
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
