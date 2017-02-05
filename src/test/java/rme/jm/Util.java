package rme.jm;

import java.util.ArrayList;
import java.util.Arrays;

public class Util {
    public static <T> ArrayList<T> list(T... objects) {
        final ArrayList<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(objects));
        return list;
    }
}
