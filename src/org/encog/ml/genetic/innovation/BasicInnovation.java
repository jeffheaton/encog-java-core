/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.ml.genetic.innovation;

import java.io.Serializable;

import org.encog.persist.BasicPersistedSubObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGAttribute;

/**
 * Provides basic functionality for an innovation.
 */
public class BasicInnovation extends BasicPersistedSubObject implements Innovation {

	public final static String PROPERTY_INNOVATION_ID = "id";
	
	/**
	 * The innovation id.
	 */
	@EGAttribute
	private long innovationID;

	/**
	 * @return The innovation ID.
	 */
	public long getInnovationID() {
		return innovationID;
	}

	/**
	 * Set the innovation id.
	 * @param innovationID The innovation id.
	 */
	public void setInnovationID(final long innovationID) {
		this.innovationID = innovationID;
	}

	@Override
	public Persistor createPersistor() {
		// TODO Auto-generated method stub
		return null;
	}

}
