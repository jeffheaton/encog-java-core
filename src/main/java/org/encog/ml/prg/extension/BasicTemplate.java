package org.encog.ml.prg.extension;

import java.util.Random;

import org.encog.ml.prg.ProgramNode;

public abstract class BasicTemplate implements ProgramExtensionTemplate {

	private final String name;
	private final int childNodeCount;
	private final boolean varValue;
	private final int dataSize;

	public BasicTemplate(final String theName, final int childCount,
			final boolean isVariable, int theDataSize) {
		this.name = theName;
		this.childNodeCount = childCount;
		this.varValue = isVariable;
		this.dataSize = theDataSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildNodeCount() {
		return this.childNodeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isVariable() {
		return this.varValue;
	}
	
	@Override
	public int getDataSize() {
		return this.dataSize;
	}

	@Override
	public void randomize(final Random rnd, final ProgramNode actual, final double degree) {

	}
}
