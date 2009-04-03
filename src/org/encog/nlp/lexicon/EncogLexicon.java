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
package org.encog.nlp.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.encog.nlp.lexicon.data.Fix;
import org.encog.nlp.lexicon.data.Lemma;
import org.encog.nlp.lexicon.data.Word;
import org.encog.nlp.lexicon.data.WordType;
import org.encog.nlp.lexicon.data.WordTypePossibility;
import org.encog.util.orm.ORMSession;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncogLexicon {

	public static final String WORD_TYPE_NODE = "node";
	public static final String WORD_TYPE_ACTION = "action";
	public static final String WORD_TYPE_DESCRIPTION = "description";
	public static final String WORD_TYPE_SPLIT = "split";
	public static final String WORD_TYPE_QUESTION_SIMPLE = "question-s";
	public static final String WORD_TYPE_QUESTION_EMBED = "question-e";
	
	private ORMSession session;
	private Map<String,Fix> prefixes = new HashMap<String,Fix>();
	private Map<String,Fix> suffixes = new HashMap<String,Fix>();
	private Map<String,WordType> wordTypes = new HashMap<String,WordType>();
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public EncogLexicon(ORMSession session)
	{
		this.session = session;
	}
	
	public void loadCache()
	{
		Query q = session.createQuery("from org.encog.nlp.lexicon.data.Fix");
		List<Fix> list = q.list();
		for(Fix fix: list)
		{
			if( fix.isPre())
				this.prefixes.put(fix.getText(), fix);
			else
				this.suffixes.put(fix.getText(), fix);
		}
		
		q = session.createQuery("from org.encog.nlp.lexicon.data.WordType");
		List<WordType> typeList = q.list();
		for(WordType type: typeList)
		{
			wordTypes.put(type.getCode(), type);
		}
	}
	
	public Word findWord(String word)
	{
		String scanWord = word.toLowerCase();
		
		Query q = session.createQuery("from org.encog.nlp.lexicon.data.Word where text = :w");
		q.setString("w", scanWord);
		return (Word)q.uniqueResult();
	}
	
	public Lemma findLemma(String str)
	{
		Word word = this.findWord(str);
		return this.findLemma(word);
	}
	
	public Lemma findLemma(Word word)
	{
		Query q = session.createQuery("from org.encog.nlp.lexicon.data.Lemma where root = :w");
		q.setEntity("w", word);
		return (Lemma)q.uniqueResult();
	}
	
	public Lemma obtainLemmaForRoot(Word root)
	{
		Lemma lemma = findLemma(root);
		if( lemma==null )
		{
			lemma = new Lemma();
			lemma.setRoot(root);
			lemma.addWordUse(root);
			this.session.save(lemma);
		}
		return lemma;
	}
	
	public Word obtainWord(String word)
	{
		Word w = findWord(word);
		
		if( w==null )
		{
			w = new Word();
			w.setText(word);
			session.save(w);
		}
		
		return w;
		
	}
	
	public void addFix(String value,boolean pre, WordType type)
	{
		Fix fix = new Fix();
		fix.setText(value);
		fix.setPre(pre);
		fix.setWordType(type);
		session.save(fix);
	}
	
	public long countWordList()
	{
		Long l = (Long)session.createQuery("SELECT COUNT(*) FROM Word").uniqueResult();
		return l.longValue();
		
	}
	
	public Iterator iterateWordList(int start,int size)
	{
		Query q = session.createQuery("from org.encog.nlp.lexicon.data.Word w ORDER BY w.text");
		q.setFirstResult(start);
		q.setMaxResults(size);
		Iterator result = q.iterate();
		return result;
	}

	public Map<String, Fix> getPrefixes() {
		return prefixes;
	}

	public Map<String, Fix> getSuffixes() {
		return suffixes;
	}
	
	public Word removeFix(Word word, Fix fix)
	{
		String str = word.getText();
		
		if( fix.isPost() )
		{
			if( str.endsWith(fix.getText())  && word.length()>fix.length()  )
			{
				Word result;
				String removed = str.substring(0,str.length()-fix.length());
				result = this.findWord(removed);
				if( result==null )
				{
					result = this.findWord(removed+"e");
				}
				if( result!=null )
					return result;
			}
		}
		else if( fix.isPre() )
		{
			if( str.startsWith(fix.getText())  && word.length()>fix.length() )
			{
				Word temp = findWord(str.substring(fix.length()));
				if( temp!=null )
					return temp;
			}
		}
		
		return word;
	}
	
	public Collection<Fix> determineWordFixes(Word word)
	{
		Collection<Fix> result = new ArrayList<Fix>();
		
		// determine prefixes
		for(Fix fix: this.prefixes.values() )
		{
			if( word.length()>fix.getText().length() && word.startsWith(fix.getText()) )
			{
				if( removeFix(word,fix)!=null)
					result.add(fix);
			}
		}
		
		// determine suffixes
		for(Fix fix: this.suffixes.values() )
		{
			if( word.length()>fix.getText().length() && word.endsWith(fix.getText()) )
			{
				if( removeFix(word,fix)!=null)
					result.add(fix);
			}
		}
		
		return result;
	}
	
	public void setupWord(Word word)
	{
		// determine all prefixes and suffixes that this word has
		Collection<Fix> fixes = determineWordFixes(word);
		word.getFixes().addAll(fixes);
		
		// determine the root word and setup word types by fix
		Word rootWord = word;

		for(Fix fix: fixes)
		{
			rootWord = removeFix(rootWord, fix);
			addType(word,fix.getWordType());
		}
		
		// associate with the lemma
		Lemma lemma = this.obtainLemmaForRoot(rootWord);
		lemma.addWordUse(word);	
		
	}
	
	public void addType(Word word, WordType type)
	{
		if( type!=null && !word.isWordOfType(type))
		{
			WordTypePossibility pos = new WordTypePossibility();
			pos.setWord(word);
			pos.setType(type);
			word.getTypes().add(pos);
			session.save(pos);
		}
	}
	
	public Lemma obtainLemma(String str) {
		Word word = obtainWord(str);
		return obtainLemmaForRoot(word);
		
	}

	public Iterator<Lemma> iterateLemmaList() {
		Query q = session.createQuery("from org.encog.nlp.lexicon.data.Lemma");
		return q.iterate();
	}

	public void addWordType(String str) {
		WordType wordType = new WordType();
		wordType.setCode(str);
		session.save(wordType);
	}
	
	public WordType getWordType(String code)
	{
		return this.wordTypes.get(code);
	}

	public void addType(String word, WordType wordType) {
		addType(findWord(word),wordType);
		
	}
	
	public void registerGutenbergCount(String word, int count)
	{
		Word w = obtainWord(word);
		w.setGutenbergCount(w.getGutenbergCount()+count);
		session.save(w);
	}
	
	public void registerWikiCount(String word, int count)
	{
		Word w = obtainWord(word);
		w.setWikiCount(w.getWikiCount()+count);
		session.save(w);
	}

	public boolean hasWordType(Word usedWord,Lemma lemma, WordType wordType) {
		for(Word pos: lemma.getUses())
		{
			if( usedWord.equals(pos) && pos.hasType(wordType))
			{
				return true;
			}
		}
		return false;
	}


}
