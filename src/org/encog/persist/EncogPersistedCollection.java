/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.persist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.persist.location.FilePersistence;
import org.encog.persist.location.PersistenceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EncogPersistedCollection holds a collection of EncogPersistedObjects. This
 * allows the various neural networks and some data sets to be persisted. They
 * are persisted to an XML form.
 * 
 * The EncogPersistedCollection does not load the object into memory at once.
 * This allows it to manage large files.
 * 
 * @author jheaton
 * 
 */
public class EncogPersistedCollection {

	public final static String GENERAL_ERROR = "Malformed XML near tag: ";

	public static final String TYPE_TEXT = "TextData";
	public static final String TYPE_PROPERTY = "PropertyData";
	public static final String TYPE_BASIC_NET = "BasicNetwork";
	public static final String TYPE_BASIC_LAYER = "BasicLayer";
	public static final String TYPE_CONTEXT_LAYER = "ContextLayer";
	public static final String TYPE_RADIAL_BASIS_LAYER = "RadialBasisFunctionLayer";
	public static final String TYPE_TRAINING = "TrainingData";
	public static final String TYPE_WEIGHTED_SYNAPSE = "WeightedSynapse";
	public static final String TYPE_WEIGHTLESS_SYNAPSE = "WeightlessSynapse";
	public static final String TYPE_DIRECT_SYNAPSE = "DirectSynapse";
	public static final String TYPE_ONE2ONE_SYNAPSE = "OneToOneSynapse";
	public static final String TYPE_PARSE_TEMPLATE = "ParseTemplate";

	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_DESCRIPTION = "description";

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private static Logger logger = LoggerFactory
			.getLogger(EncogPersistedCollection.class);

	public static void throwError(final String tag) {
		final String str = EncogPersistedCollection.GENERAL_ERROR + tag;
		if (EncogPersistedCollection.logger.isErrorEnabled()) {
			EncogPersistedCollection.logger.error(str);
		}
		throw new PersistError(str);
	}

	private final PersistenceLocation filePrimary;

	private PersistenceLocation fileTemp;

	private PersistWriter writer;

	/**
	 * The platform this collection was created on.
	 */
	private String platform;

	/**
	 * The version of the persisted file.
	 */
	private int fileVersion;

	private final List<DirectoryEntry> directory = new ArrayList<DirectoryEntry>();

	/**
	 * The version of Encog.
	 */
	private String encogVersion;

	public EncogPersistedCollection(final File file) {
		this(new FilePersistence(file));
	}

	public EncogPersistedCollection(final PersistenceLocation location) {
		this.filePrimary = location;

		if (this.filePrimary instanceof FilePersistence) {
			final File file = ((FilePersistence) this.filePrimary).getFile();
			String f = file.getAbsolutePath();
			final int index = f.lastIndexOf('.');
			if (index != -1) {
				f = f.substring(0, index);
			}
			f += ".tmp";
			this.fileTemp = new FilePersistence(new File(f));

			if (this.filePrimary.exists()) {
				buildDirectory();
			} else {
				create();
			}
		} else {
			this.fileTemp = null;
		}
	}

	public EncogPersistedCollection(final String filename) {
		this(new File(filename));
	}

	/**
	 * Add an EncogPersistedObject to the collection.
	 * 
	 * @param obj
	 *            The object to add.
	 */
	public void add(final String name, final EncogPersistedObject obj) {
		obj.setName(name);
		final PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();
		writer.writeObject(obj);
		writer.mergeObjects(this.filePrimary, name);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		buildDirectory();
	}

	public void buildDirectory() {
		final PersistReader reader = new PersistReader(this.filePrimary);
		final List<DirectoryEntry> d = reader.buildDirectory();
		this.directory.clear();
		this.directory.addAll(d);
		reader.close();
	}

	/**
	 * Clear the collection.
	 */
	public void clear() {

	}

	public void create() {
		final PersistWriter writer = new PersistWriter(this.filePrimary);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();
		writer.endObjects();
		writer.end();
		writer.close();

		this.directory.clear();
	}

	public void delete(final DirectoryEntry d) {
		this.delete(d.getName());

	}

	public void delete(final EncogPersistedObject obj) {
		delete(obj.getName());
	}

	public void delete(final String name) {
		final PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();
		writer.mergeObjects(this.filePrimary, name);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		for (final DirectoryEntry d : this.directory) {
			if (d.getName().equals(name)) {
				this.directory.remove(d);
				break;
			}
		}
	}

	public EncogPersistedObject find(final DirectoryEntry d) {
		return find(d.getName());
	}

	/**
	 * Called to search all Encog objects in this collection for one with a name
	 * that passes what was passed in.
	 * 
	 * @param name
	 *            The name we are searching for.
	 * @return The Encog object with the correct name.
	 */
	public EncogPersistedObject find(final String name) {

		final PersistReader reader = new PersistReader(this.filePrimary);
		final EncogPersistedObject result = reader.readObject(name);
		reader.close();
		return result;
	}

	public List<DirectoryEntry> getDirectory() {
		return this.directory;
	}

	/**
	 * @return the encogVersion
	 */
	public String getEncogVersion() {
		return this.encogVersion;
	}

	/**
	 * @return the fileVersion
	 */
	public int getFileVersion() {
		return this.fileVersion;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return this.platform;
	}

	public void mergeTemp() {
		this.filePrimary.delete();
		this.fileTemp.renameTo(this.filePrimary);
	}

	public void updateProperties(final String name, final String newName,
			final String newDesc) {
		final PersistWriter writer = new PersistWriter(this.fileTemp);
		writer.begin();
		writer.writeHeader();
		writer.beginObjects();
		writer.modifyObject(this.filePrimary, name, newName, newDesc);
		writer.endObjects();
		writer.end();
		writer.close();
		mergeTemp();
		buildDirectory();

	}

}
