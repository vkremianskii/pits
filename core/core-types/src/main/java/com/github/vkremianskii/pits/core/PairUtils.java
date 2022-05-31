package com.github.vkremianskii.pits.core;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class PairUtils {

    private PairUtils() {
    }

    public static <K, V> Map<K, V> pairsToMap(Collection<Pair<K, V>> pairs) {
        return pairs.stream().collect(toMap(Pair::left, Pair::right));
    }
}
