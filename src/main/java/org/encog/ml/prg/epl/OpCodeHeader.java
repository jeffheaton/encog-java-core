package org.encog.ml.prg.epl;

import java.io.Serializable;

public class OpCodeHeader implements Serializable {
	private short opcode; 
	private int param1;
	private short param2;
	
	/**
	 * @return the opcode
	 */
	public short getOpcode() {
		return opcode;
	}
	/**
	 * @param opcode the opcode to set
	 */
	public void setOpcode(short opcode) {
		this.opcode = opcode;
	}
	/**
	 * @return the param1
	 */
	public int getParam1() {
		return param1;
	}
	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(int param1) {
		this.param1 = param1;
	}
	/**
	 * @return the param2
	 */
	public short getParam2() {
		return param2;
	}
	/**
	 * @param param2 the param2 to set
	 */
	public void setParam2(short param2) {
		this.param2 = param2;
	}
	
	
}
