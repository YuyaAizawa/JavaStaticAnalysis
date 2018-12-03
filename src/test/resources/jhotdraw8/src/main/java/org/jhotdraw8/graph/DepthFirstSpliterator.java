/* @(#)DepthFirstSpliterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * DepthFirstSpliterator.
 *
 * @author Werner Randelshofer
 * @param <V> the vertex type
 */
public class DepthFirstSpliterator<V> extends AbstractSpliterator<V> {

    @Nullable
    private final Function<V, Iterable<V>> nextNodesFunction;
    @Nonnull
    private final Deque<V> stack;
    @Nullable
    private final Predicate<V> visited;

    /**
     * Creates a new instance.
     *
     * @param nextNodesFuncction the nextNodesFunction
     * @param root the root vertex
     */
    public DepthFirstSpliterator(Function<V, Iterable<V>> nextNodesFuncction, V root) {
        this(nextNodesFuncction,root,new HashSet<>()::add);
    }

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction the nextNodesFunction
     * @param root the root vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     */
    public DepthFirstSpliterator(@Nullable Function<V, Iterable<V>> nextNodesFunction, @Nullable V root, @Nullable Predicate<V> visited) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        if (nextNodesFunction == null) {
            throw new IllegalArgumentException("nextNodesFunction==null");
        }
        if (root == null) {
            throw new IllegalArgumentException("root==null");
        }
        if (visited == null) {
            throw new IllegalArgumentException("visited==null");
        }
        this.nextNodesFunction = nextNodesFunction;
        stack = new ArrayDeque<>(16);
        this.visited = visited;
        stack.push(root);
        visited.test(root);
    }


    @Override
    public boolean tryAdvance(@Nonnull Consumer<? super V> action) {
        V current = stack.pollFirst();
        if (current == null) {
            return false;
        }
        for (V next : nextNodesFunction.apply(current)) {
            if (visited.test(next)) {
                stack.push(next);
            }
        }
        action.accept(current);
        return true;
    }
}
