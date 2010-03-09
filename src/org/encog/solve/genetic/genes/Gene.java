package org.encog.solve.genetic.genes;

public interface Gene extends Comparable<Gene> {
	public void copy(Gene gene);
	public int getId();
	public long getInnovationId();
	public boolean isEnabled();
	public void setEnabled(boolean e);
}
