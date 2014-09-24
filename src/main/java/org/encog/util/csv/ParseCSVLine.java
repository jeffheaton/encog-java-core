/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util.csv;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.SimpleParser;

public class ParseCSVLine {
	
	private CSVFormat format;
	
	public ParseCSVLine(CSVFormat theFormat)
	{
		this.format = theFormat;
	}
	
	public List<String> parse(final String line) {
		if( this.format.getSeparator()==' ') {
			return parseSpaceSep(line);
		} else {
			return parseCharSep(line);
		}
	}
	
	private List<String> parseSpaceSep(final String line) {
		final List<String> result = new ArrayList<String>();
		SimpleParser parse  = new SimpleParser(line);
		
		while(!parse.eol()) {
			if( parse.peek()=='\"') {
				result.add( parse.readQuotedString() );
			} else {
				result.add( parse.readToWhiteSpace() );
			}
			parse.eatWhiteSpace();
		}
		
		return result;
	}

	/**
	 * Parse the line into a list of values.
	 * 
	 * @param line
	 *            The line to parse.
	 * @return The elements on this line.
	 */
	private List<String> parseCharSep(final String line) {
		final StringBuilder item = new StringBuilder();
		final List<String> result = new ArrayList<String>();
		boolean quoted = false;
		boolean hadQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			final char ch = line.charAt(i);
			if ((ch == this.format.getSeparator()) && !quoted) {
				String s = item.toString();
				if( !hadQuotes ) {
					s = s.trim();
				}
				result.add(s);
				item.setLength(0);
				quoted = false;
				hadQuotes = false;
			} else if ((ch == '\"') && quoted) {
				if( (i+1)<line.length() && line.charAt(i+1)=='\"' ) {
					i++;
					item.append("\"");
				} else {
					quoted = false;
				}
			} else if ((ch == '\"') && (item.length() == 0)) {
				hadQuotes = true;
				quoted = true;
			} else {
				item.append(ch);
			}
		}

		if (item.length() > 0) {
			String s = item.toString();
			if( !hadQuotes ) {
				s = s.trim();
			}
			result.add(s);
		}

		return result;
	}

}
