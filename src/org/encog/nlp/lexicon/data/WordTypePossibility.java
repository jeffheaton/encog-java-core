/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.encog.nlp.lexicon.data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.encog.util.orm.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
@Entity
@Table(name="lexicon_word_type_poss" )
public class WordTypePossibility extends DataObject {
	
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 2860281648328064455L;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	@Transient
	final transient private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@ManyToOne(targetEntity = Word.class)
	private Word word;
	
	@ManyToOne(targetEntity = WordType.class)
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
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[WordTypePossibility:");
		result.append(this.type.getCode());
		result.append("]");
		return result.toString();
	}
	
	

}
