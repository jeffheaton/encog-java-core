package org.encog.persist;

import org.encog.persist.persistors.generic.GenericPersistor;

/**
 * A basic Encog persisted object. Provides the name, description and collection
 * attributes. Also provides a generic persistor.
 */
public class BasicPersistedObject implements EncogPersistedObject {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the object.
	 */
	private String name;

	/**
	 * The description of the object.
	 */
	private String description;

	/**
	 * The collection the object belongs to.
	 */
	private EncogCollection collection;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persistor createPersistor() {
		return new GenericPersistor(this.getClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncogCollection getCollection() {
		return this.collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCollection(final EncogCollection collection) {
		this.collection = collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(final String theName) {
		this.name = theName;

	}
}
