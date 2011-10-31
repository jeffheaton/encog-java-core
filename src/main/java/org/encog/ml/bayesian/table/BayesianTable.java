package org.encog.ml.bayesian.table;

import java.io.Serializable;

import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;

public class BayesianTable implements Serializable {
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
			d[i] = args[i] ? 1.0 : 0.0;
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
	
	public double generateRandom(double ... args) {
		double r = Math.random();
		double limit = 0;
		
		for(TableLine line: this.lines) {
			if( line!=null && line.compareArgs(args)) {
				limit+=line.getProbability();
				if( r<limit ) {
					return line.getResult();
				}
			}
		}
		
		throw new BayesianError("Incomplete logic table for event: " + this.event.toString());
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(TableLine line: this.lines) {
			result.append(line.toString());
			result.append("\n");
		}
		return result.toString();
	}

	public TableLine[] getLines() {
		return this.lines;
	}
}
