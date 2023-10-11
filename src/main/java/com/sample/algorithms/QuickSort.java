package com.sample.algorithms;

public class QuickSort {


/*    public static void main(String[] args) {
        int[] arr = {10, 80, 30, 90, 40, 50, 70};
        QuickSort quick = new QuickSort();
        quick.sort(arr, 0, arr.length - 1);
        quick.printArray(arr);

        System.out.println("\n");

        int[] x = {6, 2, 7, 3, 5, 9, 1};
        quick.partitionWithMiddleIndex(x, 0, x.length - 1);
        quick.printArray(x);
    }*/

    /**
     * Print result
     *
     * @param arr array integer
     */
    void printArray(int arr[]) {
        int n = arr.length;
        for (int j : arr) System.out.print(j + " ");
        System.out.println();
    }

    /**
     * Quick sort
     *
     * @param low  first index
     * @param high last index
     */
    void sort(int[] arr, int low, int high) {
        if (low < high) {
            // Get position of pivot
            int pivot = partitionWithLastIndex(arr, low, high);
            sort(arr, low, pivot - 1);
            sort(arr, pivot + 1, high);
        }
    }

    /**
     * Set pivot is last element
     *
     * @param arr  array int
     * @param low  first index of array
     * @param high last index of array
     * @return
     */
    int partitionWithLastIndex(int[] arr, int low, int high) {
        // 1. select pivot is last element
        int pivot = arr[high];

        // 2. compare elements with pivot
        int j = low - 1;
        int temp;
        for (int i = low; i < high; i++) {

            // 2.1 if current element less than pivot then reverse position for them
            if (arr[i] < pivot) {
                j++;
                temp = arr[j]; // set next element index
                arr[j] = arr[i]; // next index = current index
                arr[i] = temp; // current index = next index
            }
        }

        // 2.2 reverse arr[i+1] with pivot (last index)
        temp = arr[j + 1];
        arr[j + 1] = arr[high];
        arr[high] = temp;

        return j + 1;
    }

    /**
     * Set pivot is middle element
     *
     * @param arr
     * @param low
     * @param high
     */
    void partitionWithMiddleIndex(int[] arr, int low, int high) {
        // 1. Select pivot
        int middle = low + (high - low) / 2;
        int pivot = arr[middle];
        int i = low;
        int j = high;

        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }
            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }

            if (low < j) {
                partitionWithMiddleIndex(arr, low, j);
            }
            if (high > i) {
                partitionWithMiddleIndex(arr, i, high);
            }
        }
    }
}
