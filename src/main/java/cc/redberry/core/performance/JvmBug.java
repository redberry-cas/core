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
package cc.redberry.core.performance;

import cc.redberry.core.context.CC;
import cc.redberry.core.tensor.Tensors;
import java.util.*;

/**
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class JvmBug {

    public static void main(String[] args) {
        int[] a;
        int n = 0;
        for (int i = 0; i < 100000000; ++i) {
            a = new int[10];
            for (int f : a)
                if (f != 0)
                    throw new RuntimeException("Array just after allocation: "+ Arrays.toString(a));
            Arrays.fill(a, 0);
            for (int j = 0; j < a.length; ++j)
                a[j] = (n - j)*i;
            for (int f : a)
                n += f;
        }
        System.out.println(n);
    }
}
