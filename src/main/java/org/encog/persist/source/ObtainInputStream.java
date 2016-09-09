package org.encog.persist.source;

import java.io.InputStream;

/**
 * Obtain an input stream in some way.  This class is used to abstract between reading an embedded test file and
 * an external one.  For example, common datasets, such as Iris may be embedded in an example application, however
 * the option should still be available to read these files externally from CSV files.
 */
public interface ObtainInputStream {
    /**
     * @return An input stream.
     */
    InputStream obtain();
}
