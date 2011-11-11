package org.encog.ml.bayesian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.table.BayesianTable;

public class BayesianEvent implements Serializable {
	
	private final String label;
	
	/**
	 * The parents, or given.
	 */
	private final List<BayesianEvent> parents = new ArrayList<BayesianEvent>();
	
	/**
	 * THe children, or events that use us as a given.
	 */
	private final List<BayesianEvent> children = new ArrayList<BayesianEvent>();
	private final String[] choices;
	private BayesianTable table;
	
	public BayesianEvent(String theLabel, String[] theChoices) {
		this.label = theLabel;
		this.choices = theChoices;		
	}
	
	public BayesianEvent(String theLabel) {
		this(theLabel,BayesianNetwork.CHOICES_TRUE_FALSE);
	}

	/**
	 * @return the parents
	 */
	public List<BayesianEvent> getParents() {
		return parents;
	}
	/**
	 * @return the children
	 */
	public List<BayesianEvent> getChildren() {
		return children;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	public void addChild(BayesianEvent e) {
		this.children.add(e);
	}	
	
	public void addParent(BayesianEvent e) {
		this.parents.add(e);
	}
	
	public boolean hasParents() {
		return this.parents.size()>0;
	}
	
	public boolean hasChildren() {
		return this.parents.size()>0;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("P(");
		result.append(this.getLabel());
		
		if( hasParents() ) {
			result.append("|");
		}
		
		boolean first = true;
		for(BayesianEvent e : this.parents) {
			if( !first )
				result.append(",");
			first = false;
			result.append(e.getLabel());
		}
		
		result.append(")");
		return result.toString();
	}
	
	public int calculateParameterCount() {
		int result = 1;
		
		for(BayesianEvent parent: this.parents) {
			result *= parent.getChoices().length;
		}
		
		return result;
	}

	/**
	 * @return the choices
	 */
	public String[] getChoices() {
		return choices;
	}

	/**
	 * @return the table
	 */
	public BayesianTable getTable() {
		return table;
	}

	public void finalizeStructure() {
		if( this.table == null ) {
			this.table = new BayesianTable(this);
			this.table.reset();
		} else {
			this.table.finalizeStructure();
		}
		
	}

	public void validate() {
		this.table.validate();
	}

	public boolean isBoolean() {
		return this.choices.length==2;
	}

	public boolean rollArgs(double[] args) {
		int currentIndex = 0;
		boolean done = false;
		boolean eof = false;
		
		if( this.parents.size() == 0 ) {
			done = true;
			eof = true;
		}

		while (!done) {

			//EventState state = this.parents.get(currentIndex);
			int v = (int) args[currentIndex];
			v++;
			if (v >= this.parents.get(currentIndex).getChoices().length) {
				args[currentIndex] = 0;
			} else {
				args[currentIndex] = v;
				done = true;
				break;
			}

			currentIndex++;

			if (currentIndex >= this.parents.size()) {
				done = true;
				eof = true;
			}
		}

		return !eof;
	}

	public void removeAllRelations() {
		this.children.clear();
		this.parents.clear();		
	}	
	
	public static String formatEventName(BayesianEvent event, int value) {
		StringBuilder str = new StringBuilder();
		
		if (event.isBoolean()) {
			if (value==0) {
				str.append("-");
			} else {
				str.append("+");
			}
		}
		str.append(event.getLabel());
		if (!event.isBoolean()) {
			str.append("=");
			str.append(value);
		}
		
		return str.toString();

	}

	public boolean hasGiven(String l) {
		for(BayesianEvent event: this.parents ) {
			if( event.getLabel().equals(l)) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		if( this.table==null ) {
			this.table = new BayesianTable(this);
		}
		this.table.reset();
	}
}
