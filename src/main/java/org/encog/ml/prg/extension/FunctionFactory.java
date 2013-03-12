package org.encog.ml.prg.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;

public class FunctionFactory {
	
	private final Map<String,ProgramExtensionTemplate> templateMap = new HashMap<String,ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> opcodes = new ArrayList<ProgramExtensionTemplate>();
	
	public static String createKey(String functionName, int argCount) {
		return functionName + '`' + argCount;
	}
	
	public ProgramNode factorFunction(String name, EncogProgram program, ProgramNode[] args) {
		ProgramNode fn = null;
		
		String key = FunctionFactory.createKey(name, args.length);
		
		if( !this.templateMap.containsKey(key) ) {
			throw new ExpressionError("Undefined function/operator: " + name + " with " + args.length + " args.");
		}
		
		ProgramExtensionTemplate temp = this.templateMap.get(key);
		return new ProgramNode(program, temp, args);
	}

	public void addExtension(ProgramExtensionTemplate ext) {
		String key = FunctionFactory.createKey(ext.getName(), ext.getChildNodeCount());
		this.templateMap.put(key, ext);
		this.opcodes.add(ext);
	}

	public boolean isDefined(String name, int l) {
		String key = FunctionFactory.createKey(name, l);
		return this.templateMap.containsKey(key);
	}

	public int size() {
		return this.opcodes.size();
	}
	
	public ProgramExtensionTemplate getOpCode(int theOpCode) {
		return this.opcodes.get(theOpCode);
	}

	public List<ProgramExtensionTemplate> getOpCodes() {
		return this.opcodes;
	}
}
