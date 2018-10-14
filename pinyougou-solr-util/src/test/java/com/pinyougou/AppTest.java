package com.pinyougou;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;

import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        //assertTrue(true);
        HashSet set = new LinkedHashSet();
    }

    /*int partition(int arr[], int l, int r) {
        int k = l, pivot = arr[r];
        for (int i = l; i < r; i++)
            if (arr[i] <= pivot)
                swap(arr[i], arr[k++]);
        swap(arr[k], arr[r]);
        return k;
    }

    void quicksort(int arr[], int l, int r) {
        if (l < r) {
            int pivot = partition(arr, l, r);
            quicksort(arr, l, pivot - 1);
            quicksort(arr, pivot + 1, r);
        }
    }*/

    @Test
    public void testAAA() {
        int[] arr = {1, 23, 56, 2, 7, 54, 0};
        quick_sort(arr, 0, 0);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    void quick_sort(int s[], int l, int r) {
        if (l < r) {
            int i = l;
            int j = r;
            int x = s[1];
            while (i < j) {
                while (i < j && s[j] >= x) {
                    j--;
                }
                if (i < j) {
                    s[i++] = s[j];
                }
                while (i < j && s[i] < x) {
                    i++;
                }
                if (i < j) {
                    s[j--] = s[i];
                }
            }
            s[i] = x;
            quick_sort(s, l, i - 1);
            quick_sort(s, i + 1, r);
        }
    }

    @Test
    public void dddd() {
        List<TbItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TbItem tbitem = new TbItem();
            tbitem.setTitle("a" + i);
            tbitem.setBrand("b" + i);
            list.add(tbitem);
        }
        for (TbItem tbItem : list) {
            //[{k1:v,k2:v,k3:v},{}]
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    public void cccccc() {
        String str = "中中中中国国国人";
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            String c = str.charAt(i) + "";
            if (map.containsKey(c)) {
                Integer value = map.get(c);
                map.put(c, value + 1);
            } else {
                map.put(c, 1);
            }
        }
        System.out.println(map);
    }

//TbItem{id=null, title='a0',brand='b0', specMap=null}

    @Test
    public void ddddd() {
        Set<String> set = new TreeSet<>();
        set.add("ddddasda");
        set.add("bbbdsadag");
        set.add("gggdsad");
        set.add("eeedsa");
        set.add("aaahj");
        set.add("fff");
        set.add("ccc");
        new Comparator<Set>() {
            @Override
            public int compare(Set o1, Set o2) {
                return 0;
            }

        };

        new Runnable() {
            @Override
            public void run() {
                System.out.println("dsada");
            }
        };

        for (String integer : set) {
            System.out.println(integer);
        }
    }

    @Test
    public void bubble() {
        int[] arr = {23, 1, 45, 16, 7, 10};
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for (int anInt : arr) {
            System.out.println(anInt);
        }
    }

    private static int[] bubbleTest(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

    @Test
    public void selectSort(){
        int[] arr = {123,2,56,122,5};
        selectSort(arr);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    private static int[] selectSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] > arr[i]) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }

    static class SingleTon{
        private SingleTon(){}
        private static SingleTon singleTon = new SingleTon();

        public static SingleTon getSingleTon() {
            return singleTon;
        }
    }

    static class lazySingleTon{
        private lazySingleTon(){}
        private static lazySingleTon singleTon2 = null;

        public static lazySingleTon getSingleTon2() {
            if(singleTon2 == null){
                singleTon2 = new lazySingleTon();
            }
            return singleTon2;
        }
    }
}
