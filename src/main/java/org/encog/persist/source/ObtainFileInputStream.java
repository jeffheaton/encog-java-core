package org.encog.persist.source;

import org.encog.EncogError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Obtain an input stream from a file.
 */
public class ObtainFileInputStream implements ObtainInputStream {

    /**
     * The file to read.
     */
    private File file;

    /**
     * Construct the source.
     * @param theFile The file source for the input stream.
     */
    public ObtainFileInputStream(File theFile) {
        this.file = theFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException ex) {
            throw new EncogError(ex);
        }
    }
}
