package org.encog.normalize.segregate.index;

public class IndexRangeSegregator extends IndexSegregator {
	
	private final int startingIndex;
	
	private final int endingIndex;

	public IndexRangeSegregator(int startingIndex, int endingIndex)
	{
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}
	
	public boolean shouldInclude() {
		boolean result = ( getCurrentIndex()>=this.startingIndex && getCurrentIndex()<=endingIndex );
		rollIndex();
		return result;
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public int getEndingIndex() {
		return endingIndex;
	}

	
	
}
