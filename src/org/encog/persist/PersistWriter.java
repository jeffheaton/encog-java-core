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
package org.encog.persist;

import java.io.OutputStream;
import java.util.Date;

import org.encog.Encog;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.location.PersistenceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for writing Encog persisted class files.
 * 
 * @author jheaton
 * 
 */
public class PersistWriter {

	/**
	 * The XML writer.
	 */
	private final WriteXML out;

	/**
	 * The output stream.
	 */
	private final OutputStream fileOutput;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create a writer for the specified location.
	 * 
	 * @param location
	 *            The location.
	 */
	public PersistWriter(final PersistenceLocation location) {
		this.fileOutput = location.createOutputStream();
		this.out = new WriteXML(this.fileOutput);
	}

	/**
	 * Begin an Encog document.
	 */
	public void begin() {
		this.out.beginDocument();
		this.out.beginTag("Document");
	}

	/**
	 * Begin the objects collection.
	 */
	public void beginObjects() {
		this.out.beginTag("Objects");
	}

	/**
	 * Close the writer.
	 */
	public void close() {
		this.out.close();
	}

	/**
	 * End the document.
	 */
	public void end() {
		this.out.endTag();
		this.out.endDocument();
	}

	/**
	 * End the objects collection.
	 */
	public void endObjects() {
		this.out.endTag();
	}

	/**
	 * Merge the objects from this collection into the new one. Skip the
	 * specified object.
	 * 
	 * @param location
	 *            The location to merge to.
	 * @param skip
	 *            The object to skip.
	 */
	public void mergeObjects(final PersistenceLocation location,
			final String skip) {
		final PersistReader reader = new PersistReader(location);
		reader.saveTo(this.out, skip);
		reader.close();
	}

	/**
	 * Modify the specified object, such as changing its name or description.
	 * 
	 * @param location
	 *            The location of the object being modified.
	 * @param name
	 *            The old name of the object being modified.
	 * @param newName
	 *            The new name of the object being modified.
	 * @param newDesc
	 *            The new description of the object being modified.
	 */
	public void modifyObject(final PersistenceLocation location,
			final String name, final String newName, final String newDesc) {

		final PersistReader reader = new PersistReader(location);
		reader.saveModified(this.out, name, newName, newDesc);
		reader.close();

	}

	/**
	 * Write the header for the Encog file.
	 */
	public void writeHeader() {
		this.out.beginTag("Header");
		this.out.addProperty("platform", "Java");
		this.out.addProperty("fileVersion", Encog.getInstance().getProperties()
				.get(Encog.ENCOG_FILE_VERSION));
		this.out.addProperty("encogVersion", Encog.getInstance()
				.getProperties().get(Encog.ENCOG_VERSION));
		this.out.addProperty("modified", (new Date()).toString());
		this.out.endTag();
	}

	/**
	 * Write an object.
	 * 
	 * @param obj
	 *            The object to write.
	 */
	public void writeObject(final EncogPersistedObject obj) {
		final Persistor persistor = obj.createPersistor();
		if (persistor == null) {
			final String str = "Can't find a persistor for object of type "
					+ obj.getClass().getName();
			if (this.logger.isErrorEnabled()) {
				this.logger.error(str);
			}
			throw new PersistError(str);
		}
		persistor.save(obj, this.out);
	}

}
