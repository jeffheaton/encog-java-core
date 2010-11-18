/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
