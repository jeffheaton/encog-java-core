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
package org.encog.parse.expression.epl;

import java.util.StringTokenizer;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.EncogOpcodeRegistry;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.util.SimpleParser;
import org.encog.util.csv.CSVFormat;
import org.encog.util.datastruct.StackObject;

/**
 * Parse an EPL string.
 */
public class ParseEPL {
	private final EncogProgram holder;
	private SimpleParser parser;
	private StackObject<ProgramNode> nodeStack = new StackObject<ProgramNode>(100);
	
	public ParseEPL(final EncogProgram theHolder) {
		this.holder = theHolder;
	}
	
	public ProgramNode parse(final String expression) {
		this.parser = new SimpleParser(expression);
		
		while(!this.parser.eol()) {
			this.parser.eatWhiteSpace();
			
			// read in the command
			if( this.parser.readChar()!='[' ) {
				throw new EACompileError("Expected [");
			}
			this.parser.eatWhiteSpace();
			StringBuilder cmd = new StringBuilder();
			while(this.parser.peek()!=']' && !this.parser.eol()) {
				cmd.append(this.parser.readChar());
			}
			
			if( this.parser.peek()!=']') {
				throw new EACompileError("Expected ]");
			}
			this.parser.advance();
			
			
			// parse out the command
			StringTokenizer tok = new StringTokenizer(cmd.toString(),":");
			String name = tok.nextToken();
			int childCount = Integer.parseInt(tok.nextToken());
			ProgramExtensionTemplate temp = EncogOpcodeRegistry.INSTANCE.findOpcode(name, childCount);
			if( temp==null ) { 
				throw new EACompileError("Invalid instruction: " + name);
			}
			
			// build the arguments
			ProgramNode[] args = new ProgramNode[childCount];
			for(int i=args.length-1;i>=0;i--) {
				args[i] = this.nodeStack.pop();
			}
			
			// factor the node
			ProgramNode node = this.holder.getFunctions().factorProgramNode(name, this.holder, args);
			this.nodeStack.push(node);
			
			// add any needed data to the node
			for(int i=0;i<temp.getDataSize();i++) {
				String str = tok.nextToken().trim();
				int idx = str.indexOf('#');
				if( idx!=-1) {
					int enumType = Integer.parseInt(str.substring(0,idx));
					int enumVal = Integer.parseInt(str.substring(idx+1));
					node.getData()[0] = new ExpressionValue(enumType,enumVal);
					
				}
				// is it boolean?
				else if( str.length()==1 && "tf".indexOf(Character.toLowerCase(str.charAt(0)))!=-1 ) {
					node.getData()[i] = new ExpressionValue(str.equalsIgnoreCase("t"));
				} 
				// is it a string?
				else if( str.charAt(0)=='\"' ) {
					node.getData()[i] = new ExpressionValue(str.substring(1,str.length()-1));
				}
				// is it an integer
				else if( str.indexOf('.')==-1 && str.toLowerCase().indexOf('e')==-1) {
					long l;
					try {
						l = Long.parseLong(str);
					} catch(NumberFormatException ex) {
						// sometimes Java will output a long value that is larger than can be parsed
						// this is very likely not a useful genome and we just set it to zero so that
						// the population load does not fail.
						l=0;
					}
					node.getData()[i] = new ExpressionValue(l);
				}
				// At this point, must be a float
				else {
					node.getData()[i] = new ExpressionValue(CSVFormat.EG_FORMAT.parse(str));
				}
			}
		}
		
		return this.nodeStack.pop();
	}
}
