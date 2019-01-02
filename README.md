![Encog Machine Learning Framework](http://www.heatonresearch.com/images/encog128.png)

[![Build Status](https://travis-ci.org/encog/encog-java-core.svg?branch=master)](https://travis-ci.org/encog/encog-java-core)

Encog Machine Learning Framework
--------------------------------

Encog is a pure-Java/C# machine learning framework that I created back in 2008 to support genetic programming, NEAT/HyperNEAT, and other neural network technologies.  Originally, Encog was created to support research for my master’s degree and early books.  The neural network aspects of Encog proved popular, and Encog was used by a number of people and is cited by [952 academic papers in Google Scholar](https://scholar.google.com/scholar?hl=en&as_sdt=0%2C26&q=encog&btnG=). I created Encog at a time when there were not so many well developed frameworks, such as [TensorFlow](https://medium.com/tensorflow/standardizing-on-keras-guidance-on-high-level-apis-in-tensorflow-2-0-bad2b04c819a), [Keras](https://keras.io/), [DeepLearning4J](https://deeplearning4j.org/), and many others (these are the frameworks I work with the most these days for neural networks).   

Encog continues to be developed (and bugs fixed) for the types of models not covered by the large frameworks and to provide a pure non-GPU Java/C# implementation of several classic neural networks.  Because it is pure Java, the source code for Encog can be much simpler to adapt for cases where you want to implement the neural network yourself from scratch. Some of the less mainstream technologies supported by Encog include [NEAT](https://en.wikipedia.org/wiki/Neuroevolution_of_augmenting_topologies), [HyperNEAT](https://en.wikipedia.org/wiki/HyperNEAT), and [Genetic Programming](https://en.wikipedia.org/wiki/Genetic_programming). Encog has minimal support for computer vision.  Computer vision is a fascinating topic, but just has never been a research interest of mine.

Encog supports a variety of advanced algorithms, as well as support classes to normalize and process data. Machine learning algorithms such as Support Vector Machines, Neural Networks, Bayesian Networks, Hidden Markov Models, Genetic Programming and Genetic Algorithms are supported. Most Encog training algorithms are multi-threaded and scale well to multicore hardware.

Encog continues to be developed, and is used in my own research, for areas that I need Java and are not covered by Keras.  However, for larger-scale cutting edge work, where I do not need to implement the technology from scratch, I make use of Keras/TensorFlow for my own work.

For more information: [Encog Website](http://www.encog.org)

Simple Java XOR Example in Encog
--------------------------------

```java
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class XORHelloWorld {

	/**
	 * The input necessary for XOR.
	 */
	public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 },
			{ 0.0, 1.0 }, { 1.0, 1.0 } };

	/**
	 * The ideal data necessary for XOR.
	 */
	public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

	/**
	 * The main method.
	 * @param args No arguments are used.
	 */
	public static void main(final String args[]) {

		// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,2));
		network.addLayer(new BasicLayer(new ActivationReLU(),true,5));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
		network.getStructure().finalizeStructure();
		network.reset();

		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);

		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

		int epoch = 1;

		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > 0.01);
		train.finishTraining();

		// test the neural network
		System.out.println("Neural Network Results:");
		for(MLDataPair pair: trainingSet ) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		}

		Encog.getInstance().shutdown();
	}
}
```
