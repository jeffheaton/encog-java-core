package org.encog.ml.prg.extension;

import java.util.HashMap;
import java.util.Map;

import org.encog.ml.ea.exception.EACompileError;
import org.encog.ml.prg.EncogProgramContext;

public enum EncogOpcodeRegistry {
	INSTANCE;
	
	public static String createKey(String functionName, int argCount) {
		return functionName + '`' + argCount;
	}

	private final Map<String, ProgramExtensionTemplate> registry = new HashMap<String, ProgramExtensionTemplate>();

	private EncogOpcodeRegistry() {
		add(StandardExtensions.EXTENSION_NOT_EQUAL);
		add(StandardExtensions.EXTENSION_NOT);
		add(StandardExtensions.EXTENSION_VAR_SUPPORT);
		add(StandardExtensions.EXTENSION_CONST_SUPPORT);
		add(StandardExtensions.EXTENSION_NEG);
		add(StandardExtensions.EXTENSION_ADD);
		add(StandardExtensions.EXTENSION_SUB);
		add(StandardExtensions.EXTENSION_MUL);
		add(StandardExtensions.EXTENSION_DIV);
		add(StandardExtensions.EXTENSION_POWER);
		add(StandardExtensions.EXTENSION_AND);
		add(StandardExtensions.EXTENSION_OR);
		add(StandardExtensions.EXTENSION_EQUAL);
		add(StandardExtensions.EXTENSION_GT);
		add(StandardExtensions.EXTENSION_LT);
		add(StandardExtensions.EXTENSION_GTE);
		add(StandardExtensions.EXTENSION_LTE);
		add(StandardExtensions.EXTENSION_ABS);
		add(StandardExtensions.EXTENSION_ACOS);
		add(StandardExtensions.EXTENSION_ASIN);
		add(StandardExtensions.EXTENSION_ATAN);
		add(StandardExtensions.EXTENSION_ATAN2);
		add(StandardExtensions.EXTENSION_CEIL);
		add(StandardExtensions.EXTENSION_COS);
		add(StandardExtensions.EXTENSION_COSH);
		add(StandardExtensions.EXTENSION_EXP);
		add(StandardExtensions.EXTENSION_FLOOR);
		add(StandardExtensions.EXTENSION_LOG);
		add(StandardExtensions.EXTENSION_LOG10);
		add(StandardExtensions.EXTENSION_MAX);
		add(StandardExtensions.EXTENSION_MIN);
		add(StandardExtensions.EXTENSION_POWFN);
		add(StandardExtensions.EXTENSION_RANDOM);
		add(StandardExtensions.EXTENSION_ROUND);
		add(StandardExtensions.EXTENSION_SIN);
		add(StandardExtensions.EXTENSION_SINH);
		add(StandardExtensions.EXTENSION_SQRT);
		add(StandardExtensions.EXTENSION_TAN);
		add(StandardExtensions.EXTENSION_TANH);
		add(StandardExtensions.EXTENSION_TODEG);
		add(StandardExtensions.EXTENSION_TORAD);
		add(StandardExtensions.EXTENSION_LENGTH);
		add(StandardExtensions.EXTENSION_FORMAT);
		add(StandardExtensions.EXTENSION_LEFT);
		add(StandardExtensions.EXTENSION_RIGHT);
		add(StandardExtensions.EXTENSION_CINT);
		add(StandardExtensions.EXTENSION_CFLOAT);
		add(StandardExtensions.EXTENSION_CSTR);
		add(StandardExtensions.EXTENSION_CBOOL);
		add(StandardExtensions.EXTENSION_IFF);
		add(StandardExtensions.EXTENSION_CLAMP);
	}

	public void add(final ProgramExtensionTemplate ext) {
		this.registry.put(EncogOpcodeRegistry.createKey(ext.getName(), ext.getChildNodeCount()), ext);
	}

	public void register(final EncogProgramContext context, final int opcode) {
		if (!this.registry.containsKey(opcode)) {
			throw new EACompileError("Unknown opcode: " + opcode);
		}
		final ProgramExtensionTemplate temp = this.registry.get(opcode);
		context.getFunctions().addExtension(temp);
	}

	public ProgramExtensionTemplate findOpcode(String name, int args) {
		String key = EncogOpcodeRegistry.createKey(name, args);
		if( this.registry.containsKey(key) ) {
			return this.registry.get(key);
		} else {
			return null;
		}
	}

}
