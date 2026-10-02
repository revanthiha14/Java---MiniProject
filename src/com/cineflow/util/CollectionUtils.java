package com.cineflow.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic utility methods for Collections Framework operations.
 * Demonstrates:
 *  - Generic static methods (<T>, <K, V>)
 *  - Functional programming helpers
 *  - Collections conversions (Array to Collection, Collection to Array)
 */
public final class CollectionUtils {

    private CollectionUtils() {
        // Private constructor prevents instantiation
    }

    /**
     * Filters any generic collection based on a functional Predicate.
     */
    public static <T> List<T> filterList(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null) return new ArrayList<>();
        return collection.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Extracts a unique Set of keys or mapped attributes from a collection of entities.
     */
    public static <T, R> Set<R> extractUnique(Collection<T> collection, Function<T, R> extractor) {
        if (collection == null) return new HashSet<>();
        return collection.stream()
                .map(extractor)
                .filter(r -> r != null)
                .collect(Collectors.toSet());
    }

    /**
     * Groups a collection into a Map indexed by a key extractor function.
     */
    public static <K, V> Map<K, List<V>> groupByKey(Collection<V> collection, Function<V, K> keyExtractor) {
        return collection.stream()
                .collect(Collectors.groupingBy(keyExtractor));
    }

    /**
     * Converts a generic List to a strongly-typed Array demonstration.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list, Function<Integer, T[]> arraySupplier) {
        if (list == null) return arraySupplier.apply(0);
        return list.toArray(arraySupplier.apply(list.size()));
    }
}
