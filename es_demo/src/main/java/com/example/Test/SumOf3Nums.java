package com.example.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * 三数之和（双指针算法）
 * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组
 *
 */
public class SumOf3Nums {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> list = new ArrayList();
        Arrays.sort(nums);
        for(int i=0; i< nums.length; i++){
            if (i >0 && nums[i] == nums[i-1]) {
                continue;
            }
            int j = i + 1;
            int k = nums.length - 1;
            int sub = 0 - nums[i];
            while (j < k){
                if ((nums[j] + nums[k]) < sub) {
                    j++;
                }else if ((nums[j] + nums[k]) > sub){
                    k--;
                }else if ((nums[j] + nums[k]) == sub){
                    ArrayList<Integer> arr = new ArrayList();
                    arr.add(nums[i]);
                    arr.add(nums[j]);
                    arr.add(nums[k]);
                    list.add(arr);
                    //去重
                    while (j < k && nums[j+1] == nums[j])
                        j++;
                    while (j < k && nums[k-1] == nums[k])
                        k--;
                    j++;
                    k--;
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {

        SumOf3Nums sum = new SumOf3Nums();

        int[] nums = new int[]{-1, 0, 1, 2, -1, -4};
        List<List<Integer>> list = sum.threeSum(nums);
        System.out.println(list);
    }
}
