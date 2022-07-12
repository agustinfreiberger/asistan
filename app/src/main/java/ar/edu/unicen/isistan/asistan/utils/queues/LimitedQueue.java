package ar.edu.unicen.isistan.asistan.utils.queues;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class LimitedQueue<E> implements Iterable<E> {

    protected int maxSize;
    protected ArrayList<E> list;

    public LimitedQueue(int maxSize) {
        this.maxSize = maxSize;
        this.list = new ArrayList<>(maxSize+1);
    }

    public E get(int index) {
        return this.list.get(index);
    }

    public boolean add(@NotNull E element) {
        this.list.add(element);
        while (this.list.size() > this.maxSize)
            this.list.remove(0);
        return true;
    }

    public boolean remove(@NotNull E element) {
        return this.list.remove(element);
    }

    public void clear() {
        this.list.clear();
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public E getFirst() {
        return this.list.get(0);
    }

    public E getLast() {
        return this.list.get(this.list.size()-1);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addAll(LimitedQueue<E> queue) {
        for (E element: queue)
            this.add(element);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

}