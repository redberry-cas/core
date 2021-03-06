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
package cc.redberry.core.indexmapping;

import cc.redberry.core.number.Complex;
import cc.redberry.core.tensor.*;
import cc.redberry.core.tensor.functions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central public facade of this package. Provides static methods for calculation of mappings between indices.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 * @since 1.0
 */
public final class IndexMappings {

    private IndexMappings() {
    }

    /**
     * Creates output port of mappings of two simple tensors and do not takes into account the arguments of fields.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return port of mappings of indices
     */
    public static MappingsPort simpleTensorsPort(SimpleTensor from, SimpleTensor to) {
        final IndexMappingProvider provider = ProviderSimpleTensor.FACTORY_SIMPLETENSOR.create(IndexMappingProvider.Util.singleton(new IndexMappingBufferImpl()), from, to);
        provider.tick();
        return new MappingsPortRemovingContracted(provider);
    }

    /**
     * Creates output port of mappings of two products of tensors represented as arrays of multipliers, where
     * each multiplier of {@code from} will be mapped on the multiplier of {@code to} at the same
     * position. Such ordering can be obtained via {@link cc.redberry.core.transformations.substitutions.ProductsBijectionsPort}.
     * In contrast to {@link #createPort(cc.redberry.core.tensor.Tensor, cc.redberry.core.tensor.Tensor)}, this method
     * will fully handles mappings of free indices on contracted ones (like e.g. _i^j -> _k^k).
     *
     * @param from from tensor
     * @param to   to tensor
     * @return port of mappings of indices
     */
    public static MappingsPort createBijectiveProductPort(Tensor[] from, Tensor[] to) {
        if (from.length != to.length)
            throw new IllegalArgumentException("From length != to length.");
        if (from.length == 0)
            return IndexMappingProvider.Util.singleton(new IndexMappingBufferImpl());
        if (from.length == 1)
            return createPort(from[0], to[0]);
        return new MappingsPortRemovingContracted(new SimpleProductMappingsPort(IndexMappingProvider.Util.singleton(new IndexMappingBufferImpl()), from, to));
    }

    /**
     * Creates output port of mappings of tensor {@code from} on tensor {@code to}.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return output port of mappings
     */
    public static MappingsPort createPort(Tensor from, Tensor to) {
        return createPort(new IndexMappingBufferImpl(), from, to);
    }

    /**
     * Creates output port of mappings of tensor {@code from} on tensor {@code to} with specified
     * mappings rules defined in specified {@link IndexMappingBuffer}.
     *
     * @param buffer initial mapping rules
     * @param from   from tensor
     * @param to     to tensor
     * @return output port of mapping
     */
    public static MappingsPort createPort(final IndexMappingBuffer buffer,
                                          final Tensor from, final Tensor to) {
        final IndexMappingProvider provider = createPort(IndexMappingProvider.Util.singleton(buffer), from, to);
        provider.tick();
        return new MappingsPortRemovingContracted(provider);
    }

    /**
     * Returns the first mapping of tensor {@code from} on tensor {@code to}.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return mapping of indices of tensor {@code from} on tensor {@code to}
     */
    public static IndexMappingBuffer getFirst(Tensor from, Tensor to) {
        return createPort(from, to).take();
    }

    /**
     * Returns {@code true} if there is mapping of tensor {@code from} on tensor {@code to}.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return {@code true} if there is mapping of tensor {@code from} on tensor {@code to}
     */
    public static boolean mappingExists(Tensor from, Tensor to) {
        return getFirst(from, to) != null;
    }

    /**
     * Returns {@code true} if there is positive mapping of tensor {@code from} on tensor {@code to}.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return {@code true} if there is positivemapping of tensor {@code from} on tensor {@code to}
     */
    public static boolean positiveMappingExists(Tensor from, Tensor to) {
        IndexMappingBuffer buffer;
        MappingsPort port = createPort(from, to);
        while ((buffer = port.take()) != null)
            if (!buffer.getSign())
                return true;
        return false;
    }

    /**
     * Returns {@code true} if specified mapping is one of mappings of tensor {@code from} on tensor {@code to}.
     *
     * @param buffer mapping
     * @param from   from tensor
     * @param to     to tensor
     * @return Returns {@code true} if specified mapping is one of mappings of indices of tensor {@code from} on tensor {@code to}.
     */
    public static boolean testMapping(Tensor from, Tensor to, IndexMappingBuffer buffer) {
        return createPort(new IndexMappingBufferTester(buffer), from, to).take() != null;
    }

    private static Tensor extractNonComplexFactor(Tensor t) {
        Product p = (Product) t;
        if (p.getFactor().isMinusOne())
            return p.get(1);
        else
            return null;
    }

    static IndexMappingProvider createPort(IndexMappingProvider opu, Tensor from, Tensor to) {
        if (from.hashCode() != to.hashCode())
            return IndexMappingProvider.Util.EMPTY_PROVIDER;

        if (from.getClass() != to.getClass()) {

            Tensor nonComplex;
            //Processing case -2*(1/2)*g_mn -> g_mn
            if (from instanceof Product && !(to instanceof Product)) {
                if (from.size() != 2)
                    return IndexMappingProvider.Util.EMPTY_PROVIDER;

                if ((nonComplex = extractNonComplexFactor(from)) != null)
                    return new MinusIndexMappingProviderWrapper(createPort(opu, nonComplex, to));
                return IndexMappingProvider.Util.EMPTY_PROVIDER;
            }

            //Processing case g_mn -> -2*(1/2)*g_mn
            if (to instanceof Product && !(from instanceof Product)) {
                if (to.size() != 2)
                    return IndexMappingProvider.Util.EMPTY_PROVIDER;
                if ((nonComplex = extractNonComplexFactor(to)) != null)
                    return new MinusIndexMappingProviderWrapper(createPort(opu, from, nonComplex));
                return IndexMappingProvider.Util.EMPTY_PROVIDER;
            }

            return IndexMappingProvider.Util.EMPTY_PROVIDER;
        }

        IndexMappingProviderFactory factory = map.get(from.getClass());
        if (factory == null)
            throw new RuntimeException("Unsupported tensor type: " + from.getClass());

        return factory.create(opu, from, to);
    }

    private static final Map<Class, IndexMappingProviderFactory> map;

    static {
        map = new HashMap<>();
        map.put(SimpleTensor.class, ProviderSimpleTensor.FACTORY_SIMPLETENSOR);
        map.put(TensorField.class, ProviderSimpleTensor.FACTORY_TENSORFIELD);
        map.put(Product.class, ProviderProduct.FACTORY);
        map.put(Sum.class, ProviderSum.FACTORY);
        map.put(Expression.class, ProviderSum.FACTORY);
        map.put(Complex.class, ProviderComplex.FACTORY);
        map.put(Power.class, ProviderPower.INSTANCE);

        map.put(Sin.class, ProviderFunctions.ODD_FACTORY);
        map.put(ArcSin.class, ProviderFunctions.ODD_FACTORY);
        map.put(Tan.class, ProviderFunctions.ODD_FACTORY);
        map.put(ArcTan.class, ProviderFunctions.ODD_FACTORY);

        map.put(Cos.class, ProviderFunctions.EVEN_FACTORY);
        map.put(ArcCos.class, ProviderFunctions.EVEN_FACTORY);
        map.put(Cot.class, ProviderFunctions.EVEN_FACTORY);
        map.put(ArcCot.class, ProviderFunctions.EVEN_FACTORY);
    }

    private static Set<IndexMappingBuffer> getAllMappings(MappingsPort opu) {
        Set<IndexMappingBuffer> res = new HashSet<>();
        IndexMappingBuffer c;
        while ((c = opu.take()) != null)
            res.add(c);
        return res;
    }

    /**
     * Returns a set of all possible mappings of tensor {@code from} on tensor {@code to}.
     *
     * @param from from tensor
     * @param to   to tensor
     * @return a set of all possible mappings of tensor {@code from} on tensor {@code to}
     */
    public static Set<IndexMappingBuffer> getAllMappings(Tensor from, Tensor to) {
        return getAllMappings(IndexMappings.createPort(from, to));
    }
}
