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
	public static String createKey(final String functionName, final int argCount) {
		return functionName + '`' + argCount;
	}

	private final Map<String, ProgramExtensionTemplate> templateMap = new HashMap<String, ProgramExtensionTemplate>();
	private final ProgramExtensionTemplate[] standardOpcodes = new ProgramExtensionTemplate[32767];

	private final Map<String, Short> knownConsts = new HashMap<String, Short>();

	public void addExtension(final ProgramExtensionTemplate ext) {
		final String key = FunctionFactory.createKey(ext.getName(),
				ext.getChildNodeCount());
		this.templateMap.put(key, ext);
		this.standardOpcodes[ext.getOpcode()] = ext;
	}

	public void defineKnownConst(final short opcode, final String name,
			final ExpressionValue value) {
		if (this.knownConsts.containsKey(name)) {
			throw new EncogError("Const already defined: " + name);
		}
		final KnownConst temp = new KnownConst(opcode, name, value);
		addExtension(temp);
		this.knownConsts.put(name, opcode);
	}

	public List<ProgramExtensionTemplate> generateOpcodeList() {
		final List<ProgramExtensionTemplate> result = new ArrayList<ProgramExtensionTemplate>();
		for (int i = 0; i <= OpCodeHeader.ENCOG_OPCODE_END; i++) {
			final ProgramExtensionTemplate temp = this.standardOpcodes[i];
			if (temp != null) {
				result.add(this.standardOpcodes[i]);
			}
		}
		return result;
	}

	/**
	 * @return the knownConsts
	 */
	public Map<String, Short> getKnownConsts() {
		return this.knownConsts;
	}

	public ProgramExtensionTemplate getOpCode(final int theOpCode) {
		if (theOpCode < 0 || theOpCode > this.standardOpcodes.length
				|| this.standardOpcodes[theOpCode] == null) {
			throw new EncogEPLError("Undefined opcode: " + theOpCode);
		}

		return this.standardOpcodes[theOpCode];
	}

	public short getOpCode(final String name, final int acnt) {
		final String key = FunctionFactory.createKey(name, acnt);
		if (!this.templateMap.containsKey(key)) {
			throw new EncogEPLError("No function defined named " + name
					+ " and " + acnt + " arguments.");
		}
		return this.templateMap.get(key).getOpcode();
	}

	public Collection<ProgramExtensionTemplate> getOpCodes() {
		return this.templateMap.values();
	}

	public boolean isDefined(final String name, final int l) {
		final String key = FunctionFactory.createKey(name, l);
		return this.templateMap.containsKey(key);
	}

	public int size() {
		return this.templateMap.size();
	}

}
