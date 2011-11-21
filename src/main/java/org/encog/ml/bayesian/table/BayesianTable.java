package org.encog.ml.bayesian.table;

import java.io.Serializable;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.bayesian.BayesianError;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;

public class BayesianTable implements Serializable {
	private final BayesianEvent event;
	private TableLine[] lines;
	private int currentLine;

	public BayesianTable(BayesianEvent theEvent) {
		this.event = theEvent;
		finalizeStructure();
	}
	
	public void reset() {
		List<BayesianEvent> parents = this.event.getParents();
		
		int[] args = new int[parents.size()];
		
		do {
			for(int k = 0; k<event.getChoices().length; k++) {
				addLine(0,k,args);
			}
		} while(EnumerationQuery.roll(parents, args));
	}
	
	public void addLine(double prob, boolean result, boolean... args) {
		int[] d = new int[args.length];
		for(int i=0;i<args.length;i++) {
			d[i] = args[i] ? 0 : 1;
		}
		
		addLine(prob,result?0:1,d);
		addLine(1.0 - prob,result?1:0,d);
	}
	
	public void addLine(double prob, int result, boolean... args) {
		int[] d = new int[args.length];
		for(int i=0;i<args.length;i++) {
			d[i] = args[i] ? 0 : 1;
		}
		
		addLine(prob,result,d);
	}

	public void addLine(double prob, int result, int ... args) {
		if (args.length != this.event.getParents().size()) {
			throw new BayesianError("Truth table line with " + args.length
					+ ", specied for event with "
					+ this.event.getParents().size()
					+ " parents.  These numbers must be the same");
		}
				
		TableLine line = findLine(result,args);
		
		if( line==null ) {
			if( this.currentLine>= this.lines.length) {
				throw new BayesianError("This truth table is already full.");
			}

			line = new TableLine(prob, result, args);
			this.lines[this.currentLine++] = line;
		}
		else {
			line.setProbability(prob);
		}
	}

	public void validate() {
		if( this.currentLine!=this.lines.length) {
			throw new BayesianError("Truth table for " + this.event.toString() + " only has " + this.currentLine + " line(s), should have " + this.lines.length + " line(s).");
		}
		
	}
	
	public int generateRandom(int ... args) {
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

	public TableLine findLine(int result, int[] args) {
		
		for (TableLine line : this.lines) {
			if ( line!=null && line.compareArgs(args)) {
				if (Math.abs(line.getResult() - result) < Encog.DEFAULT_DOUBLE_EQUAL) {
					return line;
				}
			}
		}
		
		return null;
	}

	public void finalizeStructure() {
		int idealLength = event.calculateParameterCount()*event.getChoices().length;
		int actualLength = (this.lines==null)?0:this.lines.length;
		if( actualLength!=idealLength ) {
			this.lines = new TableLine[idealLength];
		}
		
	}
}
