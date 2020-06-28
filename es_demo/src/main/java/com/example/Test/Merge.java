package com.example.Test;

import java.util.Arrays;

/**
 * 合并两个有序数组
 * 说明：给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组
 * 双指针法
 */
public class Merge {

    public void merge(int[] nums1, int m, int[] nums2, int n) {

         int i = m - 1;
         int j = n - 1;
         int k = m + n - 1;

         while (i >= 0 && j >= 0){
             if (nums1[i] >= nums2[j]){
                 nums1[k] = nums1[i];
                 k--;
                 i--;
             }else if (nums1[i] < nums2[j]){
                 nums1[k] = nums2[j];
                 k--;
                 j--;
             }
         }
         if (j>=0)
             System.arraycopy(nums2, 0,nums1,0, j+1);

    }

    public static void main(String[] args) {

        Merge merge = new Merge();

        int[] nums1 = new int[]{1,2,3,0,0,0};
        int[] nums2 = new int[]{2,5,6};

        merge.merge(nums1,3,nums2,3);
        System.out.println(Arrays.toString(nums1));
    }
}
