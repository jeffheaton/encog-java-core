package org.encog.ml.prg.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.NodeFunction;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.ExpressionError;

public class FunctionFactory {
	
	private final Map<String,ProgramExtensionTemplate> templateMap = new HashMap<String,ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> opcodes = new ArrayList<ProgramExtensionTemplate>();
	private final EncogProgram program;
	
	public static String createKey(String functionName, int argCount) {
		return functionName + '`' + argCount;
	}
	
	public FunctionFactory(EncogProgram theProgram) {
		this.program = theProgram;
	}
	
	public NodeFunction factorFunction(String name, ProgramNode[] args) {
		NodeFunction fn = null;
		
		String key = FunctionFactory.createKey(name, args.length);
		
		if( !this.templateMap.containsKey(key) ) {
			throw new ExpressionError("Undefined function/operator: " + name + " with " + args.length + " args.");
		}
		
		ProgramExtensionTemplate temp = this.templateMap.get(key);
		fn = temp.factorFunction(this.program, name, args);
		
		return fn;
	}

	public void addExtension(ProgramExtensionTemplate ext) {
		String key = FunctionFactory.createKey(ext.getName(), ext.getChildNodeCount());
		this.templateMap.put(key, ext);
		this.opcodes.add(ext);
	}
	
}
