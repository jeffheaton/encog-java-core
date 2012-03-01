/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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

/**
 * The Bayesian Network is a machine learning method that is based on
 * probability, and particularly Bayes' Rule. The Bayesian Network also forms
 * the basis for the Hidden Markov Model and Naive Bayesian Network. The
 * Bayesian Network is either constructed directly or inferred from training
 * data using an algorithm such as K2.
 * 
 * http://www.heatonresearch.com/wiki/Bayesian_Network
 */
public class BayesianNetwork extends BasicML implements MLClassification, MLResettable, Serializable, MLError {

	/**
	 * Default choices for a boolean event.
	 */
	public static final String[] CHOICES_TRUE_FALSE = { "true", "false" };

	/**
	 * Mapping between the event string names, and the actual events.
	 */
	private final Map<String, BayesianEvent> eventMap = new HashMap<String, BayesianEvent>();
	
	/**
	 * A listing of all of the events.
	 */
	private final List<BayesianEvent> events = new ArrayList<BayesianEvent>();
	
	/**
	 * The current Bayesian query.
	 */
	private BayesianQuery query;
	
	/**
	 * Specifies if each input is present.
	 */
	private boolean[] inputPresent;
	
	/**
	 * Specifies the classification target.
	 */
	private int classificationTarget;
	
	/**
	 * The probabilities of each classification.
	 */
	private double[] classificationProbabilities;

	public BayesianNetwork() {
		this.query = new EnumerationQuery(this);
	}

	/**
	 * @return The mapping from string names to events.
	 */
	public Map<String, BayesianEvent> getEventMap() {
		return eventMap;
	}

	/**
	 * @return The events.
	 */
	public List<BayesianEvent> getEvents() {
		return this.events;
	}

	/**
	 * Get an event based on the string label.
	 * @param label The label to locate.
	 * @return The event found.
	 */
	public BayesianEvent getEvent(String label) {
		return this.eventMap.get(label);
	}

	/**
	 * Get an event based on label, throw an error if not found.
	 * @param label THe event label to find.
	 * @return The event.
	 */
	public BayesianEvent getEventError(String label) {
		if (!eventExists(label))
			throw (new BayesianError("Undefined label: " + label));
		return this.eventMap.get(label);
	}

	/**
	 * Return true if the specified event exists.
	 * @param label The label we are searching for.
	 * @return True, if the event exists by label.
	 */
	public boolean eventExists(String label) {
		return this.eventMap.containsKey(label);
	}
	
	/**
	 * Create, or register, the specified event with this bayesian network.
	 * @param event The event to add.
	 */
	public void createEvent(BayesianEvent event) {
		if( eventExists(event.getLabel())) {
			throw new BayesianError("The label \"" + event.getLabel()
					+ "\" has already been defined.");
		}
		
		this.eventMap.put(event.getLabel(), event);
		this.events.add(event);
	}
	
	/**
	 * Create an event specified on the label and options provided.
	 * @param label The label to create this event as.
	 * @param options The options, or states, that this event can have.
	 * @return The newly created event.
	 */
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
	
	/**
	 * Create the specified events based on a variable number of options, or choices.
	 * @param label The label of the event to create.
	 * @param options The states that the event can have.
	 * @return The newly created event.
	 */
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

	/**
	 * Create a dependency between two events.
	 * @param parentEvent The parent event.
	 * @param childEvent The child event.
	 */
	public void createDependency(BayesianEvent parentEvent,
			BayesianEvent childEvent) {
		// does the dependency exist?
		if(!hasDependency(parentEvent,childEvent) ) {		
			// create the dependency
			parentEvent.addChild(childEvent);
			childEvent.addParent(parentEvent);
		}
	}

	/**
	 * Determine if the two events have a dependency.
	 * @param parentEvent The parent event.
	 * @param childEvent The child event.
	 * @return True if a dependency exists.
	 */
	private boolean hasDependency(BayesianEvent parentEvent,
			BayesianEvent childEvent) {
		return( parentEvent.getChildren().contains(childEvent));
	}

	/**
	 * Create a dependency between a parent and multiple children.
	 * @param parentEvent The parent event.
	 * @param children The child events.
	 */
	public void createDependency(BayesianEvent parentEvent,
			BayesianEvent... children) {
		for (BayesianEvent childEvent : children) {
			parentEvent.addChild(childEvent);
			childEvent.addParent(parentEvent);
		}
	}

	/**
	 * Create a dependency between two labels.
	 * @param parentEventLabel The parent event.
	 * @param childEventLabel The child event.
	 */
	public void createDependency(String parentEventLabel, String childEventLabel) {
		BayesianEvent parentEvent = getEventError(parentEventLabel);
		BayesianEvent childEvent = getEventError(childEventLabel);
		createDependency(parentEvent, childEvent);
	}
	
	/**
	 * @return The contents as a string. Shows both events and dependences.
	 */
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
	
	/**
	 * Define the structure of the Bayesian network as a string.
	 * @param line The string to define events and relations.
	 */
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
					this.createDependency(givenEvent, event);					
				}
				givenList.add(given.getLabel());
			}
			
			// now remove givens that were not covered
			for(int i=0; i<event.getParents().size();i++) {
				BayesianEvent event2 = event.getParents().get(i);
				if( !givenList.contains(event2.getLabel()) ) {					
					removeDependency(event2,event);
				}
			}
		}		
		
		// finalize the structure
		finalizeStructure();
		if (this.query != null) {
			this.query.finalizeStructure();
		}
		
		

	}

	/**
	 * Remove a dependency, if it it exists.
	 * @param parent The parent event.
	 * @param child The child event.
	 */
	private void removeDependency(BayesianEvent parent, BayesianEvent child) {
		parent.getChildren().remove(child);
		child.getParents().remove(parent);
		
	}

	/**
	 * Remove the specified event.
	 * @param event The event to remove.
	 */
	private void removeEvent(BayesianEvent event) {
		for( BayesianEvent e : event.getParents() ) {
			e.getChildren().remove(event);
		}
		this.eventMap.remove(event.getLabel());
		this.events.remove(event);		
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * @return The number of parameters in this Bayesian network.
	 */
	public int calculateParameterCount() {
		int result = 0;
		for (BayesianEvent e : this.eventMap.values()) {
			result += e.calculateParameterCount();
		}
		return result;
	}

	/**
	 * Finalize the structure of this Bayesian network.
	 */
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

	/**
	 * Validate the structure of this Bayesian network.
	 */
	public void validate() {
		for (BayesianEvent e : this.eventMap.values()) {
			e.validate();
		}
	}

	/**
	 * Determine if one Bayesian event is in an array of others.
	 * @param given The events to check.
	 * @param e See if e is amoung given.
	 * @return True if e is amoung given.
	 */
	private boolean isGiven(BayesianEvent[] given, BayesianEvent e) {
		for (BayesianEvent e2 : given) {
			if (e == e2)
				return true;
		}

		return false;
	}

	/**
	 * Determine if one event is a descendant of another.
	 * @param a The event to check.
	 * @param b The event that has children.
	 * @return True if a is amoung b's children.
	 */
	public boolean isDescendant(BayesianEvent a, BayesianEvent b) {
		if (a == b)
			return true;

		for (BayesianEvent e : b.getChildren()) {
			if (isDescendant(a, e))
				return true;
		}
		return false;
	}

	/**
	 * True if this event is given or conditionally dependant on the others.
	 * @param given The others to check.
	 * @param e The event to check.
	 * @return
	 */
	private boolean isGivenOrDescendant(BayesianEvent[] given, BayesianEvent e) {
		for (BayesianEvent e2 : given) {
			if (isDescendant(e2, e))
				return true;
		}

		return false;
	}

	/**
	 * Help determine if one event is conditionally independent of another.
	 * @param previousHead The previous head, as we traverse the list.
	 * @param a The event to check.
	 * @param goal The goal.
	 * @param searched List of events searched.
	 * @param given Given events.
	 * @return True if conditionally independent.
	 */
	private boolean isCondIndependent(boolean previousHead, BayesianEvent a,
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
				if (!isCondIndependent(true, e, goal, searched, given))
					return false;
			}
		}

		// search parents
		for (BayesianEvent e : a.getParents()) {
			if (!searched.contains(e)) {
				searched.add(e);
				if (!previousHead || isGivenOrDescendant(given, a))
					if (!isCondIndependent(false, e, goal, searched, given))
						return false;
			}
		}

		return true;
	}

	public boolean isCondIndependent(BayesianEvent a, BayesianEvent b,
			BayesianEvent... given) {
		Set<BayesianEvent> searched = new HashSet<BayesianEvent>();
		return isCondIndependent(false, a, b, searched, given);
	}

	public BayesianQuery getQuery() {
		return query;
	}

	public void setQuery(BayesianQuery query) {
		this.query = query;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputCount() {
		return this.events.size();
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Define the probability for an event.
	 * @param line The event.
	 * @param probability The probability.
	 */
	public void defineProbability(String line, double probability) {
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		parsedProbability.defineTruthTable(this, probability);
	}

	/**
	 * Define a probability.
	 * @param line The line to define the probability.
	 */
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

	/**
	 * Require the specified event, thrown an error if it does not exist.
	 * @param label
	 * @return
	 */
	public BayesianEvent requireEvent(String label) {
		BayesianEvent result = getEvent(label);
		if( result==null ) {
			throw new BayesianError("The event " + label + " is not defined.");
		}
		return result;
	}

	/**
	 * Define a relationship.
	 * @param line The relationship to define.
	 */
	public void defineRelationship(String line) {
		ParseProbability parse = new ParseProbability(this);
		ParsedProbability parsedProbability = parse.parse(line);
		parsedProbability.defineRelationships(this);
	}
	
	/**
	 * Perform a query.
	 * @param line The query.
	 * @return The probability.
	 */
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Remove all relations between nodes.
	 */
	public void removeAllRelations() {
		for(BayesianEvent event: this.events) {
			event.removeAllRelations();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		reset(0);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset(int seed) {
		for(BayesianEvent event: this.events) {			
			event.reset();
		}
		
	}

	/**
	 * Determine the classes for the specified input.
	 * @param input The input.
	 * @return An array of class indexes.
	 */
	public int[] determineClasses(MLData input) {
		int[] result = new int[input.size()];
		
		for(int i=0;i<input.size();i++) {
			BayesianEvent event = this.events.get(i);
			int classIndex = event.matchChoiceToRange(input.getData(i));
			result[i] = classIndex;
		}
		
		return result;
	}

	/**
	 * Classify the input.
	 * @param input The input to classify.
	 */
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

	/**
	 * Get the classification target.
	 * @return The index of the classification target.
	 */
	public int getClassificationTarget() {
		return classificationTarget;
	}
	
	/**
	 * Determine if the specified input is present.
	 * @param idx The index of the input.
	 * @return True, if the input is present.
	 */
	public boolean isInputPresent(int idx) {
		return this.inputPresent[idx];
	}

	/**
	 * Define a classification structure of the form P(A|B) = P(C)
	 * @param line
	 */
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
		
		// set the values
		for(ParsedEvent parsedGiven: prob.getGivenEvents()) {
			BayesianEvent event = this.getEvent( parsedGiven.getLabel() );
			this.query.setEventValue(event, parseInt(parsedGiven.getValue()) );
		}
		
		this.query.setEventValue(be, parseInt(prob.getBaseEvents().get(0).getValue()) );		
	}
	
	private int parseInt(String str) {
		if( str==null ) {
			return 0;
		}
		
		try {
			return Integer.parseInt(str);
		} catch(NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * @return The classification target.
	 */
	public BayesianEvent getClassificationTargetEvent() {
		if( this.classificationTarget==-1) {
			throw new BayesianError("No classification target defined.");			
		}
		
		return this.events.get(this.classificationTarget);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateError(final MLDataSet data) {
		
		if( !this.hasValidClassificationTarget())
			return 1.0;
		
		// do the following just to throw an error if there is no classification target		
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

	/**
	 * @return Returns a string representation of the classification structure.
	 *         Of the form P(a|b,c,d)
	 */
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
	
	/**
	 * @return True if this network has a valid classification target.
	 */
	public boolean hasValidClassificationTarget() {
		if (this.classificationTarget < 0
				|| this.classificationTarget >= this.events.size()) {
			return false;
		} else {
			return true;
		}
	} 
}
