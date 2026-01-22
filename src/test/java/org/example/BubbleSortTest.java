package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortTest {

    @Test
    void shouldSortArray() {
        int[] arr = {5, 1, 4, 2, 8};

        BubbleSort.sort(arr);

        assertArrayEquals(new int[]{1, 2, 4, 5, 8}, arr);
    }

    @Test
    void shouldHandleNullArray() {
        assertDoesNotThrow(() -> BubbleSort.sort(null));
    }

    @ParameterizedTest
    @MethodSource("arrayProvider")
    void shouldSortVariousArrays(int[] input, int[] expected) {
        BubbleSort.sort(input);
        assertArrayEquals(expected, input);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> arrayProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{}, new int[]{}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{1}, new int[]{1}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{3, 2, 1}, new int[]{1, 2, 3}
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new int[]{5, -1, 4}, new int[]{-1, 4, 5}
                )
        );
    }
}