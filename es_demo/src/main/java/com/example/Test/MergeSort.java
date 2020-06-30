package com.example.Test;

import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

/**
 * 归并排序
 */
public class MergeSort {

    public int[] mergeSort(int[] nums){

        int len = nums.length;
        int mid = len/2 ;

        while (len <= 1){
            return nums;
        }

        int[] leftNums = Arrays.copyOfRange(nums, 0, mid);
        int[] rightNums = Arrays.copyOfRange(nums, mid, len);

        leftNums = mergeSort(leftNums);
        rightNums = mergeSort(rightNums);

        nums = mergeArray(leftNums, rightNums);

        return nums;
    }

    /**
     * 双指针合并数组
     * @param nums1
     * @param nums2
     * @return
     */
    public int[] mergeArray(int[] nums1, int[] nums2){
        int len = nums1.length + nums2.length;
        int[] arr = new int[len];

        int i = nums1.length -1;
        int j = nums2.length -1;
        int k = arr.length -1;

        while (i >=0 && j>=0){
            if (nums1[i] <= nums2[j]){
                arr[k] = nums2[j];
                k--;
                j--;
            }else if (nums1[i] > nums2[j]){
                arr[k] = nums1[i];
                k--;
                i--;
            }
        }

        if (j>=0)
            System.arraycopy(nums2, 0, arr, 0, j+1);
        if (i>=0)
            System.arraycopy(nums1, 0, arr, 0, i+1);

        return arr;
    }


    public static void main(String[] args) {

        MergeSort mergeSort = new MergeSort();
        int[] arr = new int[]{4,2,1,3,8,6,7,9};
        System.out.println(Arrays.toString(mergeSort.mergeSort(arr)));

    }
}
