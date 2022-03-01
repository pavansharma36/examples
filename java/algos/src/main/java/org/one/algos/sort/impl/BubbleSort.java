package org.one.algos.sort.impl;

import org.one.algos.sort.Sort;

public class BubbleSort implements Sort {

    @Override
    public <T extends Comparable<T>> void sort(T[] values) {
        for (int i = values.length - 1; i > 1; i--) {
            for (int j = 0; j < i; j++) {
                if (values[j].compareTo(values[j + 1]) > 0) {
                    swap(values, j, j + 1);
                }
            }
        }
    }
}
