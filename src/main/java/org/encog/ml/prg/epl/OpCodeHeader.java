package org.encog.ml.prg.epl;

import java.io.Serializable;

public class OpCodeHeader implements Serializable {

	public static short ENCOG_OPCODE_BEGIN = 0;
	public static short ENCOG_OPCODE_END = 9999;
	public static short ENCOG_KNOWN_CONST_BEGIN = 10000;
	public static short ENCOG_KNOWN_CONST_END = 14999;
	public static short ENCOG_EXTRA_OPCODES_BEGIN = 15000;
	public static short USER_DEFINED_OPCODES_BEGIN = 20000;
	public static short USER_DEFINED_OPCODES_END = 29999;

	private short opcode;
	private int param1;
	private short param2;

	/**
	 * @return the opcode
	 */
	public short getOpcode() {
		return this.opcode;
	}

	/**
	 * @return the param1
	 */
	public int getParam1() {
		return this.param1;
	}

	/**
	 * @return the param2
	 */
	public short getParam2() {
		return this.param2;
	}

	/**
	 * @param opcode
	 *            the opcode to set
	 */
	public void setOpcode(final short opcode) {
		this.opcode = opcode;
	}

	/**
	 * @param param1
	 *            the param1 to set
	 */
	public void setParam1(final int param1) {
		this.param1 = param1;
	}

	/**
	 * @param param2
	 *            the param2 to set
	 */
	public void setParam2(final short param2) {
		this.param2 = param2;
	}

}
