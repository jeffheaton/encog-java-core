package org.encog.ml.prg.extension;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.ExpressionError;

public class FunctionFactory {
	
	private final List<ProgramExtension> extensions = new ArrayList<ProgramExtension>();
	private final EncogProgram program;
	
	public FunctionFactory(EncogProgram theProgram) {
		this.program = theProgram;
	}
	
	public void addExtension(final ProgramExtension extension) {
		this.extensions.add(extension);
	}
	
	public NodeFunction factorFunction(String name, ProgramNode[] args) {
		NodeFunction fn = null;
		
		for (final ProgramExtension extension : this.extensions) {			
			fn = extension.factorFunction(this.program, name, args);
			if (fn != null) {
				break;
			}
		}

		if (fn != null) {
			return fn;
		} else {
			throw new ExpressionError("Undefined function/operator: " + name);
		}		
	}
	
}
