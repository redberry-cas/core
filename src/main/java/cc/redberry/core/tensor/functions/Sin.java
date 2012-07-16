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
package cc.redberry.core.tensor.functions;

import cc.redberry.core.number.*;
import cc.redberry.core.tensor.*;
import cc.redberry.core.utils.*;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class Sin extends AbstractScalarFunction {

    Sin(Tensor argument) {
        super(argument);
    }

    @Override
    public Tensor derivative() {
        return new Cos(argument);
    }

    @Override
    protected int hash() {
        return 7 * argument.hashCode();
    }

    @Override
    public String functionName() {
        return "Sin";
    }

    @Override
    public TensorBuilder getBuilder() {
        return new ScalarFunctionBuilder(SinFactory.FACTORY);
    }

    @Override
    public TensorFactory getFactory() {
        return SinFactory.FACTORY;
    }

    public static final class SinFactory extends AbstractScalarFunctionFactory {

        public static final SinFactory FACTORY = new SinFactory();

        private SinFactory() {
        }

        @Override
        public Tensor create1(Tensor arg) {
            if (arg instanceof ArcSin)
                return arg.get(0);
            if (TensorUtils.isZero(arg))
                return Complex.ZERO;
            return new Sin(arg);
        }
    }
}
