package org.encog.ml.prg.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.exception.EncogEPLError;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class FunctionFactory implements Serializable {
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
			throw new EncogEPLError("Undefined opcode: " + theOpCode);
		}
		
		return this.standardOpcodes[theOpCode];
	}

	public short getOpCode(String name, int acnt) {
		String key = FunctionFactory.createKey(name,acnt);
		if( !this.templateMap.containsKey(key) ) {
			throw new EncogEPLError("No function defined named " + name + " and " + acnt + " arguments.");
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

	public List<ProgramExtensionTemplate> generateOpcodeList() {
		List<ProgramExtensionTemplate> result = new ArrayList<ProgramExtensionTemplate>();
		for(int i=0;i<=OpCodeHeader.ENCOG_OPCODE_END;i++) {
			ProgramExtensionTemplate temp = this.standardOpcodes[i];
			if( temp!=null ) {
				result.add(this.standardOpcodes[i]);
			}
		}
		return result;
	}
	
	
}
