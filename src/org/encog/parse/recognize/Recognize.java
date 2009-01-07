/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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

public class Recognize {
	private List<RecognizeElement> pattern = new ArrayList<RecognizeElement>();
	private int index = 0;
	private String type;
	private int startIndex = -1;
	private int stopIndex;
	private int currentIndex = 0;
	private boolean ignore = false;
	@SuppressWarnings("unchecked")
	private Class signalClass = Signal.class;

	public Recognize(String type) {
		this.type = type;
	}
	
	public Recognize()
	{
		this.type = "Untitled";
	}

	public void add(RecognizeElement re) {
		pattern.add(re);
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public boolean getIgnore() {
		return this.ignore;
	}

	private void startTracking() {
		// System.out.println("Start tracking");
		if (startIndex == -1)
			startIndex = currentIndex;
		stopIndex = currentIndex + 1;
	}

	private void stopTracking() {
		// System.out.println("Stop tracking");
		startIndex = -1;
		index = 0;
	}

	public boolean recognize(Signal signal) {

		boolean found;
		do {
			found = recognizeIteration(signal);
		} while (found);
		return found;

	}

	protected boolean recognizeIteration(Signal signal) {
		startIndex = -1;
		index = 0;
		currentIndex = 0;

		Object array[] = signal.getData().toArray();
		while (currentIndex < array.length) {

			RecognizeElement re = pattern.get(index);
			Signal signalElement = (Signal) array[currentIndex];

			if (signalElement.getIgnore()) {
				currentIndex++;
				continue;
			}

			// System.out.println("Recognize Element:" + signalElement.dump() );
			boolean success = re.recognize(signalElement);

			switch (re.getAllow()) {
			case RecognizeElement.ALLOW_ONE:
				if (success) {
					startTracking();
					index++;
				} else {
					stopTracking();
				}
				break;

			case RecognizeElement.ALLOW_MULTIPLE:
				if (success) {
					startTracking();
				} else
					index++;

				break;
			}

			if (index >= pattern.size()) {

				if (startIndex != -1) {
					Signal temp = signal.pack(startIndex, stopIndex, type,
							getSignalClass());
					temp.setName(re.getName());
					temp.setIgnore(ignore);
					return true;
				}
				index = 0;
			}
			currentIndex++;

		}
		return false;
	}

	public RecognizeElement createElement(int allow) {
		RecognizeElement result = new RecognizeElement(allow);
		add(result);
		return result;
	}

	@SuppressWarnings("unchecked")
	public Class getSignalClass() {
		return signalClass;
	}

	@SuppressWarnings("unchecked")
	public void setSignalClass(Class signalClass) {
		this.signalClass = signalClass;
	}
	
	
	
	/**
	 * @return the pattern
	 */
	public List<RecognizeElement> getPattern() {
		return pattern;
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[Recognize:");
		result.append(this.type);
		result.append(']');
		return result.toString();
	}
}
