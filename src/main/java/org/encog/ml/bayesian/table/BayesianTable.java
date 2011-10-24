package org.encog.ml.bayesian.table;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;

public class BayesianTable {
	private final BayesianEvent event;
	private final TableLine[] lines;
	private int currentLine;

	public BayesianTable(BayesianEvent theEvent) {
		this.event = theEvent;
		this.lines = new TableLine[theEvent.calculateParameterCount()*theEvent.getChoices().length];
	}
	
	public void addLine(double prob, boolean result, boolean... args) {
		double[] d = new double[args.length];
		for(int i=0;i<args.length;i++) {
			d[i] = args[i] ? 0.0 : 1.0;
		}
		
		addLine(prob,result?1.0:0.0,d);
		addLine(1.0 - prob,result?0.0:1.0,d);
	}
	
	public void addLine(double prob, double result, boolean... args) {
		double[] d = new double[args.length];
		for(int i=0;i<args.length;i++) {
			d[i] = args[i] ? 0.0 : 1.0;
		}
		
		addLine(prob,result,d);
	}

	public void addLine(double prob, double result, double... args) {
		if (args.length != this.event.getParents().size()) {
			throw new BayesianError("Truth table line with " + args.length
					+ ", specied for event with "
					+ this.event.getParents().size()
					+ " parents.  These numbers must be the same");
		}
		
		if( this.currentLine>= this.lines.length) {
			throw new BayesianError("This truth table is already full.");
		}
		
		TableLine line = new TableLine(prob, result, args);
		this.lines[this.currentLine++] = line;		
	}

	public void validate() {
		if( this.currentLine!=this.lines.length) {
			throw new BayesianError("Truth table for " + this.event.toString() + " only has " + this.currentLine + " line(s), should have " + this.lines.length + " line(s).");
		}
		
	}
}
