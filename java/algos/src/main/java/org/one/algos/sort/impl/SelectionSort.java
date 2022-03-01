package org.one.algos.sort.impl;

import org.one.algos.sort.Sort;

public class SelectionSort implements Sort {

    @Override
    public <T extends Comparable<T>> void sort(T[] values) {
        for (int out = 0; out < values.length - 1; out++) {
            int min = out;
            for (int in = out + 1; in < values.length; in++) {
                if (values[min].compareTo(values[in]) > 0) {
                    min = in;
                }
            }
            swap(values, out, min);
        }
    }

}
