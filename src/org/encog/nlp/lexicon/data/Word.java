package org.encog.nlp.lexicon.data;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;
import org.hibernate.annotations.Index;


@Entity
@Table(name="lexicon_word",
    uniqueConstraints = {@UniqueConstraint(columnNames={"text"})} )
public class Word extends DataObject {

	@Index( name="index_lexicon_word")
	private String text;

	@ManyToOne
	@JoinColumn(nullable = true)
	private Lemma lemma;
	
	@OneToMany(mappedBy = "word" )
	@JoinColumn(name = "id", nullable = false)
	private Collection<WordTypePossibility> types = new ArrayList<WordTypePossibility>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text.toLowerCase();
	}

	public int length() {
		return text.length();
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub

	}

	public boolean endsWith(String value) {
		return text.endsWith(value);
	}

	public boolean startsWith(String value) {
		return text.startsWith(value);
	}

	public Lemma getLemma() {
		return lemma;
	}

	public void setLemma(Lemma lemma) {
		this.lemma = lemma;
	}
	
	public boolean isWordOfType(WordType type)
	{
		return types.contains(type);
	}
	


	public Collection<WordTypePossibility> getTypes() {
		return types;
	}

	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Word:");
		result.append(this.text);
		result.append(",lemma=");
		if( this.lemma!=null )
			result.append(this.lemma.getId());
		result.append("]");
		return result.toString();
	}
	
}
