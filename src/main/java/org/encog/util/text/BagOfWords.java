/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BagOfWords {
	private final Map<String, Integer> words = new HashMap<String, Integer>();
	private boolean breakSpaces = true;
	private boolean ignoreCase = true;
	private int totalWords;
	private final int k;
	private int laplaceClasses; 
	
	public BagOfWords(int laplace) {
		this.k = laplace;
	}
	
	public BagOfWords() {
		this(0);
	}

	public void process(String str) {
		if (breakSpaces) {
			processSpaces(str);
		} else {
			increase(str);
		}
	}

	private void processSpaces(String str) {
		StringBuilder word = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != '\'' && !Character.isLetterOrDigit(ch)) {
				if (word.length() > 0) {
					increase(word.toString());
					word.setLength(0);
				}
			} else {
				word.append(ch);
			}
		}

		if (word.length() > 0) {
			increase(word.toString());
		}
	}

	public void increase(String word) {
		String word2;
		this.totalWords++;
		this.laplaceClasses++;

		if (this.ignoreCase) {
			word2 = word.toLowerCase();
		} else {
			word2 = word;
		}

		if (this.words.containsKey(word2)) {
			int i = this.words.get(word2);
			i++;
			this.words.put(word2, i);
		} else {
			this.words.put(word2, 1);
		}
	}

	/**
	 * @return the breakSpaces
	 */
	public boolean isBreakSpaces() {
		return breakSpaces;
	}

	/**
	 * @param breakSpaces the breakSpaces to set
	 */
	public void setBreakSpaces(boolean breakSpaces) {
		this.breakSpaces = breakSpaces;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/**
	 * @return the words
	 */
	public Map<String, Integer> getWords() {
		return words;
	}

	public void clear() {
		this.words.clear();
	}

	public String toString() {
		StringBuilder result = new StringBuilder();

		// sort
		Set<String> set = new TreeSet<String>();
		set.addAll(this.words.keySet());

		// display
		for (String key : set) {
			int i = this.words.get(key);
			result.append(key);
			result.append(",");
			result.append(i);
			result.append("\n");
		}

		return result.toString();
	}

	public boolean contains(String word) {
		return this.words.containsKey(word);
	}
	
	public int getK() {
		return this.k;
	}

	/**
	 * @return the totalWords
	 */
	public int getTotalWords() {
		return totalWords;
	}
	
	public int getCount(String word) {
		String word2;
		if( this.ignoreCase ) {
			word2 = word.toLowerCase();
		} else {
			word2 = word;
		}
		if( !this.words.containsKey(word2) ) {
			return 0;
		}
		return this.words.get(word2);
	}

	public double probability(String word) {
		double n = ((double)getCount(word))+((double)this.k);
		double d = ((double)getTotalWords())+(k*this.laplaceClasses);
		return n/d;
	}

	/**
	 * @return the laplaceClasses
	 */
	public int getLaplaceClasses() {
		return laplaceClasses;
	}

	/**
	 * @param laplaceClasses the laplaceClasses to set
	 */
	public void setLaplaceClasses(int laplaceClasses) {
		this.laplaceClasses = laplaceClasses;
	}

	/**
	 * @param totalWords the totalWords to set
	 */
	public void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}

	public int getUniqueWords() {
		return this.words.size();
	}
	
	

}
