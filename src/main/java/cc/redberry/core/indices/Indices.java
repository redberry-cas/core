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
package cc.redberry.core.indices;

import cc.redberry.core.context.ToStringMode;
import cc.redberry.core.indexmapping.IndexMapping;
import cc.redberry.core.utils.IntArray;

/**
 * This interface states common tensor indices functionality. For specification
 * and more information see method summary and implementations. Indices objects
 * are considered to be immutable, so there is no way to change indices. <p>For
 * individual index structure see <link>IndicesUtils</link>.</p>
 *
 * @see IndicesBuilder
 * @see IndexMapping
 * @see IndicesUtils
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public interface Indices {

    /**
     * Return {@link IntArray} of upper case indices. Due to immutability of
     * indices this method returns simple wrapper of generic integer array.
     *
     * @return IntArray of upper case indices
     *
     * @see IntArray
     */
    IntArray getUpper();

    /**
     * Return {@link IntArray} of lower case indices. Due to immutability of
     * indices this method returns simple wrapper of generic integer array.
     *
     * @return IntArray of lower case indices
     *
     * @see IntArray
     */
    IntArray getLower();

    /**
     * Return {@link IntArray} of all indices. Due to immutability of indices
     * this method returns simple wrapper of generic integer array.
     *
     * @return IntArray of all indices
     */
    IntArray getAllIndices();

    /**
     * Returns the number of indices
     *
     * @return number of indices
     */
    int size();

    /**
     * Returns the index at the specified position in this
     * <code>Indices</code>.
     *
     * @param position position of the index to return
     *
     * @return the index at the specified position in this
     * <code>Indices</code>
     *
     * @throws IndexOutOfBoundsException - if the index is out of range (index <
     *                                   0 || index >= size())
     */
    int get(int position);

    /**
     * Returns new instance of
     * <code>Indices</code>, witch contains only non contracted indices from
     * this
     * <code>Indices</code> instance. Returned Indices shall conserve relevant
     * order of indices.
     * <p/>
     * <h4><a name="Indices">Example:</a></h4> If Indices are
     * <code>_{mn}^{nop}</code>, getFreeIndices() will return
     * <code>Indices</code> that are
     * <code>_{n}^{op}</code>.
     *
     * @return non contracted indices Indices instance
     */
    //TODO rename to getFree()
    Indices getFreeIndices();

    /**
     * Returns new instance of Indices, witch contains inverse indices, i.e. all
     * this indices with inverse states. Result Indices have the same order with
     * original one. Symmetries of inverse indices is same as initial indices
     * and they are not cloning (same references).
     * <p/>
     * <h4><a name="Indices">Example:</a></h4> If Indices are
     * <code>_{mn}^{nop}</code>, getInverseIndices() will return Indices that
     * are
     * <code>^{mn}_{nop}</code>
     *
     * @return same indices but with the inverse states
     */
    //TODO rename to getInverse()
    Indices getInverseIndices();

    /**
     * Returns true if this indices are equals to specified indices, without
     * regard for order. Only we interesting in this method, is equality of the
     * sets of indices. So, e.g. for _{ab}^c and ^c_{ba} this method will return
     * true, but for _{ab}^c and ^c_{ak}, of course it returns false.
     *
     * @param indices indices to be compared
     *
     * @return true if the set of specified indices equals to this one
     */
    boolean equalsRegardlessOrder(Indices indices);

    /**
     * Checks for the consistence of this Indices. Package specification forbids
     * more than one index with the same case, type and value ,i.e. _{a...a}
     * indices
     */
    void testConsistentWithException();

    /**
     * This method applies specified {@code IndexMapping} to this indices and
     * returns true if mapping changed indices and false if not.
     *
     * @param mapping specified {@code IndexeMapping}
     *
     * @return true if mapping changed indices and false if not
     *
     * @see IndexMapping
     */
    Indices applyIndexMapping(IndexMapping mapping);

    /**
     * This method returns string representation of indices due to symbols print
     * mode, specified in
     * <code>enum</code> {@link ToStringMode}. General convention for indices
     * output is LaTeX-base code for indices, i.e. string
     * <code>_{mn}^{a}</code> represents indices with length 3, where first
     * <code>m</code> down, second
     * <code>n</code> down and third
     * <code>a</code> up.
     * <p/>
     * <h4><a name="Indices">Example for
     * <code>_{&alpha &beta}^{&gamma &delta}</code>:</a></h4>
     * <code>LaTeX</code>:
     * <code>_{\alpha \beta}^{\gamma \delta}</code> <p><code>UTF8</code>:
     * <code>_{&alpha &beta}^{&gamma &delta}</code>
     *
     * @param mode symbols printing mode
     *
     * @return string representation due to mode
     *
     * @see ToStringMode
     */
    String toString(ToStringMode mode);

    @Override
    boolean equals(Object other);

    /**
     * Returns the array of indices <b>id</b>s with respect to symmetries. First
     * of all, for SortedIndices, it always returns the array with length equal
     * to #size() and filled by zeros. For SimpleIndices we assume, that each
     * index in Indices data array have some <b>id</b>, which is {@code short}.
     * If there is no any symmetries, index <b>id</b> is equal to the index
     * position in Indices data array. If there is a symmetry, which transpose
     * two indices, then their <b>id</b>s are equal, otherwise their <b>id</b>s
     * are not equals. For example, if we consider the following symmetries: [2,
     * 1, 0, 3, 4, 5, 6, 7] and [0, 1, 2, 3, 7, 4, 6, 5], the resulting diffIds
     * array will be the following: [0, 0, 1, 2, 3, 3, 4, 3]
     *
     * @return the array of indices <b>id</b>s with respect to symmetries
     */
    short[] getDiffIds();
}
