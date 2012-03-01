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
package org.encog.ml.bayesian.parse;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.util.SimpleParser;
import org.encog.util.csv.CSVFormat;

/**
 * Used to parse probability strings for the Bayes networks.
 */
public class ParseProbability {
	
	/**
	 * The network used.
	 */
	private final BayesianNetwork network;
	
	/**
	 * Parse the probability for the specified network.
	 * @param theNetwork THe network to parse for.
	 */
	public ParseProbability(BayesianNetwork theNetwork) {
		this.network = theNetwork;
	}
	
	/**
	 * Add events, as they are pased.
	 * @param parser The parser.
	 * @param results The events found.
	 * @param delim The delimiter to use.
	 */
	private void addEvents(SimpleParser parser, List<ParsedEvent> results, String delim) {
		boolean done = false;
		StringBuilder l = new StringBuilder();
		
		while( !done && !parser.eol()) {
			char ch = parser.peek();
			if( delim.indexOf(ch) != -1 ) {
				if( ch==')' || ch=='|' ) 
					done = true;
									
				ParsedEvent parsedEvent;			
				
				// deal with a value specified by + or -
				if( l.length()>0 && l.charAt(0)=='+' ) {
					String l2 = l.toString().substring(1);
					parsedEvent = new ParsedEvent(l2.trim());
					parsedEvent.setValue("0");
				} else if( l.length()>0 && l.charAt(0)=='-') {
					String l2 = l.toString().substring(1);
					parsedEvent = new ParsedEvent(l2.trim());
					parsedEvent.setValue("1");
				} else {
					String l2 = l.toString();
					parsedEvent = new ParsedEvent(l2.trim());
				}
				
				// parse choices
				if( ch=='[' ) {
					parser.advance();
					int index = 0;
					while( ch!=']' && !parser.eol() ) {
						
						String labelName = parser.readToChars(":,]");
						if( parser.peek()==':' ) {
							parser.advance();
							parser.eatWhiteSpace();
							double min = Double.parseDouble(parser.readToWhiteSpace());
							parser.eatWhiteSpace();
							if(!parser.lookAhead("to", true) ) {
								throw new BayesianError("Expected \"to\" in probability choice range.");
							}
							parser.advance(2);
							double max = CSVFormat.EG_FORMAT.parse(parser.readToChars(",]"));
							parsedEvent.getList().add(new ParsedChoice(labelName,min,max));
							
						} else {
							parsedEvent.getList().add(new ParsedChoice(labelName,index++));
						}
						parser.eatWhiteSpace();
						ch = parser.peek();
						
						if( ch==',' ) {
							parser.advance();
						}
					}
				}
				
				// deal with a value specified by =
				if( parser.peek()=='=' ) {
					parser.readChar();
					String value = parser.readToChars(delim);
//					BayesianEvent evt = this.network.getEvent(parsedEvent.getLabel());
					parsedEvent.setValue(value);
				}  
				
				if( ch==',') {
					parser.advance();
				}
				
				if( ch==']') {
					parser.advance();
				}
				
				if( parsedEvent.getLabel().length()>0 ) {
					results.add(parsedEvent);
				}
				l.setLength(0);
			} else {
				parser.advance();
				l.append(ch);
			}
		}
		
	}
	
	/**
	 * Parse the given line.
	 * @param line
	 * @return The parsed probability.
	 */
	public ParsedProbability parse(String line) {
		
		ParsedProbability result = new ParsedProbability();

		SimpleParser parser = new SimpleParser(line);
		parser.eatWhiteSpace();
		if (!parser.lookAhead("P(", true)) {
			throw new EncogError("Bayes table lines must start with P(");
		}
		parser.advance(2);

		// handle base
		addEvents(parser, result.getBaseEvents(), "|,)=[]");

		// handle conditions
		if (parser.peek() == '|') {
			parser.advance();
			addEvents(parser, result.getGivenEvents(), ",)=[]");

		}

		if (parser.peek() != ')') {
			throw new BayesianError("Probability not properly terminated.");
		}

		return result;
	
	}
	
	/**
	 * Parse a probability list.
	 * @param network The network to parse for.
	 * @param line The line to parse.
	 * @return The parsed list.
	 */
	public static List<ParsedProbability> parseProbabilityList(BayesianNetwork network, String line) {
		List<ParsedProbability> result = new ArrayList<ParsedProbability>(); 
		
		StringBuilder prob = new StringBuilder();
		for(int i=0;i<line.length();i++) {
			char ch = line.charAt(i);
			if( ch==')') {
				prob.append(ch);
				ParseProbability parse = new ParseProbability(network);
				ParsedProbability parsedProbability = parse.parse(prob.toString());
				result.add(parsedProbability);
				prob.setLength(0);
			}
			else {
				prob.append(ch);
			}			
		}
		return result;
	}
}
