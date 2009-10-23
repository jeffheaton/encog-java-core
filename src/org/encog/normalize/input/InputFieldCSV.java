/**
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

package org.encog.normalize.input;

import java.io.File;

import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReferenceable;

/**
 * An input field based on a CSV file.
 */
@EGReferenceable
public class InputFieldCSV extends BasicInputField {

	/**
	 * The file to read.
	 */
	private File file;

	/**
	 * The CSV column represented by this field.
	 */
	@EGAttribute
	private int offset;

	/**
	 * Construct an InputFieldCSV with the default constructor.  This is mainly
	 * used for reflection.
	 */
	public InputFieldCSV() {

	}

	/**
	 * Construct a input field for a CSV file.
	 * @param usedForNetworkInput True if this field is used for actual input
	 * to the neural network, as opposed to segregation only.
	 * @param file The tile to read.
	 * @param offset The CSV file column to read.
	 */
	public InputFieldCSV(final boolean usedForNetworkInput, final File file,
			final int offset) {
		this.file = file;
		this.offset = offset;
		setUsedForNetworkInput(usedForNetworkInput);
	}

	/**
	 * @return The file being read.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @return The column in this CSV file to read.
	 */
	public int getOffset() {
		return this.offset;
	}
}
