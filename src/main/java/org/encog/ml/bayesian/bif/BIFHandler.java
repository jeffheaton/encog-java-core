package org.encog.ml.bayesian.bif;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BIFHandler extends DefaultHandler {

	private final List<FileSection> currentSection = new ArrayList<FileSection>();
	private final List<BIFVariable> bifVariables = new ArrayList<BIFVariable>();
	private final List<BIFDefinition> bifDefinitions = new ArrayList<BIFDefinition>();
	private BIFVariable currentVariable;
	private BIFDefinition currentDefinition;
	private String currentString;

	private BayesianNetwork network;

	public BIFHandler() {
		this.network = new BayesianNetwork();
	}

	private void handleBeginBIF(String qName, Attributes attributes) {
		if (qName.equals("NETWORK")) {
			this.currentSection.add(FileSection.NETWORK);
		}
	}

	private void handleBeginNETWORK(String qName, Attributes attributes) {
		if (qName.equals("VARIABLE")) {
			this.currentSection.add(FileSection.VARIABLE);
			this.currentVariable = new BIFVariable();
			this.bifVariables.add(this.currentVariable);
		} else if (qName.equals("DEFINITION")) {
			this.currentSection.add(FileSection.DEFINITION);
			this.currentDefinition = new BIFDefinition();
			this.bifDefinitions.add(this.currentDefinition);
		}
	}

	private void handleBeginVARIABLE(String qName, Attributes attributes) {
		if (qName.equals("VARIABLE")) {
			this.currentVariable = new BIFVariable();
			this.bifVariables.add(this.currentVariable);
		}
	}

	private void handleBeginDEFINITION(String qName, Attributes attributes) {
		if (qName.equals("DEFINITION")) {
			this.currentDefinition = new BIFDefinition();
			this.bifDefinitions.add(this.currentDefinition);
		}
	}

	private void handleEndBIF(String qName) {
		if (qName.equals("BIF")) {
			this.currentSection.remove(this.currentSection.size() - 1);
		}
	}

	private void handleEndNETWORK(String qName) {
		if (qName.equals("NETWORK")) {
			this.currentSection.remove(this.currentSection.size() - 1);
		}
	}

	private void handleEndVARIABLE(String qName) {
		if (qName.equals("NAME")) {
			this.currentVariable.setName(this.currentString);
		} else if (qName.equals("OUTCOME")) {
			this.currentVariable.addOption(this.currentString);
		} else if( qName.equals("VARIABLE") ) {
			this.currentSection.remove(this.currentSection.size() - 1);
		}
	}

	private void handleEndDEFINITION(String qName) {
		if (qName.equals("FOR")) {
			this.currentDefinition.setForDefinition(this.currentString);
		} else if (qName.equals("GIVEN")) {
			this.currentDefinition.addGiven(this.currentString);
		} else if (qName.equals("TABLE")) {
			this.currentDefinition.setTable(this.currentString);
		} else if( qName.equals("DEFINITION") ) {
			this.currentSection.remove(this.currentSection.size() - 1);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (this.currentSection.size() == 0 && qName.equals("BIF")) {
			this.currentSection.add(FileSection.BIF);
		}

		else if (this.currentSection.size() > 0) {
			switch (this.currentSection.get(this.currentSection.size() - 1)) {
			case BIF:
				handleBeginBIF(qName, attributes);
				break;
			case DEFINITION:
				handleBeginDEFINITION(qName, attributes);
				break;
			case NETWORK:
				handleBeginNETWORK(qName, attributes);
				break;
			case VARIABLE:
				handleBeginVARIABLE(qName, attributes);
				break;
			}
		}
	}

	public BayesianNetwork getNetwork() {
		return network;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (this.currentSection.size() > 0) {
			switch (this.currentSection.get(this.currentSection.size() - 1)) {
			case BIF:
				handleEndBIF(qName);
				break;
			case DEFINITION:
				handleEndDEFINITION(qName);
				break;
			case NETWORK:
				handleEndNETWORK(qName);
				break;
			case VARIABLE:
				handleEndVARIABLE(qName);
				break;
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();

		// define variables
		for (BIFVariable v : this.bifVariables) {
			this.network.createEvent(v.getName(), v.getOptions());
		}

		// define relations 
		for (BIFDefinition d : this.bifDefinitions) {
			BayesianEvent childEvent = this.network.requireEvent(d
					.getForDefinition());
			for (String s : d.getGivenDefinitions()) {
				BayesianEvent parentEvent = this.network.requireEvent(s);
				this.network.createDependancy(parentEvent, childEvent);
			}
		}

		this.network.finalizeStructure();
		
		// define probabilities
		
		for (BIFDefinition d : this.bifDefinitions) {
			double[] t = d.getTable();
			BayesianEvent childEvent = this.network.requireEvent(d.getForDefinition());
			
			int tableIndex = 0;
			int[] args = new int[childEvent.getParents().size()];
			do {
				for(int result = 0; result<childEvent.getChoices().length;result++) {
					childEvent.getTable().addLine(t[tableIndex++], result, args);	
				}
			} while(BIFUtil.rollArgs(childEvent,args));
		}
	}
	

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		this.currentString = new String(ch, start, length);
	}
}
