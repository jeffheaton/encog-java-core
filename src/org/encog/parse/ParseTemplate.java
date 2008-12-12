package org.encog.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.ParseTemplatePersistor;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParseTemplate implements EncogPersistedObject {

	private Collection<Recognize> recognizers = new ArrayList<Recognize>();
	private String name;
	private String description;

	public void addRecognizer(Recognize recognize) {
		recognizers.add(recognize);
	}

	public Recognize createRecognizer(String name) {
		Recognize result = new Recognize(name);
		addRecognizer(result);
		return result;
	}

	/**
	 * @return the recognizers
	 */
	public Collection<Recognize> getRecognizers() {
		return recognizers;
	}

	public Persistor createPersistor() {
		return new ParseTemplatePersistor();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
