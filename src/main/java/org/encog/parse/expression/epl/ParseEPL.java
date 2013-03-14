package org.encog.parse.expression.epl;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;
import org.encog.util.SimpleParser;

public class ParseEPL {
	private final EncogProgram holder;
	private SimpleParser parser;
	private ProgramNode rootNode;
	
	public ParseEPL(final EncogProgram theHolder) {
		this.holder = theHolder;
	}
	
	public ProgramNode parse(final String expression) {
		this.parser = new SimpleParser(expression);
		
		while(!this.parser.eol()) {
			this.parser.eatWhiteSpace();
			if( this.parser.readChar()!='[' ) {
				throw new ExpressionError("Expected [");
			}
			StringBuilder cmd = new StringBuilder();
			while(this.parser.peek()!=']') {
				cmd.append(this.parser.readChar());
			}
		}
		
		return this.rootNode;
	}
}
