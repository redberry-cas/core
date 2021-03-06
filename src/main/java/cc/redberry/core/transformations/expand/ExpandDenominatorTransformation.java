/*
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2013:
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
package cc.redberry.core.transformations.expand;

import cc.redberry.core.tensor.Product;
import cc.redberry.core.tensor.Tensor;
import cc.redberry.core.tensor.TensorField;
import cc.redberry.core.tensor.Tensors;
import cc.redberry.core.tensor.functions.ScalarFunction;
import cc.redberry.core.tensor.iterator.TraverseGuide;
import cc.redberry.core.tensor.iterator.TraversePermission;
import cc.redberry.core.transformations.Transformation;
import cc.redberry.core.transformations.fractions.NumeratorDenominator;

import static cc.redberry.core.utils.TensorUtils.isPositiveIntegerPower;

/**
 * Expands out products and powers that appear as denominators in expressions.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @since 1.0
 */
public final class ExpandDenominatorTransformation extends AbstractExpandTransformation {
    /**
     * Default traverse guide.
     */
    public static TraverseGuide ExpandDenominatorTraverseGuide = new TraverseGuide() {
        @Override
        public TraversePermission getPermission(Tensor tensor, Tensor parent, int indexInParent) {
            if (tensor instanceof ScalarFunction)
                return TraversePermission.DontShow;
            if (tensor instanceof TensorField)
                return TraversePermission.DontShow;
            if (isPositiveIntegerPower(tensor))
                return TraversePermission.DontShow;
            return TraversePermission.Enter;
        }
    };
    /**
     * The default instance.
     */
    public static final ExpandDenominatorTransformation EXPAND_DENOMINATOR = new ExpandDenominatorTransformation();

    private ExpandDenominatorTransformation() {
        super(new Transformation[0], ExpandDenominatorTraverseGuide);
    }

    /**
     * Creates expand transformation with specified additional transformations to
     * be applied after each step of expand.
     *
     * @param transformations transformations to be applied after each step of expand
     */
    public ExpandDenominatorTransformation(Transformation[] transformations) {
        super(transformations, ExpandDenominatorTraverseGuide);
    }

    /**
     * Creates expand transformation with specified additional transformations to
     * be applied after each step of expand and leaves unexpanded parts of expression specified by
     * {@code traverseGuide}.
     *
     * @param transformations transformations to be applied after each step of expand
     * @param traverseGuide   traverse guide
     */
    public ExpandDenominatorTransformation(Transformation[] transformations, TraverseGuide traverseGuide) {
        super(transformations, traverseGuide);
    }

    /**
     * Expands out products and powers that appear as denominators of tensor.
     *
     * @param tensor tensor to be transformed
     * @return result
     */
    public static Tensor expandDenominator(Tensor tensor) {
        return EXPAND_DENOMINATOR.transform(tensor);
    }

    /**
     * Expands out products and powers that appear as denominators of tensor and applies specified transformations
     * after each step of expand.
     *
     * @param tensor tensor to be transformed
     * @return result
     */
    public static Tensor expandDenominator(Tensor tensor, Transformation... transformations) {
        return new ExpandDenominatorTransformation(transformations).transform(tensor);
    }

    @Override
    protected Tensor expandProduct(Product product, Transformation[] transformations) {
        NumeratorDenominator numDen = NumeratorDenominator.getNumeratorAndDenominator(product, NumeratorDenominator.integerDenominatorIndicator);
        Tensor denominator = numDen.denominator;
        if (denominator instanceof Product)
            denominator = ExpandUtils.expandProductOfSums((Product) numDen.denominator, transformations);
        if (numDen.denominator == denominator)
            return product;
        return Tensors.multiply(numDen.numerator, Tensors.reciprocal(denominator));
    }
}
