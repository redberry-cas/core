/*
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2012:
 *   Stanislav Poslavsky   <stvlpos@mail.ru>
 *   Bolotin Dmitriy       <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Redberry. If not, see <http://www.gnu.org/licenses/>.
 */
package cc.redberry.core.tensor;

import cc.redberry.core.indexmapping.*;
import cc.redberry.core.utils.TensorUtils;
import org.junit.Assert;
import org.junit.Test;

import static cc.redberry.core.tensor.Tensors.parse;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class SumBuilderForIndexlessTensorsTest {

    @Test
    public void test1() {
        SumBuilder isb = new SumBuilder();
        isb.put(parse("a"));
        isb.put(parse("2*a"));
        isb.put(parse("-3*a"));
        isb.put(parse("a*b"));
        isb.put(parse("7*a*b"));
        isb.put(parse("Sin[c]"));
        isb.put(parse("d"));
        isb.put(parse("Sin[-c]"));

        Tensor expected = Tensors.parse("8*a*b+d");
        Assert.assertTrue(TensorUtils.equals(expected, isb.build()));
    }

    @Test
    public void test2() {
        SumBuilder isb = new SumBuilder();
        Tensor expected = Tensors.parse("0");
        Assert.assertTrue(TensorUtils.equals(expected, isb.build()));
    }

    @Test
    public void test3() {
        SumBuilder isb = new SumBuilder();
        isb.put(parse("a"));
        isb.put(parse("2*a"));
        isb.put(parse("-3*a"));
        isb.put(parse("0"));
        isb.put(parse("-Power[d,2]"));
        isb.put(parse("Sin[c]"));
        isb.put(parse("Power[d,2]"));
        isb.put(parse("Sin[-c]"));
        isb.put(parse("(1/2)*Cos[-c]"));
        isb.put(parse("(1/2)*Cos[-c]"));

        Tensor expected = Tensors.parse("Cos[c]");
        Assert.assertTrue(IndexMappings.mappingExists(expected, isb.build(), false));
    }

    @Test
    public void test5() {
        SumBuilder isb = new SumBuilder();
        isb.put(parse("a_mn"));
        isb.put(parse("2*a_mn"));
        isb.put(parse("-3*a_mn"));
      

        System.out.println(isb.build());
        Tensor expected = Tensors.parse("0");
        Assert.assertTrue(IndexMappings.mappingExists(expected, isb.build(), false));
    }
}