package com.github.vkremianskii.pits.core.util;

import com.github.vkremianskii.pits.core.Tuple2;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TupleUtils {

    private TupleUtils() {
    }

    public static <K, V> Map<K, V> mapFromTuples(Collection<Tuple2<K, V>> tuples) {
        return tuples.stream().collect(toMap(Tuple2::first, Tuple2::second));
    }
}
