package org.encog.bot.browse.range;

abstract public class FormElement extends DocumentRange {
	private String name;
	private String value;
	private Form owner;
	
	public Form getOwner() {
		return owner;
	}
	public void setOwner(Form owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	abstract public boolean isAutoSend();
	
}
