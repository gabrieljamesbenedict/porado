package com.gabrieljamesbenedict.porado.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class PeekableIterator<T> implements Iterator<T> {

    private final Collection<T> collection;

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
        return n == 1? peek()
        : collection.stream().skip(n).limit(1).findFirst().orElse(null);
    }
}
