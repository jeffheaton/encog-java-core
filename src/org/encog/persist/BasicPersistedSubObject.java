package org.encog.persist;

/**
 * Provides a basic implementation for an Encog persisted object that is not top
 * level. Because it is not stored in the top level collection the name,
 * description and collection attributes do not have meaning, and are not
 * implemented.
 */
public abstract class BasicPersistedSubObject implements EncogPersistedObject {

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @return Always returns null.
	 */
	@Override
	public EncogCollection getCollection() {
		return null;
	}

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @return Always returns null.
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @return Always returns null.
	 */
	@Override
	public String getName() {
		return null;
	}

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @param collection
	 *            Not used.
	 */
	@Override
	public void setCollection(final EncogCollection collection) {

	}

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @param theDescription
	 *            Not used.
	 */
	@Override
	public void setDescription(final String theDescription) {

	}

	/**
	 * Not implemented. This object is not a "top level persisted object".
	 * 
	 * @param theName
	 *            Not used.
	 */
	@Override
	public void setName(final String theName) {

	}
}
