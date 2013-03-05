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

public abstract class BasicTemplate implements ProgramExtensionTemplate,
		Serializable {
	private short opcode;
	private boolean variableValue;
	private int childNodeCount;
	private String name;
	private boolean operator;

	public BasicTemplate(final short opcode) {
		InputStream is = null;

		try {
			is = ResourceInputStream
					.openResourceInputStream("org/encog/data/epl_opcodes.csv");
			final ReadCSV csv = new ReadCSV(is, true, CSVFormat.EG_FORMAT);

			while (csv.next()) {
				if (csv.getInt(0) == opcode) {
					this.opcode = opcode;
					this.name = csv.get(1);
					this.childNodeCount = csv.getInt(2);
					this.variableValue = csv.get(3).equalsIgnoreCase("T");
					this.operator = csv.get(4).equalsIgnoreCase("T");
					return;
				}
			}

			throw new EncogError("Opcode " + opcode
					+ " is not defined as an internal Encog function.");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public BasicTemplate(final String theName, final int theChildNodeCount,
			final boolean theVariableValue) {
		this.name = theName;
		this.childNodeCount = theChildNodeCount;
		this.variableValue = theVariableValue;
	}

	/**
	 * @return the childNodeCount
	 */
	@Override
	public int getChildNodeCount() {
		return this.childNodeCount;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return the opcode
	 */
	@Override
	public short getOpcode() {
		return this.opcode;
	}

	@Override
	public boolean isOperator() {
		return this.operator;
	}

	/**
	 * @return the variableValue
	 */
	@Override
	public boolean isVariableValue() {
		return this.variableValue;
	}

	@Override
	public void randomize(final Random r, final EncogProgram program,
			final double degree) {
		program.writeNode(this.opcode);
	}

	/**
	 * @param childNodeCount
	 *            the childNodeCount to set
	 */
	public void setChildNodeCount(final int childNodeCount) {
		this.childNodeCount = childNodeCount;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param opcode
	 *            the opcode to set
	 */
	public void setOpcode(final short opcode) {
		this.opcode = opcode;
	}

	/**
	 * @param variableValue
	 *            the variableValue to set
	 */
	public void setVariableValue(final boolean variableValue) {
		this.variableValue = variableValue;
	}

}
