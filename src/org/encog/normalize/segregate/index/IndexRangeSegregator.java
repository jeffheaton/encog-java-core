package org.encog.normalize.segregate.index;

public class IndexRangeSegregator extends IndexSegregator {

	private final int startingIndex;

	private final int endingIndex;

	public IndexRangeSegregator(final int startingIndex, final int endingIndex) {
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
	}

	public int getEndingIndex() {
		return this.endingIndex;
	}

	public int getStartingIndex() {
		return this.startingIndex;
	}

	public boolean shouldInclude() {
		final boolean result = ((getCurrentIndex() >= this.startingIndex) && (getCurrentIndex() <= this.endingIndex));
		rollIndex();
		return result;
	}

}
