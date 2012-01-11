/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.app.analyst.util;

import org.encog.StatusReportable;
import org.encog.app.analyst.AnalystListener;
import org.encog.app.analyst.EncogAnalyst;

/**
 * Used to bridge the AnalystListerner to an StatusReportable object.
 * 
 */
public class AnalystReportBridge implements StatusReportable {

	/**
	 * The analyst to bridge to.
	 */
	private final EncogAnalyst analyst;

	/**
	 * Construct the bridge object.
	 * @param theAnalyst The Encog analyst to use.
	 */
	public AnalystReportBridge(final EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void report(final int total, final int current, 
			final String message) {
		for (final AnalystListener listener : this.analyst.getListeners()) {
			listener.report(total, current, message);
		}

	}

}
