package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.encog.util.orm.DataObject;



@Entity(name = "lexicon_alias")
public class Alias extends DataObject {
	  
	  private String alias;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@ManyToOne(targetEntity = Word.class)
	@JoinColumn(nullable = false)
	private Word word;

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Alias:id=");
		result.append(this.getId());
		result.append(",");
		result.append(this.getAlias());
		result.append("]");
		return result.toString();
	}
	  
	  
	  
}
