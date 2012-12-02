package org.encog.ml.prg.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class FunctionFactory {
	public static short USER_DEFINED_OPCODES = 20000;
	public static short ENCOG_EXTRA_OPCODES = 15000;
	private final Map<String,ProgramExtensionTemplate> templateMap = new HashMap<String,ProgramExtensionTemplate>();
	private final ProgramExtensionTemplate[] standardOpcodes = new ProgramExtensionTemplate[32767];
	private final Map<String,Short> knownConsts = new HashMap<String,Short>();
	
	public static String createKey(String functionName, int argCount) {
		return functionName + '`' + argCount;
	}

	public void addExtension(ProgramExtensionTemplate ext) {
		String key = FunctionFactory.createKey(ext.getName(), ext.getChildNodeCount());
		this.templateMap.put(key, ext);
		this.standardOpcodes[ext.getOpcode()] = ext;
	}

	public boolean isDefined(String name, int l) {
		String key = FunctionFactory.createKey(name, l);
		return this.templateMap.containsKey(key);
	}

	public int size() {
		return this.templateMap.size();
	}
	
	public ProgramExtensionTemplate getOpCode(int theOpCode) {
		if( theOpCode<0 || theOpCode>this.standardOpcodes.length || this.standardOpcodes[theOpCode]==null  ) {
			throw new EncogError("Undefined opcode: " + theOpCode);
		}
		
		return this.standardOpcodes[theOpCode];
	}

	public short getOpCode(String name, int acnt) {
		String key = FunctionFactory.createKey(name,acnt);
		if( !this.templateMap.containsKey(key) ) {
			throw new EncogError("No function defined named " + name + " and " + acnt + " arguments.");
		}
		return this.templateMap.get(key).getOpcode();
	}

	public Collection<ProgramExtensionTemplate> getOpCodes() {
		return this.templateMap.values();
	}
	
	public void defineKnownConst(final short opcode, String name, ExpressionValue value) {
		if( this.knownConsts.containsKey(name) ) {
			throw new EncogError("Const already defined: " + name);
		}
		KnownConst temp = new KnownConst(opcode,name,value);
		addExtension(temp);
		this.knownConsts.put(name,opcode);
	}

	/**
	 * @return the knownConsts
	 */
	public Map<String,Short> getKnownConsts() {
		return knownConsts;
	}
	
	
}
