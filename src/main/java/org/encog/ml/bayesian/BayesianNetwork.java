package org.encog.ml.bayesian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.encog.ml.MLRegression;
import org.encog.ml.bayesian.parse.ParseProbability;
import org.encog.ml.bayesian.parse.ParsedEvent;
import org.encog.ml.bayesian.parse.ParsedProbability;
import org.encog.ml.bayesian.query.BayesianQuery;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;
import org.encog.ml.bayesian.query.sample.EventState;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.csv.CSVFormat;

public class BayesianNetwork implements MLRegression, Serializable {

	public static final String[] CHOICES_TRUE_FALSE = { "false", "true" };

	private final Map<String, BayesianEvent> eventMap = new HashMap<String, BayesianEvent>();
	private final List<BayesianEvent> events = new ArrayList<BayesianEvent>();
	private BayesianQuery query;

	public BayesianNetwork() {
		this.query = new EnumerationQuery(this);
	}

	/**
	 * @return the events
	 */
	public Map<String, BayesianEvent> getEventMap() {
		return eventMap;
	}

	public List<BayesianEvent> getEvents() {
		return this.events;
	}

	public BayesianEvent getEvent(String label) {
		return this.eventMap.get(label);
	}

	public BayesianEvent getEventError(String label) {
		if (!eventExists(label))
			throw (new BayesianError("Undefined label: " + label));
		return this.eventMap.get(label);
	}

	public boolean eventExists(String label) {
		return this.eventMap.containsKey(label);
	}
	
	public BayesianEvent createEvent(String label, String ... options) {
		if( label==null) {
			throw new BayesianError("Can't create event with null label name");
		}
		
		if (eventExists(label)) {
			throw new BayesianError("The label \"" + label
					+ "\" has already been defined.");
		}
		BayesianEvent event = new BayesianEvent(label,options);
		this.eventMap.put(label, event);
		this.events.add(event);
		return event;
	}
	
	public BayesianEvent createEvent(String label, List<String> options) {
		String[] s = new String[options.size()];
		options.toArray(s);
		return createEvent(label,s);		
	}	

	public void createDependancy(BayesianEvent parentEvent,
			BayesianEvent childEvent) {
		// does the dependency exist?
		if(!hasDependency(parentEvent,childEvent) ) {		
			// create the dependency
			parentEvent.addChild(childEvent);
			childEvent.addParent(parentEvent);
		}
	}

	private boolean hasDependency(BayesianEvent parentEvent,
			BayesianEvent childEvent) {
		return( parentEvent.getChildren().contains(childEvent));
	}

	public void createDependancy(BayesianEvent parentEvent,
			BayesianEvent... children) {
		for (BayesianEvent childEvent : children) {
			parentEvent.addChild(childEvent);
			childEvent.addParent(parentEvent);
		}
	}

	public void createDependancy(String parentEventLabel, String childEventLabel) {
		BayesianEvent parentEvent = getEventError(parentEventLabel);
		BayesianEvent childEvent = getEventError(childEventLabel);
		createDependancy(parentEvent, childEvent);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		// display only those with no parents
		for (BayesianEvent e : this.eventMap.values()) {
			if (!e.hasParents()) {
				if (!first)
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}

		// display only those with parents and children
		for (BayesianEvent e : this.eventMap.values()) {
			if (e.hasParents() && e.hasChildren()) {
				if (!first)
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}

		// display only those with parents and no children
		for (BayesianEvent e : this.eventMap.values()) {
			if (e.hasParents() && !e.hasChildren()) {
				if (!first)
					result.append(" ");
				first = false;
				result.append(e.toString());
			}
		}

		return result.toString();
	}

	public int calculateParameterCount() {
		int result = 0;
		for (BayesianEvent e : this.eventMap.values()) {
			result += e.calculateParameterCount();
		}
		return result;
	}

	public void finalizeStructure() {
		for (BayesianEvent e : this.eventMap.values()) {
			e.finalizeStructure();
		}
		
		if( this.query!=null ) {
			this.query.finalizeStructure();
		}
	}

	public void validate() {
		for (BayesianEvent e : this.eventMap.values()) {
			e.validate();
		}
	}

	private boolean isGiven(BayesianEvent[] given, BayesianEvent e) {
		for (BayesianEvent e2 : given) {
			if (e == e2)
				return true;
		}

		return false;
	}

	public boolean isDescendant(BayesianEvent a, BayesianEvent b) {
		if (a == b)
			return true;

		for (BayesianEvent e : b.getChildren()) {
			if (isDescendant(a, e))
				return true;
		}
		return false;
	}

	private boolean isGivenOrDescendant(BayesianEvent[] given, BayesianEvent e) {
		for (BayesianEvent e2 : given) {
			if (isDescendant(e2, e))
				return true;
		}

		return false;
	}

	private boolean isCondIndependant(boolean previousHead, BayesianEvent a,
			BayesianEvent goal, Set<BayesianEvent> searched,
			BayesianEvent... given) {

		// did we find it?
		if (a == goal) {
			return false;
		}

		// search children
		for (BayesianEvent e : a.getChildren()) {
			if (!searched.contains(e) || !isGiven(given, a)) {
				searched.add(e);
				if (!isCondIndependant(true, e, goal, searched, given))
					return false;
			}
		}

		// search parents
		for (BayesianEvent e : a.getParents()) {
			if (!searched.contains(e)) {
				searched.add(e);
				if (!previousHead || isGivenOrDescendant(given, a))
					if (!isCondIndependant(false, e, goal, searched, given))
						return false;
			}
		}

		return true;
	}

	public boolean isCondIndependant(BayesianEvent a, BayesianEvent b,
			BayesianEvent... given) {
		Set<BayesianEvent> searched = new HashSet<BayesianEvent>();
		return isCondIndependant(false, a, b, searched, given);
	}

	public BayesianQuery getQuery() {
		return query;
	}

	public void setQuery(BayesianQuery query) {
		this.query = query;
	}

	@Override
	public int getInputCount() {
		return this.query.getEvidenceEvents().size();
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public MLData compute(MLData input) {

		// copy the input to evidence
		int inputIndex = 0;
		for (int i = 0; i < this.events.size(); i++) {
			BayesianEvent event = this.events.get(i);
			EventState state = this.query.getEventState(event);
			if (state.getEventType() == EventType.Evidence) {
				state.setValue(input.getData(inputIndex++));
			}
		}

		// execute the query
		this.query.execute();

		MLData result = new BasicMLData(1);
		result.setData(0, this.query.getProbability());
		return result;
	}

	public void defineProbability(String line, double probability) {
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		parsedProbability.defineTruthTable(this, probability);
	}

	public void defineProbability(String line) {
		int index = line.lastIndexOf('=');
		boolean error = false;
		double prob = 0.0;
		String left = "";
		String right = "";
		
		if (index != -1) {
			left = line.substring(0, index);
			right = line.substring(index + 1);

			try {
				prob = CSVFormat.EG_FORMAT.parse(right);
			} catch (NumberFormatException ex) {
				error = true;
			}
		}
		
		if( error || index==-1) {
			throw new BayesianError("Probability must be of the form \"P(event|condition1,condition2,etc.)=0.5\".  Conditions are optional.");
		}
		defineProbability(left,prob);
	}

	public BayesianEvent requireEvent(String label) {
		BayesianEvent result = getEvent(label);
		if( result==null ) {
			throw new BayesianError("The event " + label + " is not defined.");
		}
		return result;
	}

	public void defineRelationship(String line) {
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		parsedProbability.defineRelationships(this);
	}

	public void defineQuery(String line) {
		
		if( this.query==null ) {
			throw new BayesianError("This Bayesian network does not have a query to define.");
		}
		
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		
		// first, mark all events as hidden
		this.query.reset();
		
		// deal with evidence (input)
		for( ParsedEvent parsedEvent : parsedProbability.getGivenEvents() ) {
			BayesianEvent event = getEvent(parsedEvent.getLabel());
			query.defineEventType(event, EventType.Evidence);
		}
		
		// deal with outcome (output)
		for( ParsedEvent parsedEvent : parsedProbability.getBaseEvents() ) {
			BayesianEvent event = getEvent(parsedEvent.getLabel());
			query.defineEventType(event, EventType.Outcome);
		}
		
		query.locateEventTypes();
	}
}
