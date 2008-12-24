package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;

@Entity
@Table(name="lexicon_fix",
    uniqueConstraints = {@UniqueConstraint(columnNames={"text","pre"})} )
public class Fix extends DataObject {

	private String text;
	private boolean pre;
	
	@ManyToOne
	@JoinColumn(nullable = true)
	private WordType wordType;
	
	@Override
	public void validate() {
		// TODO Auto-generated method stu
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		this.text = value;
	}

	public boolean isPre() {
		return pre;
	}

	public void setPre(boolean pre) {
		this.pre = pre;
	}

	public int length() {
		return this.text.length();
	}

	public boolean isPost() {
		return !isPre();
	}
	
	
	
	public WordType getWordType() {
		return wordType;
	}

	public void setWordType(WordType wordType) {
		this.wordType = wordType;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Fix:");
		if( this.pre)
		{
			result.append("p:");
		}
		else
		{
			result.append("s:");
		}
		result.append(this.text);
		result.append("]");
		return result.toString();
	}
}
