package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BubbleSortTest {

    @Test
    void testNullArray() {
        assertDoesNotThrow(() -> BubbleSort.sort(null));
    }

    @Test
    void testEmptyArray() {
        int[] arr = new int[]{};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testSingleElement() {
        int[] arr = new int[]{42};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void testAlreadySorted() {
        int[] arr = new int[]{1, 2, 3, 4, 5};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testReverseSorted() {
        int[] arr = new int[]{5, 4, 3, 2, 1};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testWithDuplicates() {
        int[] arr = new int[]{3, 1, 2, 3, 1};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 3}, arr);
    }

    @Test
    void testWithNegativeNumbers() {
        int[] arr = new int[]{0, -1, 5, -3, 2};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{-3, -1, 0, 2, 5}, arr);
    }
}
