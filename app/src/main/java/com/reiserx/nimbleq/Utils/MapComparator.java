package com.reiserx.nimbleq.Utils;

import java.util.Comparator;
import java.util.Map;

public class MapComparator implements Comparator<Map<String, String>>
{
    private final String key;
    String firstValue;
    String secondValue;

    public MapComparator(String key)
    {
        this.key = key;
    }

    public int compare(Map<String, String> first, Map<String, String> second)
    {
        if (first != null && second != null) {
            firstValue = first.get(key);
            secondValue = second.get(key);
            return firstValue.compareTo(secondValue);
        } else
            return 0;
    }
}