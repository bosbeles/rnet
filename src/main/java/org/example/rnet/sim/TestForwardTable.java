package org.example.rnet.sim;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestForwardTable {


    @Getter
    private Map<Integer, List<Integer>> leftToRightMap = new HashMap<>();

    @Getter
    private Map<Integer, List<Integer>> rightToLeftMap = new HashMap<>();


    public TestForwardTable(Integer left, Integer right, int leftCount, int rightCount, int maxRightToLeftRegistration) {
        int leftPort = left;
        int rightPort = right;
        for (int i = 1; i <= rightCount; i++) {
            Integer rightIndex = rightPort + i;
            List<Integer> list = new ArrayList<>();
            rightToLeftMap.put(rightIndex, list);

            for (int j = 0; j < maxRightToLeftRegistration; j++) {
                int port = leftPort + (leftCount / rightCount * (i - 1) + j) % leftCount + 1;
                Integer leftIndex = port;
                list.add(leftIndex);
            }
        }
        leftToRightMap = reverse(rightToLeftMap);
    }

    private static <K> Map<K, List<K>> reverse(Map<K, List<K>> original) {
        Map<K, List<K>> reverse = new HashMap<>();
        for (Map.Entry<K, List<K>> entry : original.entrySet()) {
            K key = entry.getKey();
            List<K> value = entry.getValue();
            for (K k : value) {
                List<K> ks = reverse.get(k);
                if (ks == null) {
                    ks = new ArrayList<>();
                    reverse.put(k, ks);
                }
                ks.add(key);
            }
        }
        return reverse;
    }

    public static void main(String[] args) {
        TestForwardTable table = new TestForwardTable(
                5000, 6000,
                100, 12, 32);
        System.out.println(table.getLeftToRightMap());
        System.out.println(table.getRightToLeftMap());

    }


}
