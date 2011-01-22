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

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.encog.Encog;
import org.encog.neural.networks.BasicNetwork;
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
public class EncogPersistedCollection implements EncogCollection {
	/**
	 * The location this collection is saved at.
	 */
	private PersistenceLocation location;
	
	/**
	 * Generic error message for bad XML.
	 */
	public static final String GENERAL_ERROR = "Malformed XML near tag: ";

	/**
	 * The type is TextData.
	 */
	public static final String TYPE_TEXT = "TextData";

	/**
	 * The type is PropertyData.
	 */
	public static final String TYPE_PROPERTY = "PropertyData";

	/**
	 * The type is BasicNetwork.
	 */
	public static final String TYPE_BASIC_NET = "BasicNetwork";

	/**
	 * The type is BasicLayer.
	 */
	public static final String TYPE_BASIC_LAYER = "BasicLayer";

	/**
	 * The type is ContextLayer.
	 */
	public static final String TYPE_CONTEXT_LAYER = "ContextLayer";

	/**
	 * The type is RadialBasisFunctionLayer.
	 */
	public static final String TYPE_RADIAL_BASIS_LAYER = "RadialBasisFunctionLayer";

	/**
	 * The type is TrainingData.
	 */
	public static final String TYPE_TRAINING = "TrainingData";

	/**
	 * The type is WeightedSynapse.
	 */
	public static final String TYPE_WEIGHTED_SYNAPSE = "WeightedSynapse";

	/**
	 * The type is WeightlessSynapse.
	 */
	public static final String TYPE_WEIGHTLESS_SYNAPSE = "WeightlessSynapse";

	/**
	 * The type is DirectSynapse.
	 */
	public static final String TYPE_DIRECT_SYNAPSE = "DirectSynapse";

	/**
	 * The type is OneToOneSynapse.
	 */
	public static final String TYPE_ONE2ONE_SYNAPSE = "OneToOneSynapse";

	/**
	 * The type is ParseTemplate.
	 */
	public static final String TYPE_NORMALIZATION = "DataNormalization";

	/**
	 * The name attribute.
	 */
	public static final String ATTRIBUTE_NAME = "name";

	/**
	 * The description attribute.
	 */
	public static final String ATTRIBUTE_DESCRIPTION = "description";

	/**
	 * The logging object.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EncogPersistedCollection.class);

	/**
	 * For training continuation.
	 */
	public static final String TYPE_TRAINING_CONTINUATION = "TrainingContinuation";

	/**
	 * The basic population.
	 */
	public static final String TYPE_POPULATION = "BasicPopulation";

	public static final String TYPE_SVM = "SVMNetwork";

	public static final String TYPE_BINARY = "BufferedNeuralDataSet";
	
	public static final String TYPE_SCRIPT = "EncogScript";

	/**
	 * Throw and log an error.
	 * 
	 * @param tag
	 *            The tag this error is for.
	 */
	public static void throwError(final String tag) {
		final String str = EncogPersistedCollection.GENERAL_ERROR + tag;
		if (EncogPersistedCollection.LOGGER.isErrorEnabled()) {
			EncogPersistedCollection.LOGGER.error(str);
		}
		throw new PersistError(str);
	}

	/**
	 * The primary file being persisted to.
	 */
	private final PersistenceLocation filePrimary;

	/**
	 * The temp file, to be used for merges.
	 */
	private PersistenceLocation fileTemp;

	/**
	 * A writer to use with the file.
	 */
	@SuppressWarnings("unused")
	private PersistWriter writer;

	/**
	 * The platform this collection was created on.
	 */
	private String platform = "Java";

	/**
	 * The version of the persisted file.
	 */
	private int fileVersion = 1;

	/**
	 * Directory entries for all of the objects in the current file.
	 */
	private final Set<DirectoryEntry> directory = new TreeSet<DirectoryEntry>();

	/**
	 * The version of Encog.
	 */
	private String encogVersion = Encog.getInstance().getProperties().get(
			Encog.ENCOG_VERSION);

	/**
	 * Create a persistance collection for the specified file.
	 * 
	 * @param file
	 *            The file to load/save.
	 */
	public EncogPersistedCollection(final File file) {
		this(new FilePersistence(file));
	}

	/**
	 * Create an object based on the specified location.
	 * 
	 * @param location
	 *            The location to load/save from.
	 */
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
				try {
					buildDirectory();
				} catch (final PersistError e) {
					create();
				}
			} else {
				create();
			}
		} else {
			this.fileTemp = null;
		}
	}

	/**
	 * Construct an object with the specified filename.
	 * 
	 * @param filename
	 *            The filename to load/save from.
	 */
	public EncogPersistedCollection(final String filename) {
		this(new File(filename));
	}

	/**
	 * Add an EncogPersistedObject to the collection.
	 * 
	 * @param name
	 *            The name of the object to load.
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

	/**
	 * Build a directory of objects. Also load the header information.
	 */
	public void buildDirectory() {
		PersistReader reader = null;

		try {
			reader = new PersistReader(this.filePrimary);
			final Map<String, String> header = reader.readHeader();
			if (header != null) {
				this.fileVersion = Integer.parseInt(header.get("fileVersion"));
				this.encogVersion = header.get("encogVersion");
				this.platform = header.get("platform");
			}
			final Set<DirectoryEntry> d = reader.buildDirectory();
			this.directory.clear();
			this.directory.addAll(d);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Clear the collection.
	 */
	public void clear() {

	}

	/**
	 * Create the file.
	 */
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

	/**
	 * Delete the specified object, use a directory entry.
	 * 
	 * @param d
	 *            The object to delete.
	 */
	public void delete(final DirectoryEntry d) {
		this.delete(d.getName());

	}

	/**
	 * Delete the specified object.
	 * 
	 * @param obj
	 *            The object to delete.
	 */
	public void delete(final EncogPersistedObject obj) {
		delete(obj.getName());
	}

	/**
	 * Delete the specified object.
	 * 
	 * @param name
	 *            the object name.
	 */
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

	/**
	 * Find the specified object, using a DirectoryEntry.
	 * 
	 * @param d
	 *            The directory entry to find.
	 * @return The loaded object.
	 */
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
		if( result!=null )
			result.setCollection(this);
		return result;
	}

	/**
	 * @return The directory entries for the objects in this file.
	 */
	public Collection<DirectoryEntry> getDirectory() {
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

	/**
	 * Merge the temp file with the main one, call this to make any changes
	 * permanent.
	 */
	public void mergeTemp() {
		this.filePrimary.delete();
		this.fileTemp.renameTo(this.filePrimary);
	}

	/**
	 * Update any header properties for an Encog object, for example, a rename.
	 * 
	 * @param name
	 *            The name of the object to change.
	 * @param newName
	 *            The new name of this object.
	 * @param newDesc
	 *            The description for this object.
	 */
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

	/**
	 * Determine if the specified resource exists.
	 * @param name The name of the resource to check.
	 * @return True if it exists.
	 */
	public boolean exists(String name) {
		for( DirectoryEntry dir: this.directory )
		{
			if( dir.getName().equals(name))
				return true;
		}
		return false;
	}

	public PersistenceLocation getLocation()
	{
		return this.location;
	}
}
