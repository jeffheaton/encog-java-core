package org.encog.ml.bayesian.query.enumerate;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.EventType;
import org.encog.ml.bayesian.query.BasicQuery;
import org.encog.ml.bayesian.query.sample.EventState;

public class EnumerationQuery extends BasicQuery {

	private List<EventState> enumerationEvents = new ArrayList<EventState>();
	
	public EnumerationQuery(BayesianNetwork theNetwork) {
		super(theNetwork);
	}
	
	public void locateEventTypes() {
		super.locateEventTypes();
		this.enumerationEvents.clear();
		
		for(EventState state: this.getEvents().values() ) {
			if( state.getEventType()==EventType.Hidden ) {
				this.enumerationEvents.add(state);
			}
		}
	}
	
	public void forward() {
		int currentIndex = 0;
		boolean done = false;
		
		while(!done) {
			
		}
		for(int i=0;i<this.enumerationEvents.size();i++) {
			EventState state = this.enumerationEvents.get(i);
			int v = (int)state.getValue();
			v++;
			if( v>=state.getEvent().getChoices().length)
			{
				
			}
			else 
			{
				
			}
		}
	}

	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
