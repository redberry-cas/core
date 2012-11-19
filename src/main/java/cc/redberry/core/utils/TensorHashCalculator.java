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
 * the Free Software Foundation, either version 2 of the License, or
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
package cc.redberry.core.utils;

import cc.redberry.core.indices.Indices;
import cc.redberry.core.indices.SimpleIndices;
import cc.redberry.core.tensor.Product;
import cc.redberry.core.tensor.ProductContent;
import cc.redberry.core.tensor.SimpleTensor;
import cc.redberry.core.tensor.Tensor;
import cc.redberry.core.tensor.functions.ScalarFunction;

import java.util.Arrays;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class TensorHashCalculator {

    private TensorHashCalculator() {
    }

    private static int _hashWithIndices(final Tensor tensor, final int[] indices) {
        if (tensor instanceof SimpleTensor) {
            SimpleIndices si = ((SimpleTensor) tensor).getIndices();
            short[] sInds = si.getDiffIds();
            int hash = tensor.hashCode();
            int pos;
            for (int i = 0; i < si.size(); ++i)
                if ((pos = Arrays.binarySearch(indices, si.get(i))) >= 0)
                    hash += (HashFunctions.JenkinWang32shift(sInds[i])
                            * HashFunctions.JenkinWang32shift(pos) * 7);
            return HashFunctions.JenkinWang32shift(hash);
        }
        if (tensor instanceof ScalarFunction)
            return tensor.hashCode();

        int hash = tensor.hashCode();
        if (tensor instanceof Product) {
            ProductContent pc = ((Product) tensor).getContent();
            //TODO may be refactor with noncommutative operation using stretcIds 
            for (int i = pc.size() - 1; i >= 0; --i)
                hash += HashFunctions.JenkinWang32shift((int) pc.getStretchId(i)) * _hashWithIndices(pc.get(i), indices);
            return hash;
        }

        for (Tensor t : tensor)
            hash ^= _hashWithIndices(t, indices);
        return hash;
    }

    public static int hashWithIndices(final Tensor tensor, final int[] indices) {
        if (indices.length == 0)
            return tensor.hashCode();
        Arrays.sort(indices);
        return _hashWithIndices(tensor, indices);
    }

    public static int hashWithIndices(final Tensor tensor, final Indices indices) {
        return hashWithIndices(tensor, indices.getAllIndices().copy());
    }

    public static int hashWithIndices(final Tensor tensor) {
        return hashWithIndices(tensor, tensor.getIndices().getFree());
    }
}
