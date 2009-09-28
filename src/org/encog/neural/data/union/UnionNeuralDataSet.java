package org.encog.neural.data.union;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A UnionNeuralDataSet is used to create a compound data set that is made
 * up of other data sets.  The union set will iterate through all of the date
 * of the subsets in the order that they were added to the union.  There
 * are a number of uses for this sort of a dataset.  One is for processing
 * extremely large SQL datasets.  You can break your query into multiple
 * SQLNeuralDataSet objects and use the UnionNeuralDataSet to cause them
 * to appear as one large dataset.  
 * 
 * The UnionNeuralDataSet can also be used to combine several different
 * dataset types into one.
 * 
 * You must specify the ideal and input sizes.  All subsets must adhear
 * to these sizes.
 *
 */
public class UnionNeuralDataSet implements NeuralDataSet {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The error to report when the user attempts an ADD.
	 */
	private final static String ADD_ERROR = "Add is not supported in UnionNeuralDataSet";
	
	/**
	 * The error to report when the user attempts a remove.
	 */
	private final static String REMOVE_ERROR = "Remove is not supported in UnionNeuralDataSet";
	
	/**
	 * The size of the input data.
	 */
	private int inputSize;
	
	/**
	 * The size of the ideal data.
	 */
	private int idealSize;
	
	/**
	 * The subsets that this union is made up of.
	 */
	private List<NeuralDataSet> subsets = new ArrayList<NeuralDataSet>();
	
	/**
	 * The iterators that have been created so far for this data set.
	 */
	private Collection<UnionIterator> iterators = new ArrayList<UnionIterator>();
	
	/**
	 * The iterator used to access the UnionNeuralDataSet.
	 *
	 */
	public class UnionIterator implements Iterator<NeuralDataPair>
	{
		/**
		 * The next subset.
		 */
		private int currentSet;
		
		/**
		 * An iterator to the current subset.
		 */
		private Iterator<NeuralDataPair> currentIterator;

		/**
		 * Construct the union iterator.  This sets the current set
		 * and current iterators.
		 */
		public UnionIterator()
		{
			this.currentSet = 1;
			this.currentIterator = subsets.get(0).iterator();
		}
		
		/**
		 * Determine if there is more data to be read from this iterator.
		 * @return True if there is more data to read.
		 */
		public boolean hasNext() {
			
			if( !this.currentIterator.hasNext() )
			{
				if( this.currentSet< subsets.size())
				{
					this.currentIterator = subsets.get(currentSet++).iterator();					
				}
				return currentIterator.hasNext();
			}
			else
				return true;
			
			
		}

		/**
		 * Obtain the next piece of data.
		 * @return The next data pair.
		 */
		public NeuralDataPair next() {
			return this.currentIterator.next();
		}

		/**
		 * Not implemented.  Will throw an error.
		 */
		public void remove() {
			logger.error(REMOVE_ERROR);
			throw new NeuralDataError(REMOVE_ERROR);			
		}


		
	}
	
	/**
	 * Not supported.
	 * @param data1 Not supported.
	 */
	public void add(NeuralData data1) {
		this.logger.error(ADD_ERROR);
		throw new NeuralDataError(ADD_ERROR);				
	}

	/**
	 * Not supported.
	 * @param inputData Not supported.
	 * @param idealData Not supported. 
	 */
	public void add(NeuralData inputData, NeuralData idealData) {
		this.logger.error(ADD_ERROR);
		throw new NeuralDataError(ADD_ERROR);
	}

	/**
	 * Not supported.
	 * @param inputData Not supported.
	 */
	public void add(NeuralDataPair inputData) {
		this.logger.error(ADD_ERROR);
		throw new NeuralDataError(ADD_ERROR);
	}

	/**
	 * Close the dataset.
	 */
	public void close() {
		this.iterators.clear();
	}

	/**
	 * @return The array size of the ideal data.
	 */
	public int getIdealSize() {		
		return this.idealSize;
	}

	/**
	 * @return The array size of the input data.
	 */
	public int getInputSize() {
		return this.inputSize;
	}

	/**
	 * Obtain an iterator to access the collection of data.
	 * @return The new iterator.
	 */
	public Iterator<NeuralDataPair> iterator() {
		UnionIterator result = new UnionIterator();
		this.iterators.add(result);
		return result;
	}
	
	/**
	 * 
	 * @return A collection of the subsets that make up this union.
	 */
	public List<NeuralDataSet> getSubsets()
	{
		return this.subsets;
	}
	
	/**
	 * Construct the union data set.  All subsets must have input and
	 * ideal sizes to match the parameters specified to this constructor.
	 * For unsupervised training specify 0 for idealSize.
	 * @param inputSize The array size of the input data.
	 * @param idealSize The array size of the ideal data.
	 */
	public UnionNeuralDataSet(final int inputSize, final int idealSize)
	{
		this.inputSize = inputSize;
		this.idealSize = idealSize;
	}
	
	/**
	 * Add a subset.  This method will validate that the input and
	 * ideal sizes are correct.
	 * @param set The subset to add.
	 */
	public void addSubset(NeuralDataSet set)
	{
		if( set.getInputSize() != this.inputSize )
		{
			String str = "Subset input size of " + set.getInputSize() +
			" must match union input size of " + inputSize;
			logger.error(str);
			throw new NeuralDataError(str);
		}
		else if( set.getIdealSize() != this.idealSize )
		{
			String str = "Subset ideal size of " + set.getIdealSize() +
			" must match union ideal size of " + idealSize;
			logger.error(str);
			throw new NeuralDataError(str);
		}
		else
			this.subsets.add(set);
		
	}

}
