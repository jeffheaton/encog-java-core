package org.encog.solve.genetic.genes;


public class DoubleGene extends BasicGene {
	private double value;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void copy(Gene gene) {
		this.value = ((DoubleGene)gene).getValue();
		
	}
	
	public String toString()
	{
		return ""+value;
	}
}
