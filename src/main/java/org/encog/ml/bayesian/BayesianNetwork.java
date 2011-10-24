package org.encog.ml.bayesian;

import java.util.HashMap;
import java.util.Map;

public class BayesianNetwork {
	
	public static final String[] CHOICES_TRUE_FALSE = { "false", "true" };
	
	private final Map<String,BayesianEvent> events  = new HashMap<String,BayesianEvent>();

	/**
	 * @return the events
	 */
	public Map<String, BayesianEvent> getEvents() {
		return events;
	}
	
	public BayesianEvent getEvent(String label) {
		return this.events.get(label);
	}
	
	public BayesianEvent getEventError(String label) {
		if( !eventExists(label) )
			throw(new BayesianError("Undefined label: " + label));
		return this.events.get(label);
	}
	
	public boolean eventExists(String label) {
		return this.events.containsKey(label);
	}
	
	public BayesianEvent createEvent(String label) {
		if( eventExists(label)) {
			throw new BayesianError("The label \"" + label + "\" has already been defined.");
		}
		BayesianEvent event = new BayesianEvent(label);
		this.events.put(label, event);
		return event;		
	}
	
	public void createDependancy(BayesianEvent parentEvent, BayesianEvent childEvent) {
		parentEvent.addChild(childEvent);
		childEvent.addParent(parentEvent);
	}
	
	public void createDependancy(BayesianEvent parentEvent, BayesianEvent ... children) {
		for(BayesianEvent childEvent: children ) {
			parentEvent.addChild(childEvent);
			childEvent.addParent(parentEvent);
		}
	}
	
	public void createDependancy(String parentEventLabel, String childEventLabel) {
		BayesianEvent parentEvent = getEventError(parentEventLabel);
		BayesianEvent childEvent = getEventError(childEventLabel);
		createDependancy(parentEvent,childEvent);		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		
		// display only those with no parents
		for( BayesianEvent e : this.events.values() ) {
			if( !e.hasParents( ) ) {
				if( !first ) 
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}
		
		// display only those with parents and children
		for( BayesianEvent e : this.events.values() ) {
			if( e.hasParents( ) && e.hasChildren() ) {
				if( !first ) 
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}
		
		// display only those with parents and no children
		for( BayesianEvent e : this.events.values() ) {
			if( e.hasParents( ) && !e.hasChildren() ) {
				if( !first ) 
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}

		return result.toString();
	}
	
	public int calculateParameterCount() {
		int result = 0;
		for( BayesianEvent e: this.events.values()) {
			result+=e.calculateParameterCount();
		}
		return result;
	}

	public void finalizeStructure() {
		for( BayesianEvent e: this.events.values()) {
			e.finalizeStructure();
		}
	}

	public void validate() {
		for( BayesianEvent e: this.events.values()) {
			e.validate();
		}
	}
}
