package org.encog.normalize.segregate.index;


public class IndexSampleSegregator extends IndexSegregator {

	private final int startingIndex;
	private final int endingIndex;
	private final int sampleSize;
	
	public IndexSampleSegregator(int startingIndex, int endingIndex, int sampleSize)
	{
		this.sampleSize = sampleSize;
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}
	
	public boolean shouldInclude() {
		int sampleIndex = this.getCurrentIndex()%this.sampleSize;
		rollIndex();
		return(sampleIndex>=this.startingIndex && sampleIndex<=this.endingIndex);
	}

	public int getStartingIndex() {
		return startingIndex;
	}

	public int getEndingIndex() {
		return endingIndex;
	}

	public int getSampleSize() {
		return sampleSize;
	}

}
