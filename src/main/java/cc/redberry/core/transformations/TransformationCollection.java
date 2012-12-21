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
package cc.redberry.core.transformations;

import cc.redberry.core.tensor.Tensor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class TransformationCollection implements Transformation {
    private final Transformation[] transformations;

    public TransformationCollection(Collection<Transformation> transformations) {
        //todo if collection in collection
        this.transformations = transformations.toArray(new Transformation[transformations.size()]);
    }

    public TransformationCollection(Transformation... transformations) {
        this.transformations = transformations.clone();
    }

    @Override
    public Tensor transform(Tensor t) {
        for (Transformation tr : transformations)
            t = tr.transform(t);
        return t;
    }

    public List<Transformation> getTransformations() {
        return Collections.unmodifiableList(Arrays.asList(transformations));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; ; ++i) {
            sb.append(transformations[i]);
            if (i == transformations.length - 1)
                break;
            sb.append("\n");
        }
        return sb.toString();
    }
}
