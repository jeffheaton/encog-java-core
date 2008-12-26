package org.encog.nlp.lexicon.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.encog.util.orm.DataObject;

@Entity
@Table(name = "lexicon_lemma")
public class Lemma extends DataObject {

	@OneToMany(mappedBy = "lemma" )
	private Collection<Word> uses = new ArrayList<Word>();
	
	
	@ManyToOne
	private Word root;
	
	@Override
	public void validate() {
		// TODO Auto-generated method stub
	}


	public Collection<Word> getUses() {
		return uses;
	}


	public void setUses(Collection<Word> uses) {
		this.uses = uses;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Lemma:id=");
		result.append(this.getId());
		result.append(",uses=");
		boolean first = true;
		for(Word use:this.uses)
		{
			if( first )
			{
				result.append(',');
				first = false;
			}
			result.append(use.toString());
		}
		result.append("]");
		return result.toString();
	}


	public void addWordUse(Word word) {
		word.setLemma(this);
		this.uses.add(word);
		
	}


	public Word getRoot() {
		return root;
	}


	public void setRoot(Word root) {
		this.root = root;
	}


	public Word findFix(Fix fix) {
		for(Word word: this.uses)
		{
			if( word.hasFix(fix) )
			{
				return word;
			}
		}
		return null;
	}
	
	
	
	
}
