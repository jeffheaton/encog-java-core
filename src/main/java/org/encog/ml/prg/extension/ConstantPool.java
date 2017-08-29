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
package org.encog.ml.prg.extension;

import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.mathutil.randomize.generate.MersenneTwisterGenerateRandom;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.util.EngineArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jeffh on 9/13/2016.
 */
public class ConstantPool implements ProgramExtensionTemplate {

    public static double COMMON_CONST[] = { 0, 1, 2, 10, 0.1, 0.5, 0.25};

    /**
     * The constant pool.
     */
    private final ExpressionValue[] pool;

    /**
     * The name of the constant.
     */
    private final String name;

    private final ParamTemplate returnValue;

    /**
     * Construct for a specific constant pool.
     * @param theName The name of the pool.
     * @param thePool The pool of values.
     */
    public ConstantPool(String theName, double[] thePool) {
        this.pool = new ExpressionValue[thePool.length];
        for(int i=0;i<thePool.length;i++) {
            ExpressionValue value = new ExpressionValue(thePool[i]);
            pool[i] = value;
        }
        this.name = theName;
        this.returnValue = new ParamTemplate();
        this.returnValue.addType("f");
    }

    /**
     * Construct for a specific constant pool and random constants.
     * @param theName The name of the pool.
     * @param thePool The pool of values.
     * @param count The number of random values to generate, total size is length of pool + count.
     * @param rnd Random number generator.
     * @param low The low end of the random range.
     * @param high The high end of the random range.
     */
    public ConstantPool(String theName, double[] thePool, int count, GenerateRandom rnd, double low, double high) {
        this.pool = new ExpressionValue[thePool.length+count];
        for(int i=0;i<thePool.length;i++) {
            ExpressionValue value = new ExpressionValue(thePool[i]);
            pool[i] = value;
        }
        for(int i=0;i<count;i++) {
            ExpressionValue value = new ExpressionValue(rnd.nextDouble(low,high));
            pool[thePool.length+i] = value;
        }
        this.name = theName;
        this.returnValue = new ParamTemplate();
        this.returnValue.addType("f");
    }

    /**
     * Construct with common constants, plus 100 random constants between -10 and 10.
     */
    public ConstantPool() {
        this("#CONST_POOL_100",COMMON_CONST, 100,new MersenneTwisterGenerateRandom(42),-10,10);
    }

    @Override
    public ExpressionValue evaluate(ProgramNode actual) {
        return this.pool[(int)actual.getData()[0].toIntValue()];
    }

    @Override
    public int getChildNodeCount() {
        return 0;
    }

    @Override
    public int getDataSize() {
        return 1;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.Leaf;
    }

    @Override
    public List<ParamTemplate> getParams() {
        return ProgramExtensionTemplate.NO_PARAMS;
    }

    @Override
    public int getPrecedence() {
        return ProgramExtensionTemplate.NO_PREC;
    }

    @Override
    public ParamTemplate getReturnValue() {
        return this.returnValue;
    }

    @Override
    public boolean isPossibleReturnType(EncogProgramContext context, ValueType rtn) {
        return this.returnValue.getPossibleTypes().contains(rtn);
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public void randomize(Random rnd, List<ValueType> desiredType, ProgramNode actual, double minValue, double maxValue) {
        actual.getData()[0] = new ExpressionValue(rnd.nextInt(this.pool.length));
    }
}
