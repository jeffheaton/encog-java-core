package org.encog.persist.persistors.generic;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Simple object to persist with the generic class.
 * 
 * @author jheaton
 */
public class SimpleObjectToPersist implements EncogPersistedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3193782478666198020L;

	/**
	 * The first test string.
	 */
	private String first;

	/**
	 * The second test string.
	 */
	private String second;

	/**
	 * A test number.
	 */
	private double number;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used.
	 */
	@Override
	public Object clone() {
		return null;
	}

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used for this simple test.
	 */
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The first test string.
	 */
	public String getFirst() {
		return this.first;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The second test string.
	 */
	public double getNumber() {
		return this.number;
	}

	/**
	 * @return The second test string.
	 */
	public String getSecond() {
		return this.second;
	}

	/**
	 * Set the description of this object.
	 * 
	 * @param theDescription
	 *            The description of this object.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Set the first test string.
	 * 
	 * @param first
	 *            The new value.
	 */
	public void setFirst(final String first) {
		this.first = first;
	}

	/**
	 * Set the name of this object.
	 * 
	 * @param theName
	 *            The name of this object.
	 */
	public void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * The number to test with.
	 * 
	 * @param number
	 *            The new value.
	 */
	public void setNumber(final double number) {
		this.number = number;
	}

	/**
	 * The second test string.
	 * 
	 * @param second
	 *            The new value.
	 */
	public void setSecond(final String second) {
		this.second = second;
	}

}
