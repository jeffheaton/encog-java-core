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

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Entity
@Table(name="lexicon_word",
    uniqueConstraints = {@UniqueConstraint(columnNames={"text"})} )
public class Word extends DataObject {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Index( name="index_lexicon_word")
	private String text;

	@ManyToOne
	private Lemma lemma;
	
	@OneToMany(mappedBy = "word" )
	private Collection<WordTypePossibility> types = new ArrayList<WordTypePossibility>();
	
	private int gutenbergCount;
	private int wikiCount;

	@ManyToMany
	private Collection<Fix> fixes = new ArrayList<Fix>();
	
	
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
	
	

	public int getGutenbergCount() {
		return gutenbergCount;
	}

	public void setGutenbergCount(int gutenbergCount) {
		this.gutenbergCount = gutenbergCount;
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

	public boolean hasType(WordType wordType) {
		for( WordTypePossibility pos: this.types)
		{
			if( pos.getType().equals(wordType) )
				return true;
		}
		return false;
	}

	public Collection<Fix> getFixes() {
		return fixes;
	}

	public boolean hasFix(Fix fix) {
		if( fix.isPre() )
			return this.text.startsWith(fix.getText());
		else
			return this.text.endsWith(fix.getText());
	}

	public int getWikiCount() {
		return wikiCount;
	}

	public void setWikiCount(int wikiCount) {
		this.wikiCount = wikiCount;
	}
	
	
	
	
}
