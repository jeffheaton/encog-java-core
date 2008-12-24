package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;

@Entity(name = "lexicon_word_type")
@Table(name="lexicon_word_type",
    uniqueConstraints = {@UniqueConstraint(columnNames={"code"})} )
public class WordType extends DataObject {

	private String code;
	
	
	
	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}

}
