package org.one.algos.sort.impl;

import org.one.algos.sort.Sort;

public class InsertionSort implements Sort {

    @Override
    public <T extends Comparable<T>> void sort(T[] values) {
        for (int out = 1; out < values.length; out++) {
            final T temp = values[out];
            int in = out;
            while (in > 0 && values[in - 1].compareTo(temp) > 0) {
                values[in] = values[in - 1];
                --in;
            }
            values[in] = temp;
        }
    }
}
