package org.one.algos;

import org.one.algos.sort.Sort;
import org.one.algos.sort.impl.InsertionSort;

public class App {

    public static void main(String[] args) {
        final Sort sorter = new InsertionSort();
        final Integer[] i = {55, 3, 96, 4, 44, 2, 65, 29, 23};
        for (final Integer j : i) {
            System.out.print(j + " ");
        }
        System.out.println();
        sorter.sort(i);
        for (final Integer j : i) {
            System.out.print(j + " ");
        }
    }
}
