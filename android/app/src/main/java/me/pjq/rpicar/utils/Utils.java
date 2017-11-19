package me.pjq.rpicar.utils;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 19/11/2017.
 */

public class Utils {
    public static int[] Getrandomarray(int i, int i1) {
        int size = 11;
        int[] value = new int[size];
        for (int j = 0; j < size; j++) {
            value[j] = j;
        }

        return value;
    }
}
