/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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
package org.encog.persist.source;

import org.encog.EncogError;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Obtain an InputStream from an embedded resource.
 */
public class ObtainResourceInputStream implements ObtainInputStream {

    /**
     * The resources.
     */
    private Class resources;

    /**
     * The name of the embedded resource.
     */
    private String resourceName;

    /**
     * Construct the source for the specified resource name.
     * @param theResourceName The resource name to read from.
     * @param theResources Where to pull the resources from.
     */
    public ObtainResourceInputStream(String theResourceName, Class theResources) {
        this.resources = theResources;
        this.resourceName = theResourceName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream obtain() {
        final InputStream istream = this.resources.getResourceAsStream("/"+resourceName);
        if (istream == null) {
            throw new EncogError("Cannot access data set, make sure the resources are available.");
        }
        return istream;
    }
}
