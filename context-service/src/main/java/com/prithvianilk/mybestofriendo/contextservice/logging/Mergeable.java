package com.prithvianilk.mybestofriendo.contextservice.logging;

/**
 * Interface for objects that can be merged with other objects of the same type.
 * Used by WideEventContext to support merging values when enriching with the
 * same key.
 *
 * @param <T> The type of object this can merge with
 */
public interface Mergeable<T extends Mergeable<T>> {

    /**
     * Merges this object with another object of the same type.
     *
     * @param other The other object to merge with
     * @return A new merged object containing data from both this and other
     */
    T merge(T other);
}
