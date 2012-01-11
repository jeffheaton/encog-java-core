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
package org.encog.persist;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface defines an Encog Persistor. An Encog persistor will write an
 * Encog object to an EG file.
 */
public interface EncogPersistor {

	/**
	 * @return Get the class string for the object.
	 */
	String getPersistClassString();

	/**
	 * Read the object from an input stream.
	 * @param is The input stream.
	 * @return The object.
	 */
	Object read(InputStream is);

	/**
	 * Save the object.
	 * @param os The output stream to save to.
	 * @param obj The object to save.
	 */
	void save(OutputStream os, Object obj);

	/**
	 * @return Get the file version used by this persistor.
	 */
	int getFileVersion();
}
