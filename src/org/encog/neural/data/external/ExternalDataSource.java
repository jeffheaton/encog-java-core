package org.encog.neural.data.external;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogMemoryCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.location.FilePersistence;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.persistors.generic.GenericPersistor;
import org.encog.util.simple.EncogUtility;

public class ExternalDataSource implements EncogPersistedObject, NeuralDataSet,
		Indexable {

	/**
	 * The resource name of this data source.
	 */
	private String name;

	/**
	 * The description of this data source.
	 */
	private String description;

	/**
	 * The type of this resource, currently the only supported type is
	 * TYPE_FILE.
	 */
	private String type = TYPE_FILE;

	/**
	 * The location of the file.
	 */
	private String link;

	/**
	 * The location of the temp binary file.
	 */
	@EGIgnore
	private File tempBinary;

	/**
	 * The temp binary file to use.
	 */
	@EGIgnore
	private transient BufferedNeuralDataSet binary;

	/**
	 * The number of inputs.
	 */
	private int inputCount;

	/**
	 * The number of ideal outputs.
	 */
	private int idealCount;

	private boolean headers;
	
	private boolean initDone = false;
	
	/**
	 * The Encog collection this object belongs to, or null if none.
	 */
	@EGIgnore
	private transient EncogCollection encogCollection;

	public static final String TYPE_FILE = "FILE";

	@Override
	public Persistor createPersistor() {
		return new GenericPersistor(ExternalDataSource.class);
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;

	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	public void setLink(File file) {
		this.link = file.getAbsolutePath();
	}

	@Override
	public void add(NeuralData data1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(NeuralData inputData, NeuralData idealData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(NeuralDataPair inputData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getIdealSize() {
		return this.idealCount;
	}

	@Override
	public int getInputSize() {
		return this.inputCount;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @param inputCount
	 *            the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	/**
	 * @return the idealCount
	 */
	public int getIdealCount() {
		return idealCount;
	}

	/**
	 * @param idealCount
	 *            the idealCount to set
	 */
	public void setIdealCount(int idealCount) {
		this.idealCount = idealCount;
	}

	/**
	 * @return the headers
	 */
	public boolean isHeaders() {
		return headers;
	}

	/**
	 * @param headers
	 *            the headers to set
	 */
	public void setHeaders(boolean headers) {
		this.headers = headers;
	}

	public Iterator<NeuralDataPair> iterator() {
		if(!this.initDone)
			init();
		return this.binary.iterator();
	}

	public void init() {
			File linkFile = this.obtainRelativeLink();
			File path = linkFile.getParentFile();
			String name = linkFile.getName();
			int idx = name.lastIndexOf('.');
			if( idx!=-1 )
				name = name.substring(0,idx);
			name+=".bin";
			
			this.tempBinary = new File(path,name);
			
			if( !this.tempBinary.exists() || this.tempBinary.lastModified()<linkFile.lastModified())
			{			
				EncogUtility.convertCSV2Binary(linkFile,
					this.tempBinary, this.inputCount, this.idealCount,
					this.headers);
			}
			
			this.binary = new BufferedNeuralDataSet(this.tempBinary);
			this.initDone = true;
	}

	public void dispose() {
		if (this.binary != null) {
			this.binary.close();
			this.tempBinary.delete();
			this.tempBinary = null;
			this.binary = null;
		}
	}

	/**
	 * @return True if this training data is supervised.
	 */
	@Override
	public boolean isSupervised() {
		return this.idealCount > 0;
	}

	@Override
	public void getRecord(long index, NeuralDataPair pair) {
		this.binary.getRecord(index, pair);
	}

	@Override
	public long getRecordCount() {
		if( !this.initDone )
			init();
		return this.binary.getRecordCount();
	}

	@Override
	public Indexable openAdditional() {
		return this.binary.openAdditional();
	}

	/**
	 * @return The collection this Encog object belongs to, null if none.
	 */
	public EncogCollection getCollection() {
		return this.encogCollection;
	}

	/**
	 * Set the Encog collection that this object belongs to.
	 */
	public void setCollection(EncogCollection collection) {
		this.encogCollection = collection; 
	}

	public File obtainRelativeLink() {
		PersistenceLocation location = this.getCollection().getLocation();
		
		if (location instanceof FilePersistence) {
			FilePersistence fp = (FilePersistence) location;
			File egParentDir = fp.getFile().getParentFile();
			File linkLocation = new File(this.link);

			if (egParentDir != null) {
				if (linkLocation.getParent() == null) {
					return new File(egParentDir, this.link);
				}
			}
		}
		return new File(this.link);
	}

	public void storeRelativeLink(File linkLocation, EncogMemoryCollection encogMemoryCollection) {
		PersistenceLocation location = encogMemoryCollection.getLocation();
		
		if (location instanceof FilePersistence) {
			FilePersistence fp = (FilePersistence) location;
			String egParentDir = fp.getFile().getParent();

			if( egParentDir != null )
			{
				if( linkLocation.getParent()!=null && linkLocation.getParent().equals(egParentDir) )
				{
					this.link = linkLocation.getName();
					return;
				}
			}
		}
		this.link = linkLocation.toString();

	}

	@Override
	public Iterator<?> createIterator() {
		return this.iterator();
	}

	
}
