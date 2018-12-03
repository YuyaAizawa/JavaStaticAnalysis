/* @(#)ReadableCollection.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides an API for reading a collection.
 *
 * @param <E> the element type
 */
public interface ReadableCollection<E> extends Iterable<E> {
    /**
     * Returns the size of the collection.
     *
     * @return the size
     */
    int size();

    /**
     * Returns true if the collection is empty.
     *
     * @return true if empty
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Converts the collection to an array.
     *
     * @param a a template array
     * @return an array
     */
    default <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        //noinspection unchecked
        T[] r = a.length >= size ? a : (T[]) Arrays.copyOf(a, size, a.getClass());
        Iterator<E> it = iterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                throw new ConcurrentModificationException("fewer elements than expected. expected=" + size);
            }
            //noinspection unchecked
            r[i] = (T) it.next();
        }
        if (it.hasNext()) {
            throw new ConcurrentModificationException("more elements than expected. expected=" + size);
        }
        return r;
    }

    /**
     * Returns a stream.
     *
     * @return a stream
     */
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    boolean contains(E e);
}
