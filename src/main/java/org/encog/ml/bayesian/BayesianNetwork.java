package org.encog.ml.bayesian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.encog.ml.BasicML;
import org.encog.ml.MLClassification;
import org.encog.ml.MLError;
import org.encog.ml.MLResettable;
import org.encog.ml.bayesian.parse.ParseProbability;
import org.encog.ml.bayesian.parse.ParsedChoice;
import org.encog.ml.bayesian.parse.ParsedEvent;
import org.encog.ml.bayesian.parse.ParsedProbability;
import org.encog.ml.bayesian.query.BayesianQuery;
import org.encog.ml.bayesian.query.enumerate.EnumerationQuery;
import org.encog.ml.bayesian.query.sample.EventState;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;

public class BayesianNetwork extends BasicML implements MLClassification, MLResettable, Serializable, MLError {

	public static final String[] CHOICES_TRUE_FALSE = { "true", "false" };

	private final Map<String, BayesianEvent> eventMap = new HashMap<String, BayesianEvent>();
	private final List<BayesianEvent> events = new ArrayList<BayesianEvent>();
	private BayesianQuery query;
	private boolean[] inputPresent;
	private int classificationTarget;
	private double[] classificationProbabilities;

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
	
	public void createEvent(BayesianEvent event) {
		if( eventExists(event.getLabel())) {
			throw new BayesianError("The label \"" + event.getLabel()
					+ "\" has already been defined.");
		}
		
		this.eventMap.put(event.getLabel(), event);
		this.events.add(event);
	}
	
	public BayesianEvent createEvent(String label, List<BayesianChoice> options) {
		if( label==null) {
			throw new BayesianError("Can't create event with null label name");
		}
		
		if (eventExists(label)) {
			throw new BayesianError("The label \"" + label
					+ "\" has already been defined.");
		}
		
		BayesianEvent event;
		
		if( options.size()==0 ) {
			event = new BayesianEvent(label);
		} else {
			event = new BayesianEvent(label,options);
			
		}
		createEvent(event);
		return event;
	}
	
	public BayesianEvent createEvent(String label, String ... options) {
		if( label==null) {
			throw new BayesianError("Can't create event with null label name");
		}
		
		if (eventExists(label)) {
			throw new BayesianError("The label \"" + label
					+ "\" has already been defined.");
		}
		
		BayesianEvent event;
		
		if( options.length==0 ) {
			event = new BayesianEvent(label);
		} else {
			event = new BayesianEvent(label,options);
			
		}
		createEvent(event);
		return event;
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
	
	public String getContents() {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (BayesianEvent e : this.events) {
			if (!first)
				result.append(" ");
			first = false;
			result.append(e.toFullString());
		}

		return result.toString();		
	}
	
	public void setContents(String line) {
		List<ParsedProbability> list = ParseProbability.parseProbabilityList(this, line);
		List<String> labelList = new ArrayList<String>();
		
		// ensure that all events are there
		for(ParsedProbability prob: list ) {
			ParsedEvent parsedEvent = prob.getChildEvent();
			String eventLabel = parsedEvent.getLabel();
			labelList.add(eventLabel);
			
			// create event, if not already here
			BayesianEvent e = getEvent(eventLabel); 
			if( e==null ) {
				List<BayesianChoice> cl = new ArrayList<BayesianChoice>();
								
				for( ParsedChoice c : parsedEvent.getList() ) {
					cl.add(new BayesianChoice(c.getLabel(),c.getMin(),c.getMax()));
				}
				
				createEvent(eventLabel, cl);				
			}
		}

		
		// now remove all events that were not covered
		for(int i=0; i<events.size();i++) {
			BayesianEvent event = this.events.get(i);
			if( !labelList.contains(event.getLabel()) ) {
				removeEvent(event);
			}
		}

		// handle dependencies
		for(ParsedProbability prob: list ) {
			ParsedEvent parsedEvent = prob.getChildEvent();
			String eventLabel = parsedEvent.getLabel();
			
			BayesianEvent event = requireEvent(eventLabel);
			
			// ensure that all "givens" are present
			List<String> givenList = new ArrayList<String>();
			for( ParsedEvent given: prob.getGivenEvents() ) {
				if( !event.hasGiven(given.getLabel()) ) {
					BayesianEvent givenEvent = requireEvent(given.getLabel());
					this.createDependancy(givenEvent, event);					
				}
				givenList.add(given.getLabel());
			}
			
			// now remove givens that were not covered
			for(int i=0; i<event.getParents().size();i++) {
				BayesianEvent event2 = event.getParents().get(i);
				if( !givenList.contains(event2.getLabel()) ) {					
					removeDependancy(event2,event);
				}
			}
		}		
		
		// finalize the structure
		finalizeStructure();
		if (this.query != null) {
			this.query.finalizeStructure();
		}
		
		

	}

	private void removeDependancy(BayesianEvent parent, BayesianEvent child) {
		parent.getChildren().remove(child);
		child.getParents().remove(parent);
		
	}

	private void removeEvent(BayesianEvent event) {
		for( BayesianEvent e : event.getParents() ) {
			e.getChildren().remove(event);
		}
		this.eventMap.remove(event.getLabel());
		this.events.remove(event);		
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (BayesianEvent e : this.events) {
			if (!first)
				result.append(" ");
			first = false;
			result.append(e.toString());
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
		
		this.inputPresent = new boolean[this.events.size()];
		EngineArray.fill(this.inputPresent, true);
		this.classificationTarget = -1;
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
		return this.events.size();
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	public double computeProbability(MLData input) {

		// copy the input to evidence
		int inputIndex = 0;
		for (int i = 0; i < this.events.size(); i++) {
			BayesianEvent event = this.events.get(i);
			EventState state = this.query.getEventState(event);
			if (state.getEventType() == EventType.Evidence) {
				state.setValue((int)input.getData(inputIndex++));
			}
		}

		// execute the query
		this.query.execute();
		
		return this.query.getProbability();
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
	
	public double performQuery(String line) {
		if( this.query==null ) {
			throw new BayesianError("This Bayesian network does not have a query to define.");
		}
		
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		
		// create a temp query
		BayesianQuery q = this.query.clone();
		
		// first, mark all events as hidden
		q.reset();
		
		// deal with evidence (input)
		for( ParsedEvent parsedEvent : parsedProbability.getGivenEvents() ) {
			BayesianEvent event = this.requireEvent(parsedEvent.getLabel());
			q.defineEventType(event, EventType.Evidence);
			q.setEventValue(event, parsedEvent.resolveValue(event));
		}
		
		// deal with outcome (output)
		for( ParsedEvent parsedEvent : parsedProbability.getBaseEvents() ) {
			BayesianEvent event = requireEvent(parsedEvent.getLabel());
			q.defineEventType(event, EventType.Outcome);
			q.setEventValue(event, parsedEvent.resolveValue(event));
		}
		
		q.locateEventTypes();
		
		q.execute();
		return q.getProbability();
	}

	@Override
	public void updateProperties() {
		// Not needed		
	}

	public int getEventIndex(BayesianEvent event) {
		for(int i=0;i<this.events.size();i++) {
			if( event==events.get(i))
				return i;
		}
		
		return -1;
	}

	public void removeAllRelations() {
		for(BayesianEvent event: this.events) {
			event.removeAllRelations();
		}
	}

	@Override
	public void reset() {
		reset(0);
		
	}

	@Override
	public void reset(int seed) {
		for(BayesianEvent event: this.events) {			
			event.reset();
		}
		
	}

	public int[] determineClasses(MLData input) {
		int[] result = new int[input.size()];
		
		for(int i=0;i<input.size();i++) {
			BayesianEvent event = this.events.get(i);
			int classIndex = event.matchChoiceToRange(input.getData(i));
			result[i] = classIndex;
		}
		
		return result;
	}

	@Override
	public int classify(MLData input) {
		
		if( this.classificationTarget<0 || this.classificationTarget>=this.events.size() ) {
			throw new BayesianError("Must specify classification target by calling setClassificationTarget.");
		}
		
		int[] d = this.determineClasses(input);
		
		// properly tag all of the events
		for(int i=0;i<this.events.size();i++) {
			BayesianEvent event = this.events.get(i);
			if( i==this.classificationTarget ) {
				this.query.defineEventType(event, EventType.Outcome);
			} else if( this.inputPresent[i] ) {
				this.query.defineEventType(event, EventType.Evidence);
				this.query.setEventValue(event, d[i]);
			} else {
				this.query.defineEventType(event, EventType.Hidden);
				this.query.setEventValue(event, d[i]);
			}
		}
		
		
		// loop over and try each outcome choice
		BayesianEvent outcomeEvent = this.events.get(this.classificationTarget);
		this.classificationProbabilities = new double[outcomeEvent.getChoices().size()];
		for(int i=0;i<outcomeEvent.getChoices().size();i++) {
			this.query.setEventValue(outcomeEvent, i);
			this.query.execute();
			classificationProbabilities[i] = this.query.getProbability();
		}
		
		
		return EngineArray.maxIndex(this.classificationProbabilities);
	}

	public int getClassificationTarget() {
		return classificationTarget;
	}
	
	public boolean isInputPresent(int idx) {
		return this.inputPresent[idx];
	}

	public void defineClassificationStructure(String line) {
		List<ParsedProbability> list = ParseProbability.parseProbabilityList(this, line);	
		
		if( list.size()>1) {
			throw new BayesianError("Must only define a single probability, not a chain.");
		}
		
		if( list.size()==0) {
			throw new BayesianError("Must define at least one probability.");
		}

		// first define everything to be hidden
		for(BayesianEvent event: this.events) {
			this.query.defineEventType(event, EventType.Hidden);
		}
		
		// define the base event
		ParsedProbability prob = list.get(0);
		if( prob.getBaseEvents().size()==0 ) {
			return;
		}
		
		BayesianEvent be = this.getEvent( prob.getChildEvent().getLabel() );
		this.classificationTarget = this.events.indexOf(be);
		this.query.defineEventType(be, EventType.Outcome);
		
		// define the given events
		for(ParsedEvent parsedGiven: prob.getGivenEvents()) {
			BayesianEvent given = this.getEvent( parsedGiven.getLabel() );
			this.query.defineEventType(given, EventType.Evidence);
		}
		
		this.query.locateEventTypes();
		
	}

	public BayesianEvent getClassificationTargetEvent() {
		if( this.classificationTarget==-1) {
			throw new BayesianError("No classification target defined.");			
		}
		
		return this.events.get(this.classificationTarget);
	}
	
	@Override
	public double calculateError(final MLDataSet data) {
		getClassificationTarget();
		int badCount = 0;
		int totalCount = 0;
		
		for(MLDataPair pair: data) {
			int c = this.classify(pair.getInput());
			totalCount++;
			if( c!=pair.getInput().getData(this.classificationTarget)) {
				badCount++;
			}
		}
		
		return (double)badCount/(double)totalCount;
	}

	public String getClassificationStructure() {
		StringBuilder result = new StringBuilder();
		
		result.append("P(");
		boolean first = true;
		
		for(int i=0;i<this.getEvents().size();i++) {
			BayesianEvent event = this.events.get(i);	
			EventState state = this.query.getEventState(event);
			if( state.getEventType()==EventType.Outcome ) {
				if(!first) {
					result.append(",");
				}
				result.append(event.getLabel());
				first = false;
			}
		}
		
		result.append("|");
		
		first = true;
		for(int i=0;i<this.getEvents().size();i++) {
			BayesianEvent event = this.events.get(i);			
			if( this.query.getEventState(event).getEventType()==EventType.Evidence ) {
				if(!first) {
					result.append(",");
				}
				result.append(event.getLabel());
				first = false;
			}
		}
		
		result.append(")");
		return result.toString();
	}

	public boolean hasValidClassificationTarget() {
		if( this.classificationTarget<0 || this.classificationTarget>=this.events.size() ) {
			return false;
		} else {
			return true;
		}
	}
}
