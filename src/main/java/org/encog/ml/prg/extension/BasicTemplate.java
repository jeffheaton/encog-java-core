package org.encog.ml.prg.extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Random;

import org.encog.EncogError;
import org.encog.ml.prg.EncogProgram;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.file.ResourceInputStream;

public abstract class BasicTemplate implements ProgramExtensionTemplate, Serializable {
	private short opcode;
	private boolean variableValue; 
	private int childNodeCount;
	private String name;
	
	
	public BasicTemplate(short opcode) {
		InputStream is = null;
		
		try {
			is = ResourceInputStream.openResourceInputStream("org/encog/data/epl_opcodes.csv");
			ReadCSV csv = new ReadCSV(is,true,CSVFormat.EG_FORMAT);

			while(csv.next()) {
				if( csv.getInt(0)==opcode) {
					this.opcode = opcode;
					this.name = csv.get(1);
					this.childNodeCount = csv.getInt(2);
					this.variableValue = csv.get(3).equalsIgnoreCase("T");
					return;
				}
			}
			
			throw new EncogError("Opcode " + opcode + " is not defined as an internal Encog function.");
		} finally {
			if( is!=null ) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public BasicTemplate(String theName, int theChildNodeCount, boolean theVariableValue) {
		this.name = theName;
		this.childNodeCount = theChildNodeCount;
		this.variableValue = theVariableValue;
	}

	@Override
	public void randomize(Random r, EncogProgram program, double degree) {
		program.writeNode(this.opcode);
	}
	
	/**
	 * @return the opcode
	 */
	@Override
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
	 * @return the variableValue
	 */
	@Override
	public boolean isVariableValue() {
		return variableValue;
	}


	/**
	 * @param variableValue the variableValue to set
	 */
	public void setVariableValue(boolean variableValue) {
		this.variableValue = variableValue;
	}


	/**
	 * @return the childNodeCount
	 */
	@Override
	public int getChildNodeCount() {
		return childNodeCount;
	}


	/**
	 * @param childNodeCount the childNodeCount to set
	 */
	public void setChildNodeCount(int childNodeCount) {
		this.childNodeCount = childNodeCount;
	}


	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	

	
}
