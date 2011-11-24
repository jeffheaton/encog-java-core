package org.encog.ml.bayesian.words;


import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.probability.CalcProbability;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.naive.NaiveBayesian;
import org.encog.util.text.BagOfWords;

public class BayesianWordAnalyzer {
	
	private int k;
	private BagOfWords classBag;
	private BagOfWords notClassBag;
	private BagOfWords totalBag;
	private final String className;
	private final String notClassName;
	private CalcProbability messageProbability;
	private String lastProblem;
	private final int classSampleCount;
	private final int notClassSampleCount;
	
	public BayesianWordAnalyzer(
			int theK, 
			String theClassName, 
			String[] classStrings, 
			String theNotClassName, 
			String[] notClassStrings)
	{
		this.k = theK;
		
		this.className = theClassName;
		this.notClassName = theNotClassName;
		
		this.classSampleCount = classStrings.length;
		this.notClassSampleCount = notClassStrings.length;
		
		this.classBag = new BagOfWords(this.k);
		this.notClassBag = new BagOfWords(this.k);
		this.totalBag = new BagOfWords(this.k);

		
		for(String line: classStrings) {
			this.classBag.process(line);
			totalBag.process(line);
		}
		
		for(String line: notClassStrings) {
			this.notClassBag.process(line);
			totalBag.process(line);
		}
		
		this.classBag.setLaplaceClasses(totalBag.getUniqueWords());
		this.notClassBag.setLaplaceClasses(totalBag.getUniqueWords());
		
		this.messageProbability = new CalcProbability(this.k);
		messageProbability.addClass(this.classSampleCount);
		messageProbability.addClass(this.notClassSampleCount);
	}

	
	public List<String> separateSpaces(String str) {
		List<String> result = new ArrayList<String>();
		StringBuilder word = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != '\'' && !Character.isLetterOrDigit(ch)) {
				if (word.length() > 0) {
					result.add(word.toString());
					word.setLength(0);
				}
			} else {
				word.append(ch);
			}
		}

		if (word.length() > 0) {
			result.add(word.toString());
		}
		
		return result;
	}
	
	public double probability(String m) {
		List<String> words = separateSpaces(m);
		double probClass = messageProbability.calculate(0);

		NaiveBayesian network = new NaiveBayesian(this.className, probClass);
		
		int index = 0;
		for( String word: words) {
			String w2 = word+index;
			double pClass = this.classBag.probability(word); // class
			double pNot = this.notClassBag.probability(word); // not class
			network.addFeature(w2, pClass, pNot);
			index++;
		}
		
		network.finalizeStructure();
		
		return network.computeNaiveProbability();		
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}


	/**
	 * @return the notClassName
	 */
	public String getNotClassName() {
		return notClassName;
	}
	
	public double getClassProbability() {
		return this.messageProbability.calculate(0);
	}
	
	public double getNotClassProbability() {
		return this.messageProbability.calculate(1);
	}
	
	public double probabilityWordClass(String word) {
		StringBuilder s = new StringBuilder();
		s.append("P(");
		s.append(word);
		s.append("|");
		s.append(this.className);
		s.append(")");
		this.lastProblem = s.toString();
		return this.classBag.probability(word);
	}
	
	public double probabilityWordNotClass(String word) {
		StringBuilder s = new StringBuilder();
		s.append("P(");
		s.append(word);
		s.append("|");
		s.append(this.notClassName);
		s.append(")");
		this.lastProblem = s.toString();
		return this.notClassBag.probability(word);
	}
	
	public String getLastProblem() {
		return this.lastProblem;
	}


	public BagOfWords getClassWordBag() {
		return this.classBag;		
	}
	
	public BagOfWords getNotClassWordBag() {
		return this.notClassBag;		
	}


	public int getDictionarySize() {		
		return this.totalBag.getUniqueWords();
	}	
}
