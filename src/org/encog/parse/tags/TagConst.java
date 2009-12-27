/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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

package org.encog.parse.tags;

/**
 * Constants to use while parsing the tags.
 * 
 * @author jheaton
 * 
 */
public final class TagConst {

	/**
	 * The beginning of a comment.
	 */
	public static final String COMMENT_BEGIN = "!--";

	/**
	 * The end of a comment.
	 */
	public static final String COMMENT_END = "-->";

	/**
	 * The beginning of a CDATA section.
	 */
	public static final String CDATA_BEGIN = "![CDATA[";

	/**
	 * THe end of a CDATA section.
	 */
	public static final String CDATA_END = "]]";

	/**
	 * Private constructor.
	 */
	private TagConst() {

	}
}
