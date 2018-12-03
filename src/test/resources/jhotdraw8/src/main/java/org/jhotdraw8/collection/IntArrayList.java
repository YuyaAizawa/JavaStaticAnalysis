/* @(#)IntArrayList.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Integer.max;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

/**
 * A lightweight int array list implementation for performance critical code.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntArrayList {

    private int[] items;
    /**
     * Holds the size of the list. Invariant: size >= 0.
     */
    private int size;

    /**
     * Creates a new empty instance with 0 initial capacity.
     */
    public IntArrayList() {
    }

    /**
     * Creates a new empty instance with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity
     */
    public IntArrayList(int initialCapacity) {
        increaseCapacity(initialCapacity);
    }

    /**
     * Creates a new instance from the specified collection
     *
     * @param collection a collection of integers
     */
    public IntArrayList(Collection<Integer> collection) {
        this.size = collection.size();
        this.items = new int[size];

        int count = 0;
        for (Iterator<Integer> iter = collection.iterator(); iter.hasNext();) {
            Integer value = iter.next();
            items[count++] = value;
        }
    }

    private IntArrayList(int[] items) {
        this.items = items;
        this.size = items.length;
    }

    /**
     * Adds a new item to the end of the list.
     *
     * @param newItem the new item
     */
    public void add(int newItem) {
        increaseCapacity(size + 1);
        items[size++] = newItem;
    }

    /**
     * Inserts a new item at the specified index into this list.
     *
     * @param index the index
     * @param newItem the new item
     */
    public void add(int index, int newItem) {
        rangeCheck(index, size + 1);
        increaseCapacity(size + 1);
        System.arraycopy(items, index, items, index + 1, size - index);
        items[index] = newItem;
        ++size;
    }

    /**
     * Adds all items of the specified list to this list.
     *
     * @param that another list
     */
    public void addAll(@Nonnull IntArrayList that) {
        if (that.isEmpty()) {
            return;
        }
        increaseCapacity(size + that.size);
        System.arraycopy(that.items, 0, this.items, this.size, that.size);
        this.size += that.size;
    }

    /**
     * Adds all items of this collection to the specified collection.
     *
     * @param <T> the type of the collection
     * @param out the output collection
     * @return out
     */
    @Nonnull
    public <T extends Collection<Integer>> T addAllInto(@Nonnull T out) {
        for (int i = 0, n = size; i < n; i++) {
            out.add(items[i]);
        }
        return out;
    }

    public void clear() {
        size = 0;
    }

    /**
     * Copies the contents of this list into the provided array.
     *
     * @param a an array
     * @param offset the offset into the array
     */
    public void copyInto(@Nonnull int[] a, int offset) {
        System.arraycopy(items, 0, a, offset, size);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntArrayList other = (IntArrayList) obj;
        if (other.size != this.size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (other.items[i] != this.items[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the item at the specified index.
     *
     * @param index an index
     * @return the item at the index
     */
    public int get(int index) {
        rangeCheck(index, size);
        return items[index];
    }

    /**
     * Sets the size of this list. If the new size is greater than the current
     * size, new {@code 0} items are added to the end of the list. If the new
     * size is is less than the current size, all items at indices greater or
     * equal {@code newSize} are discarded.
     *
     * @param newSize the new size
     */
    public void setSize(int newSize) {
        increaseCapacity(newSize);
        if (newSize > size) {
            Arrays.fill(items, size, newSize, 0);
        }
        size = newSize;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = 31 * result + items[i];
        }

        return result;
    }

    private void increaseCapacity(int capacity) {
        if (capacity <= size) {
            return;
        }
        if (items == null) {
            items = new int[capacity];
        }
        int newCapacity = max(capacity, size + size / 2); // grow by 50%
        int[] newItems = new int[newCapacity];
        System.arraycopy(items, 0, newItems, 0, size);
        items = newItems;
    }

    /**
     * Returns the first index of the item, or -1 if this list does not contain
     * the item.
     *
     * @param item the item
     * @return the index of the item, or -1.
     */
    public int indexOf(int item) {
        for (int i = 0; i < size; i++) {
            if (items[i] == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if size==0.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    private void rangeCheck(int index, int maxExclusive) throws IllegalArgumentException {
        if (index < 0 || index >= maxExclusive) {
            throw new IndexOutOfBoundsException("Index out of bounds " + index);
        }
    }

    /**
     * Removes the item at the specified index from this list.
     *
     * @param index an index
     * @return the removed item
     */
    public int removeAt(int index) {
        rangeCheck(index, size);
        int removedItem = items[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(items, index + 1, items, index, numMoved);
        }
        --size;
        return removedItem;
    }

    /**
     * Removes the last item
     *
     * @return the removed item
     * @throws NoSuchElementException if the list is empty
     */
    public int removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty.");
        }
        return removeAt(size - 1);
    }

    /**
     * Replaces the item at the specified index.
     *
     * @param index an index
     * @param newItem the new item
     * @return the old item
     */
    public int set(int index, int newItem) {
        rangeCheck(index, size);
        int removedItem = items[index];
        items[index] = newItem;
        return removedItem;
    }

    /**
     * Returns the size of the list.
     *
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Returns a stream for processing the items of this list.
     *
     * @return a stream
     */
    @Nonnull
    public IntStream stream() {
        return (size == 0) ? IntStream.empty() : Arrays.stream(items, 0, size);
    }

    /**
     * Returns a new array containing all of the elements in this collection.
     *
     * @return array
     */
    @Nonnull
    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(items, 0, result, 0, size);
        return result;
    }

    @Nonnull
    @Override
    public String toString() {
        return Arrays.toString(items);
    }

    /**
     * Creates a new instance with the specified items.
     *
     * @param items the items (the newly created instance references the
     * provided array)
     * @return the new instance
     */
    public static IntArrayList of(@Nonnull int... items) {
        return new IntArrayList(items);
    }
    
    /**
     * Sorts the items in ascending order.
     */
    public void sort() {
        Arrays.sort(items,0,size);
    }

}
