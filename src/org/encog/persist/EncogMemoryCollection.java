/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.persist;

import java.util.HashMap;
import java.util.Map;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.persistors.PersistorUtil;

/**
 * A memory based collection of Encog objects. Does not use the more complex
 * temp file structure like EncogPersistedCollection, but also can't handle
 * gigantic files. This class can load and save from/to Encog EG files.
 */
public class EncogMemoryCollection {

	/**
	 * The contents of this collection.
	 */
	private final Map<String, EncogPersistedObject> contents = new HashMap<String, EncogPersistedObject>();

	/**
	 * Populated during a load, the file version.
	 */
	private int fileVersion;

	/**
	 * Populated during a load, the Encog version that created this file.
	 */
	private String encogVersion;

	/**
	 * Populated during a load, the platform that this was loaded to.
	 */
	private String platform;

	/**
	 * @return The objects that were loaded from this file.
	 */
	public Map<String, EncogPersistedObject> getContents() {
		return this.contents;
	}

	/**
	 * @return The version of Encog that this file was created with.
	 */
	public String getEncogVersion() {
		return this.encogVersion;
	}

	/**
	 * @return The Encog file version.
	 */
	public int getFileVersion() {
		return this.fileVersion;
	}

	/**
	 * @return The platform that this file was created on.
	 */
	public String getPlatform() {
		return this.platform;
	}

	/**
	 * Load the contents of a location.
	 * 
	 * @param location
	 *            The location to load from.
	 */
	public void load(final PersistenceLocation location) {
		PersistReader reader = null;

		try {
			reader = new PersistReader(location);
			final Map<String, String> header = reader.readHeader();
			if (header != null) {
				this.fileVersion = Integer.parseInt(header.get("fileVersion"));
				this.encogVersion = header.get("encogVersion");
				this.platform = header.get("platform");
			}
			reader.advanceObjectsCollection();
			final ReadXML in = reader.getXMLInput();

			while (in.readToTag()) {
				if (in.is(PersistReader.TAG_OBJECTS, false)) {
					break;
				}

				final String type = in.getTag().getName();
				final String name = in.getTag().getAttributeValue("name");

				final Persistor persistor = PersistorUtil.createPersistor(type);

				if (persistor == null) {
					throw new PersistError("Do not know how to load: " + type);
				}
				final EncogPersistedObject obj = persistor.load(in);
				this.contents.put(name, obj);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}

	/**
	 * Save the contents of this collection to a location.
	 * 
	 * @param location
	 *            The location to save to.
	 */
	public void save(final PersistenceLocation location) {
		PersistWriter writer = null;

		writer = new PersistWriter(location);
		try {
			writer.begin();
			writer.writeHeader();
			writer.beginObjects();
			for (final EncogPersistedObject obj : this.contents.values()) {
				writer.writeObject(obj);
			}
			writer.endObjects();
		} finally {
			writer.end();
			writer.close();
		}
	}
}
