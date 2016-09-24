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
     * The resources.
     */
    private Class resources;

    /**
     * Construct the source.
     * @param thePath The path.
     * @param theDatasetName The name of the dataset.
     * @param theResources Where to pull the resources from.
     */
    public ObtainFallbackStream(String thePath, String theDatasetName, Class theResources) {
        this.datasetName = theDatasetName;
        this.path = thePath;
        this.resources = theResources;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        File target = null;
        final InputStream istream = this.resources.getResourceAsStream("/"+this.datasetName);
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
