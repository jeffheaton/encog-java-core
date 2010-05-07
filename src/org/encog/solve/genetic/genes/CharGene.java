package org.encog.solve.genetic.genes;

public class CharGene extends BasicGene {

	private char value;
	
	@Override
	public void copy(Gene gene) {
		this.value = ((CharGene)gene).getValue();
	}
	
	public String toString()
	{
		return ""+value;
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}
}
