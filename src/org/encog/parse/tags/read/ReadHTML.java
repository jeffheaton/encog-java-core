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
package org.encog.parse.tags.read;

import java.io.InputStream;

/**
 * This class is designed to parse HTML documents. It will parse the individual
 * tags and text between the tags.
 * 
 * @author jheaton
 * 
 */
public class ReadHTML extends ReadTags {

	/**
	 * Construct a HTML reader.
	 * 
	 * @param is
	 *            The input stream to read from.
	 */
	public ReadHTML(final InputStream is) {
		super(is);
	}

	/**
	 * Parse the attribute name.
	 * 
	 * @return The attribute name.
	 */
	protected String parseAttributeName() {
		String result = super.parseAttributeName();
		return result.toLowerCase();
	}

}
