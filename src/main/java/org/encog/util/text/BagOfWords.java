package org.encog.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BagOfWords {
	private final Map<String,Integer> words = new HashMap<String,Integer>();
	private boolean breakSpaces = true;
	private boolean ignoreCase = true;
	
	public void process(String str) {
		if( breakSpaces ) {
			processSpaces(str);
		} else {
			increase(str);
		}
	}
	
	private void processSpaces(String str) {
		StringBuilder word = new StringBuilder();
		
		for(int i=0;i<str.length();i++) {
			char ch = str.charAt(i);
			if( ch!='\'' && !Character.isLetterOrDigit(ch)) {
				increase(word.toString());
				word.setLength(0);				
			} else {
				word.append(ch);
			}
		}
	}
	
	public void increase(String word) {
		String word2;
		
		if( this.ignoreCase ) {
			word2 = word.toLowerCase();
		} else {
			word2 = word;
		}
		
		if( this.words.containsKey(word2)) {
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
	
	public int size() {
		return words.size();
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
		for(String key: set) {
			int i = this.words.get(key);
			result.append(key);
			result.append(",");
			result.append(i);
			result.append("\n");
		}
		
		return result.toString();
	}
	
}
