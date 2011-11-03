package org.encog.ml.bayesian.parse;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.util.SimpleParser;

public class ParseProbability {
	
	private final BayesianNetwork network;
	
	public ParseProbability(BayesianNetwork theNetwork) {
		this.network = theNetwork;
	}
	
	private void addEvents(SimpleParser parser, List<ParsedEvent> results, String delim) {
		boolean done = false;
		StringBuilder l = new StringBuilder();
		
		while( !done && !parser.eol()) {
			char ch = parser.peek();
			if( delim.indexOf(ch) != -1 ) {
				if( ch==')' || ch=='|') 
					done = true;
									
				ParsedEvent parsedEvent;			
				
				// deal with a value specified by + or -
				if( l.length()>0 && l.charAt(0)=='+' ) {
					String l2 = l.toString().substring(1);
					parsedEvent = new ParsedEvent(l2.trim());
					parsedEvent.setValue("true");
				} else if( l.length()>0 && l.charAt(0)=='-') {
					String l2 = l.toString().substring(1);
					parsedEvent = new ParsedEvent(l2.trim());
					parsedEvent.setValue("false");
				} else {
					String l2 = l.toString();
					parsedEvent = new ParsedEvent(l2.trim());
				}
				
				// deal with a value specified by =
				if( parser.peek()=='=' ) {
					String value = parser.readToChars(delim);
					parsedEvent.setValue(value);
				}  
				
				if( ch==',') {
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
	
	public ParsedProbability parse(String line) {
		
		ParsedProbability result = new ParsedProbability();

		SimpleParser parser = new SimpleParser(line);
		parser.eatWhiteSpace();
		if (!parser.lookAhead("P(", true)) {
			throw new EncogError("Bayes table lines must start with P(");
		}
		parser.advance(2);

		// handle base
		addEvents(parser, result.getBaseEvents(), "|,)=");

		// handle conditions
		if (parser.peek() == '|') {
			parser.advance();
			addEvents(parser, result.getGivenEvents(), ",)=");

		}

		if (parser.peek() != ')') {
			throw new BayesianError("Probability not properly terminated.");
		}

		return result;
	
	}
}
