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
package cc.redberry.core;

import cc.redberry.core.indexmapping.*;
import cc.redberry.core.tensor.*;
import java.util.*;
import java.util.Map;
import java.util.regex.*;
import org.junit.Test;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class BlackList {

    public enum Name {

        E1("a"), E2("b");
        final String name;

        Name(String name) {
            this.name = name;
        }
        public static final Name[] values = values();
    }

    @Test
    public void te() {
       List<Tensor> l = new ArrayList<>();
        System.out.println(l instanceof List);
    }
    
    
}
