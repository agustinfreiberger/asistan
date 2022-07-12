package ar.edu.unicen.isistan.asistan.utils.queues;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ReusableQueue<E extends Reusable<E>> implements Iterable<E> {

    protected LinkedList<E> aux;
    protected ArrayList<E> list;

    public ReusableQueue() {
        this.list = new ArrayList<>();
        this.aux = new LinkedList<>();
    }

    public E get(int index) {
        return this.list.get(index);
    }

    public boolean add(@NotNull E element) {
        E toAdd;
        if (this.aux.isEmpty()) {
            toAdd = element.copy();
        } else {
            toAdd = this.aux.removeFirst();
            toAdd.init(element);
        }

        this.list.add(toAdd);
        return true;
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

    public void clear() {
        while (!this.list.isEmpty()) {
            E toAux = this.list.remove(0);
            toAux.init();
            this.aux.add(toAux);
        }
    }

    public void addAll(ReusableQueue<E> queue) {
        for (E element: queue)
            this.add(element);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

}
