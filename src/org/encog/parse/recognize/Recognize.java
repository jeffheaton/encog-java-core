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
package org.encog.parse.recognize;

import java.util.ArrayList;
import java.util.List;

import org.encog.parse.signal.Signal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the template of something that the parser should recognize. This
 * consists of a collection of RecognizeElement objects that define the
 * structure of what is to be recognized.
 * 
 * @author jheaton
 * 
 */
public class Recognize {
	
	/**
	 * Recognize elements used by this recognize class. 
	 */
	private final List<RecognizeElement> pattern = new ArrayList<RecognizeElement>();
	private int index = 0;
	private final String type;
	private int startIndex = -1;
	private int stopIndex;
	private int currentIndex = 0;
	private boolean ignore = false;
	@SuppressWarnings("unchecked")
	private Class signalClass = Signal.class;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Recognize() {
		this.type = "Untitled";
	}

	public Recognize(final String type) {
		this.type = type;
	}

	public void add(final RecognizeElement re) {
		this.pattern.add(re);
	}

	public RecognizeElement createElement(final int allow) {
		final RecognizeElement result = new RecognizeElement(allow);
		add(result);
		return result;
	}

	public boolean getIgnore() {
		return this.ignore;
	}

	/**
	 * @return the pattern
	 */
	public List<RecognizeElement> getPattern() {
		return this.pattern;
	}

	@SuppressWarnings("unchecked")
	public Class getSignalClass() {
		return this.signalClass;
	}

	public boolean recognize(final Signal signal) {

		boolean found;
		do {
			found = recognizeIteration(signal);
		} while (found);
		return found;

	}

	protected boolean recognizeIteration(final Signal signal) {
		this.startIndex = -1;
		this.index = 0;
		this.currentIndex = 0;

		final Object array[] = signal.getData().toArray();
		while (this.currentIndex < array.length) {

			final RecognizeElement re = this.pattern.get(this.index);
			final Signal signalElement = (Signal) array[this.currentIndex];

			if (signalElement.getIgnore()) {
				this.currentIndex++;
				continue;
			}

			// System.out.println("Recognize Element:" + signalElement.dump() );
			final boolean success = re.recognize(signalElement);

			switch (re.getAllow()) {
			case RecognizeElement.ALLOW_ONE:
				if (success) {
					startTracking();
					this.index++;
				} else {
					stopTracking();
				}
				break;

			case RecognizeElement.ALLOW_MULTIPLE:
				if (success) {
					startTracking();
				} else {
					this.index++;
				}

				break;
			}

			if (this.index >= this.pattern.size()) {

				if (this.startIndex != -1) {
					final Signal temp = signal.pack(this.startIndex,
							this.stopIndex, this.type, getSignalClass());
					temp.setName(re.getName());
					temp.setIgnore(this.ignore);
					return true;
				}
				this.index = 0;
			}
			this.currentIndex++;

		}
		return false;
	}

	public void setIgnore(final boolean ignore) {
		this.ignore = ignore;
	}

	@SuppressWarnings("unchecked")
	public void setSignalClass(final Class signalClass) {
		this.signalClass = signalClass;
	}

	private void startTracking() {
		// System.out.println("Start tracking");
		if (this.startIndex == -1) {
			this.startIndex = this.currentIndex;
		}
		this.stopIndex = this.currentIndex + 1;
	}

	private void stopTracking() {
		// System.out.println("Stop tracking");
		this.startIndex = -1;
		this.index = 0;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[Recognize:");
		result.append(this.type);
		result.append(']');
		return result.toString();
	}
}
