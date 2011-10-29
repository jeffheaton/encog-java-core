package org.encog.mathutil.probability;

import java.util.ArrayList;
import java.util.List;

public class CalcProbability {
	private final List<Integer> classes = new ArrayList<Integer>();
	private final int laplacianSmoothing;
	private int total;

	public CalcProbability(int k) {
		super();
		this.laplacianSmoothing = k;
	}
	
	public CalcProbability() {
		this(0);
	}
	
	public void addClass(int items) {
		total+=items;
		this.classes.add(items);
	}
	
	public int getClassCount() {
		return this.classes.size();
	}
	
	public double calculate(int classNumber) {
		double classItems = this.classes.get(classNumber);
		double totalItems = this.total;
		double d = ((double)this.laplacianSmoothing*(double)classes.size());
		return (classItems + ((double)this.laplacianSmoothing)) / (totalItems+d);
	}
}
