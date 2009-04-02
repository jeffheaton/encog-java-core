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
package org.encog.nlp.reason;

import java.util.ArrayList;
import java.util.List;

import org.encog.nlp.EncogNLP;
import org.encog.nlp.lexicon.EncogLexicon;
import org.encog.nlp.lexicon.data.Lemma;
import org.encog.nlp.lexicon.data.Word;
import org.encog.nlp.memory.Concept;
import org.encog.nlp.memory.ConstConcept;
import org.encog.nlp.memory.Relation;
import org.encog.nlp.memory.RelationHolder;
import org.encog.nlp.memory.VarConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jheaton
 */
public class Reason {
	private final EncogNLP context;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Creates a new instance of Reason */
	public Reason(final EncogNLP context) {
		this.context = context;
	}

	public boolean compareConcept(final Concept concept1, final Concept concept2) {
		if (concept1.equals(concept2)) {
			return true;
		}
		final List<Concept> list1 = getBaseConcepts(concept1);
		final List<Concept> list2 = getBaseConcepts(concept2);
		return countListOverlap(list1, list2) > 0;
	}

	public boolean compareRelation(final Relation relation1,
			final Relation relation2) {
		return compareConcept(relation1.getSource(), relation2.getSource())
				&& compareConcept(relation1.getType(), relation2.getType())
				&& compareConcept(relation1.getTarget(), relation2.getTarget());
	}

	public int countListOverlap(final List<Concept> list1, final List<Concept> list2) {
		int result = 0;
		for (final Object obj : list1) {
			if (list2.contains(obj)) {
				result++;
			}
		}
		return result;
	}

	public List<Concept> getBaseConcepts(final Concept concept) {
		final List<Concept> result = new ArrayList<Concept>();
		result.add(concept);
		getBaseConcepts(concept, result, 1);
		return result;
	}

	private void getBaseConcepts(final Concept concept,
			final List<Concept> result, final int depth) {
		if (depth > 10) {
			return;
		}

		final List<Relation> list = this.context.getMemory().getRelations()
				.searchSourceType(concept, ConstConcept.CONCEPT_SUBTYPE);
		for (final Relation rel : list) {
			final Concept co = rel.getTarget();
			result.add(co);
			getBaseConcepts(co, result, depth + 1);
		}
	}

	/*public Concept getWordType(final String word) {
		
		final VarConcept concept = this.context.getMemory().getConcepts()
				.create(word);

		Word lexWord = this.context.getLexicon().findWord(word);
		if(lexWord!=null)
		{
			Lemma lexLemma = this.context.getLexicon().findLemma(lexWord);
			if( lexLemma.hasUse(context.getLexicon().getWordType(EncogLexicon.WORD_TYPE_SPLIT)) )
			{
				return 
			}
		}
		
		final List<Relation> list = searchSourceType(concept,
				ConstConcept.CONCEPT_SUBTYPE);

		for (final Relation relation : list) {
			if (relation.getTarget().equals(ConstConcept.CONCEPT_WTYPE_ACTION)) {
				return ConstConcept.CONCEPT_WTYPE_ACTION;
			} else if (relation.getTarget().equals(
					ConstConcept.CONCEPT_WTYPE_SPLIT)) {
				return ConstConcept.CONCEPT_WTYPE_SPLIT;
			} else if (relation.getTarget().equals(
					ConstConcept.CONCEPT_WTYPE_QUESTION_SIMPLE)) {
				return ConstConcept.CONCEPT_WTYPE_QUESTION_SIMPLE;
			} else if (relation.getTarget().equals(
					ConstConcept.CONCEPT_WTYPE_QUESTION_EMBED)) {
				return ConstConcept.CONCEPT_WTYPE_QUESTION_EMBED;
			}
		}
		return null;
	}*/

	public boolean query3(final RelationHolder question) {
		for (final Relation shortRelation : question.getBaseRelations()) {
			final List<Relation> matches = searchShort(shortRelation.getSource());

			boolean foundOne = false;

			for( Relation longRelation: matches ) {
				if (compareRelation(longRelation, shortRelation)) {
					foundOne = true;
					break;
				}
			}

			if (!foundOne) {
				return false;
			}
		}
		return true;
	}

	public List<Relation> searchShort(final Concept concept) {
		final List<Relation> result = new ArrayList<Relation>();
		final List<Concept> search = getBaseConcepts(concept);
		for (final Concept c : search) {

			final List<Relation> matches = this.context.getMemory()
					.getRelations().searchSource(c);
			result.addAll(matches);
		}
		return result;
	}

	public List<Relation> searchSourceType(final Concept source,
			final Concept type) {
		final List<Relation> result = new ArrayList<Relation>();

		for (final Relation relation : this.context.getMemory().getRelations()
				.getRelations()) {
			if (compareConcept(relation.getSource(), source)
					&& compareConcept(relation.getType(), type)) {
				result.add(relation);
			}
		}

		return result;
	}

}
