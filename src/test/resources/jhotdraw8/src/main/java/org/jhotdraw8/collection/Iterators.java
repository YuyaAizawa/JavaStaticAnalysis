/* @(#)Collections.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

/**
 * Collections.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Iterators {

    private Iterators() {
    }

    /**
     * Creates a list from an {@code Iterable}.
     * If the {@code Iterable} is a list, it is returned.
     *
     * @param <T> the value type
     * @param iterable the iterable
     * @return the list
     */
    @Nonnull
    public static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List<?>) return (List<T>)iterable;
        ArrayList<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
