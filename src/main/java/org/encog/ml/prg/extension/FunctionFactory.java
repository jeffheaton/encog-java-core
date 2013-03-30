package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ExpressionError;
import org.encog.ml.prg.ProgramNode;

public class FunctionFactory implements Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	private final Map<String, ProgramExtensionTemplate> templateMap = new HashMap<String, ProgramExtensionTemplate>();
	private final List<ProgramExtensionTemplate> opcodes = new ArrayList<ProgramExtensionTemplate>();
	
	public ProgramNode factorFunction(ProgramExtensionTemplate temp, EncogProgram program,
			ProgramNode[] args) {
		return new ProgramNode(program, temp, args);
	}

	public ProgramNode factorFunction(String name, EncogProgram program,
			ProgramNode[] args) {

		String key = EncogOpcodeRegistry.createKey(name, args.length);

		if (!this.templateMap.containsKey(key)) {
			throw new ExpressionError("Undefined function/operator: " + name
					+ " with " + args.length + " args.");
		}

		ProgramExtensionTemplate temp = this.templateMap.get(key);
		return new ProgramNode(program, temp, args);
	}

	public void addExtension(ProgramExtensionTemplate ext) {
		String key = EncogOpcodeRegistry.createKey(ext.getName(),
				ext.getChildNodeCount());
		this.templateMap.put(key, ext);
		this.opcodes.add(ext);
	}

	public boolean isDefined(String name, int l) {
		String key = EncogOpcodeRegistry.createKey(name, l);
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

	private ProgramExtensionTemplate findOperatorExact(String str) {
		for (ProgramExtensionTemplate opcode : opcodes) {
			// only consider operators
			if (opcode.getNodeType() == NodeType.OperatorLeft
					|| opcode.getNodeType() == NodeType.OperatorRight) {
				if (opcode.getName().equals(str)) {
					return opcode;
				}
			}
		}
		return null;
	}

	/**
	 * This method is used when parsing an expression. Consider x>=2. The parser
	 * first sees the > symbol. But it must also consider the =. So we first
	 * look for a 2-char operator, in this case there is one.
	 * 
	 * @param ch1 The first character of the potential operator.
	 * @param ch2 The second character of the potential operator.  Zero if none.
	 * @return The expression template for the operator found.
	 */
	public ProgramExtensionTemplate findOperator(char ch1, char ch2) {
		ProgramExtensionTemplate result = null;

		// if ch2==0 then we are only looking for a single char operator.
		// this is rare, but supported.
		if (ch2 == 0) {
			return findOperatorExact("" + ch1);
		}

		// first, see if we can match an operator with both ch1 and ch2
		result = findOperatorExact("" + ch1 + ch2);

		if (result == null) {
			// okay no 2-char operators matched, so see if we can find a single
			// char
			result = findOperatorExact("" + ch1);
		}

		// return the operator if we have one.
		return result;
	}

	public ProgramExtensionTemplate findFunction(String name) {
		for (ProgramExtensionTemplate opcode : opcodes) {
			// only consider functions
			if (opcode.getNodeType() == NodeType.Function ) {
				if (opcode.getName().equals(name)) {
					return opcode;
				}
			}
		}
		return null;
	}

	public void addExtension(String name, int args) {
		String key = EncogOpcodeRegistry.createKey(name, args);
		if( !this.templateMap.containsKey(key) ) {
			ProgramExtensionTemplate temp = EncogOpcodeRegistry.INSTANCE.findOpcode(name,args);
			if( temp==null ) {
				throw new EACompileError("Unknown extension " + name + " with " + args + " arguments.");
			}
			this.addExtension(temp);
		}
		
	}
}
