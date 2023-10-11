package com.sample.algorithms;

public class BinarySearch {

/*    public static void main(String[] args) {
//        int[] arr = {2, 4, 9, 10, 11, 22, 24, 31, 48, 56, 76, 86};
//        int x = searchX(arr, 0, arr.length - 1, 10);
//        System.out.printf("x=%d\n", x);
//
//        int y = searchY(arr, 24);
//        System.out.printf("y=%d", y);
//
//
//        int z = searchZ(50);
//        System.out.println(z);

        String email = "test@gmail.com";

        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (email.matches(regex)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }*/

    /**
     * Binary search element in array
     *
     * @param arr
     * @param left
     * @param right
     * @param x
     * @return
     */
    private static int searchX(int[] arr, int left, int right, int x) {
        if (right >= left) {
            // Get middle element
            int middle = left + (right - left) / 2;

            // if searchValue equal middle element then return index of middle element
            if (x == arr[middle]) {
                return middle;
            }

            // if middle element greater than x then continue with left elements
            if (x > arr[middle]) {
                return searchX(arr, middle + 1, right, x);
            }

            // if middle element less than x then continue with left elements
            return searchX(arr, left, middle - 1, x);
        }

        return -1;
    }

    /**
     * Binary search element in array
     *
     * @param arr
     * @param y
     * @return
     */
    private static int searchY(int[] arr, int y) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            if (y == arr[middle])
                return middle;

            if (y > arr[middle]) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return -1;
    }

    /**
     * Binary Search associate Quick Sort in order to search element in array
     *
     * @param z   search element
     * @return integer
     */
    private static int searchZ(int z) {
        int[] randomArr = {10, 80, 30, 90, 40, 50, 70};

        // 1. sort
        sortArr(randomArr, 0, randomArr.length - 1);

        // 2. binary search z
        int left = 0, right = randomArr.length - 1;
        while (left <= right) {
            int middle = left + (right - left) / 2;

            if (randomArr[middle] == z) return middle;

            if (randomArr[left] < middle) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return -1;
    }

    /**
     * Quick sort
     *
     * @param arr
     * @param left
     * @param right
     */
    private static void sortArr(int[] arr, int left, int right) {
        if (arr == null || arr.length == 0) return;

        if (left >= right) return;

        int middle = left + (right - left) / 2;
        int pivot = arr[middle];
        int i = left, j = right;

        while (i <= j) {
            if (arr[i] < pivot) i++;

            if (arr[j] > pivot) j--;

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }

            if (left < j) sortArr(arr, left, j);

            if (right > i) sortArr(arr, i, right);
        }
    }
}
