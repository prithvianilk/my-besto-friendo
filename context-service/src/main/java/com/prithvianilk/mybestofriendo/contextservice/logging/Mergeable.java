package com.prithvianilk.mybestofriendo.contextservice.logging;

public interface Mergeable<T extends Mergeable<T>> {

    T merge(T other);
}
