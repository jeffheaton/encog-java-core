package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;

@Entity(name = "lexicon_word_type_poss")
@Table(name="lexicon_word_type_poss" )
public class WordTypePossibility extends DataObject {
	
	@ManyToOne(targetEntity = Word.class)
	@JoinColumn(nullable = false)
	private Word word;
	
	@ManyToOne(targetEntity = WordType.class)
	@JoinColumn(nullable = false)
	private WordType type;
	
	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

	public Word getWord() {
		return word;
	}

	public void setWord(Word word) {
		this.word = word;
	}

	public WordType getType() {
		return type;
	}

	public void setType(WordType type) {
		this.type = type;
	}
	
	

}
