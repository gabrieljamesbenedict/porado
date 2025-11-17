package com.gabrieljamesbenedict.porado.util;

import com.gabrieljamesbenedict.porado.token.Token;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PeekableIterator<T> implements Iterator<T> {

    private final Collection<T> collection;

    // Constructor
    public PeekableIterator(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public boolean hasNext() {
        return !collection.isEmpty();
    }

    @Override
    public T next() {
        T t = collection.stream().limit(1).findFirst().orElse(null);
        collection.remove(t);
        return t;
    }

    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer action) {
        Iterator.super.forEachRemaining(action);
    }

    public T peek() {
        return collection.stream().limit(1).findFirst().orElse(null);
    }

    public T ahead(int n) {
        return n == 0? peek()
        : collection.stream().skip(n).limit(1).findFirst().orElse(null);
    }
}
