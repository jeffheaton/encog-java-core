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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
@Table(name = "lexicon_lemma")
public class Lemma extends DataObject {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 4329531988086656487L;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	@OneToMany(mappedBy = "lemma")
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

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[Lemma:id=");
		result.append(this.getId());
		result.append(",uses=");
		boolean first = true;
		for (Word use : this.uses) {
			if (first) {
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
		for (Word word : this.uses) {
			if (word.hasFix(fix)) {
				return word;
			}
		}
		return null;
	}

}
