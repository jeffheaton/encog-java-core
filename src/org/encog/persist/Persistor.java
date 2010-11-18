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

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;

/**
 * This interface defines a class that can load and save an
 * EncogPersistedObject.
 * 
 * @author jheaton
 * 
 */
public interface Persistor {

	/**
	 * Load from the specified node.
	 * 
	 * @param in
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	EncogPersistedObject load(ReadXML in);

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The object to save.
	 * @param out
	 *            The XML object.
	 */
	void save(EncogPersistedObject object, WriteXML out);
}
