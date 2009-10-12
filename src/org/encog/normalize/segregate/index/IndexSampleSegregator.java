package org.encog.normalize.segregate.index;

public class IndexSampleSegregator extends IndexSegregator {

	private final int startingIndex;
	private final int endingIndex;
	private final int sampleSize;

	public IndexSampleSegregator(final int startingIndex,
			final int endingIndex, final int sampleSize) {
		this.sampleSize = sampleSize;
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}

	public int getEndingIndex() {
		return this.endingIndex;
	}

	public int getSampleSize() {
		return this.sampleSize;
	}

	public int getStartingIndex() {
		return this.startingIndex;
	}

	public boolean shouldInclude() {
		final int sampleIndex = getCurrentIndex() % this.sampleSize;
		rollIndex();
		return ((sampleIndex >= this.startingIndex) && (sampleIndex <= this.endingIndex));
	}

}
