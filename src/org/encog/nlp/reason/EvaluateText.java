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
public class EvaluateText extends ParseText {

	public static final int TYPE_STATEMENT = 1;
	public static final int TYPE_QUERY = 2;
	public static final int TYPE_YESNO = 3;

	private int type;
	private final RelationHolder shortTermMemory = new RelationHolder();
	private final EncogNLP context;
	private final RelationHolder lastRelation = new RelationHolder();
	private final ParseActions actions;
	private final Reason reason;
	private final List<String> words = new ArrayList<String>();
	private int currentWord;
	private boolean ignore = false;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Creates a new instance of Parse */
	public EvaluateText(final EncogNLP context) {
		super(context);
		this.actions = new ParseActions(context);
		this.reason = new Reason(context);
		this.context = context;
	}

	public void addRelation(final Relation relation) {
		this.lastRelation.add(relation);
	}

	boolean compareWord(final String cmp) {
		final List<String> cmpList = new ArrayList<String>();
		splitWords(cmp, cmpList);

		if (cmpList.size() > this.words.size() - this.currentWord) {
			return false;
		}

		for (int i = 0; i < cmpList.size(); i++) {
			final String w = cmpList.get(i);
			final String w2 = this.words.get(this.currentWord + i);
			if (!w.equalsIgnoreCase(w2)) {
				return false;
			}
		}
		this.currentWord += cmpList.size() - 1;
		return true;
	}

	public void dump() {
		this.context.getMemory().getRelations().dump();
	}

	public void dumpShort() {
		this.shortTermMemory.dump();
	}

	public RelationHolder getLastRelation() {
		return this.lastRelation;
	}

	public int getLocation() {
		int target = this.lastRelation.findFirstEmpty();
		if (target == Relation.RELATION_NODE_NONE) {
			target = Relation.RELATION_NODE_TARGET;
		}
		return target;
	}

	public Reason getReason() {
		return this.reason;
	}

	public RelationHolder getShortTermMemory() {
		return this.shortTermMemory;
	}

	boolean isPossessive(final String word) {
		if (word.toLowerCase().equals("its")) {
			return true;
		} else if (word.toLowerCase().endsWith("\'s")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTargetEmpty() {
		final int target = this.lastRelation.findFirstEmpty();
		return target == Relation.RELATION_NODE_TARGET;
	}

	public String parse(final String input) {
		this.type = TYPE_STATEMENT;
		this.shortTermMemory.clear();

		final Relation relation = this.shortTermMemory.createRelation();
		getLastRelation().clear();
		getLastRelation().add(relation);
		parseShortTerm(input);
		if (this.type == TYPE_STATEMENT) {
			if (this.shortTermMemory.countNull() > 0) {
				return "Sorry, I did not understand that.";
			} else {
				this.context.getMemory().addRelation(this.shortTermMemory);
			}
			return "Okay.";
		} else if (this.type == TYPE_YESNO) {
			final boolean answer = this.reason.query3(this.shortTermMemory);

			if (answer) {
				return "Yes";
			} else {
				return "No";
			}
		}

		return "What?";
	}

	void parseShortTerm(final String input) {
		// split the words
		splitWords(input, this.words);

		// now parse the words
		this.currentWord = 0;
		while (this.currentWord < this.words.size()) {
			final String w = this.words.get(this.currentWord);
			parseWord(w);
			this.currentWord++;
		}
	}

	void parseWord(String word) {
		// System.out.println(">>>>>>" + word);
		// shortTermMemory.dump();
		boolean processAsBasicWord = false;

		if (this.ignore) {
			return;
		}

		if (word.trim().length() == 0) {
			return;
		}

		word = word.toLowerCase();

		// swap out word with simple form
		if (compareWord("is that which is")) {
			word = "is";
		} else if (compareWord("to have its own")) {
			word = "owns";
		}
		
		Word lexWord = context.getLexicon().findWord(word);
		Lemma lemma = null;
		
		if( lexWord!=null )
			lemma = context.getLexicon().findLemma(lexWord);
		
		boolean isQuestionEmbed = false;
		boolean isQuestionSimple = false;
		boolean isSplit = false;
		boolean isAction = false;
		
		if( lexWord!=null )
		{
			isQuestionEmbed = lexWord.hasType(context.getLexicon().getWordType(EncogLexicon.WORD_TYPE_QUESTION_EMBED));
			isQuestionSimple = lexWord.hasType(context.getLexicon().getWordType(EncogLexicon.WORD_TYPE_QUESTION_SIMPLE));
			isSplit = lexWord.hasType(context.getLexicon().getWordType(EncogLexicon.WORD_TYPE_SPLIT));
			isAction = lexWord.hasType(context.getLexicon().getWordType(EncogLexicon.WORD_TYPE_ACTION));
		}

		
		final int location = getLocation();

		if (word.equals("(")) {
			this.ignore = true;
			return;
		} else if (word.equals(")")) {
			this.ignore = false;
			return;
		} else if (word.equals(".") || word.equals(",") || word.equals("?")
				|| word.equals("!")) {
			return;
		} else if (word.equals("a") || word.equals("an") || word.equals("the")) {
			return;
		} else if ((location == Relation.RELATION_NODE_SOURCE || location == Relation.RELATION_NODE_NONE)
				&& (isQuestionEmbed || isQuestionSimple)) {
			if (isQuestionEmbed) {
				this.actions.beginQuestion(word);
			} else {
				this.type = TYPE_YESNO;
			}
		} else if (word.equals(";")) {
			this.lastRelation.clear();
		} else if (isPossessive(word)) {
			this.actions.side(location, this.context.getTypeConcept("of"),
					this.context.getMemory().getConcepts().create(
							stripPossessive(word)));
		} else if (isSplit)// split
		// words
		{
			this.actions.split();
		} else if (word.equals("that") || word.equals("which")
				|| word.equals("who") || word.equals("whome")
				|| word.equals("(")) {
			if (isTargetEmpty()) {
				processAsBasicWord = true;
			} else {
				this.actions.extend(null);
			}
		} else if (word.equals("of") || word.equals("by")
				|| word.equals("with") || word.equals("in")
				|| word.equals("as") || word.equals("for")
				|| isAction) { // action words
			if (location == Relation.RELATION_NODE_TARGET) {
				if (this.lastRelation
						.countContents(Relation.RELATION_NODE_TARGET) < 1) {
					VarConcept c = this.context.getMemory().getConcepts()
							.create(word);
					this.actions.side(Relation.RELATION_NODE_TYPE,
							ConstConcept.CONCEPT_SUBTYPE, c);
				} else {
					this.actions.extend(this.context.getTypeConcept(word));
				}
			} else {
				this.actions.lastRelationAdd(Relation.RELATION_NODE_TYPE,
						this.context.getTypeConcept(word));
			}
		} else if (word.equals("are") || word.equals("is")
				|| word.equals("has") || word.equals("to")) { // binary or binary modifyable (i.e. to have)
			if (location == Relation.RELATION_NODE_TARGET) {
				if (this.lastRelation
						.countContents(Relation.RELATION_NODE_TARGET) < 1) {
					VarConcept c = this.context.getMemory().getConcepts()
							.create(word);
					this.actions.side(Relation.RELATION_NODE_TYPE,
							ConstConcept.CONCEPT_SUBTYPE, c);
				} else {
					this.actions.extend(this.context.getTypeConcept(word));
				}
			} else {
				this.actions.lastRelationAdd(Relation.RELATION_NODE_TYPE,
						this.context.getTypeConcept(word));
			}
		} else {
			processAsBasicWord = true;
		}

		if (processAsBasicWord) {
			this.actions.processBasicWord(word);
		}

	}

	public void replaceLastRelation(final List<Relation> list) {
		this.lastRelation.replaceRelations(list);
	}

	void setType(final int type) {
		this.type = type;
	}

	String stripPossessive(final String word) {
		if (word.toLowerCase().equals("its")) {
			return "its";
		} else if (word.toLowerCase().endsWith("\'s")) {
			return word.substring(0, word.length() - 2);
		} else {
			return word;
		}
	}

}
