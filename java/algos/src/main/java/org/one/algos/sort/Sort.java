package org.one.algos.sort;

public interface Sort {
    <T extends Comparable<T>> void sort(T[] values);

    default <T> void swap(T[] elements, int first, int second) {
        final T temp = elements[first];
        elements[first] = elements[second];
        elements[second] = temp;
    }
}
