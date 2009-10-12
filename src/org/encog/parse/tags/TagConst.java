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
