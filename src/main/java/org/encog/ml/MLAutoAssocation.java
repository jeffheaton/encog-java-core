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
package org.encog.ml;

/**
 * Defines a MLMethod that can handle autoassocation.  Autoassociation is a 
 * simple form of pattern recognition where the MLMethod echos back the 
 * exact pattern that the input most closely matches.  For example, if the 
 * autoassociative MLMethod were trained to recognize an 8x8 grid of 
 * characters, the return value would be the entire 8x8 grid of the 
 * character recognized.
 * 
 * This is the type of recognition performed by Hopfield Networks.  It is
 * also an optional recognition form used by GR/PNN's.  This is a form of
 * unsupervised training.
 *
 */
public interface MLAutoAssocation extends MLRegression {

}
