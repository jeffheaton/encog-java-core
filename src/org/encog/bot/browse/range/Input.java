package org.encog.bot.browse.range;

public class Input extends FormElement {

	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[Input:");
		builder.append("type=");
		builder.append(this.getType());
		builder.append(",name=");
		builder.append(this.getName());
		builder.append(",value=");
		builder.append(this.getValue());
		builder.append("]");
		return builder.toString();
	}
	@Override
	public boolean isAutoSend() {
		// TODO Auto-generated method stub
		return !type.equalsIgnoreCase("submit");
	}
	
	
	
}
