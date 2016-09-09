package org.encog.persist.source;

import org.encog.EncogError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A fallback stream that will attempt to read from the resource first, failing that, read from a file.
 */
public class ObtainFallbackStream implements ObtainInputStream {
    /**
     * The name of the file to read.
     */
    private String datasetName;

    /**
     * The path.
     */
    private String path;

    /**
     * Construct the source.
     * @param theDatasetName The filename.
     */
    public ObtainFallbackStream(String theDatasetName, String thePath) {
        this.datasetName = theDatasetName;
        this.path = thePath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        File target = null;
        final InputStream istream = this.getClass().getResourceAsStream("/"+this.datasetName);
        if (istream == null) {
            try {
                target = new File(this.path,this.datasetName);
                return new FileInputStream(target);
            } catch (FileNotFoundException e) {
                throw new EncogError("Cannot access data set, make sure the resources are available: " + target);
            }

        }
        return istream;
    }
}
