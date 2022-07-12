package ar.edu.unicen.isistan.asistan.utils.queues;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ReusableLimitedQueue<E extends Reusable<E>> extends LimitedQueue<E> {

    protected LinkedList<E> aux;

    public ReusableLimitedQueue(int maxSize) {
        super(maxSize);
        this.aux = new LinkedList<>();
    }

    @Override
    public boolean remove(@NotNull E element) {
        int indexOf = this.list.indexOf(element);
        if (indexOf != -1) {
            E removed = this.list.remove(indexOf);
            removed.init();
            this.aux.add(removed);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(@NotNull E element) {
        E toAdd;
        if (this.aux.isEmpty()) {
            toAdd = element.copy();
        } else {
            toAdd = this.aux.removeFirst();
            toAdd.init(element);
        }

        this.list.add(toAdd);
        while (this.list.size() > this.maxSize) {
            E toAux = this.list.remove(0);
            toAux.init();
            this.aux.add(toAux);
        }

        return true;
    }

    @Override
    public void clear() {
        while (!this.list.isEmpty()) {
            E toAux = this.list.remove(0);
            toAux.init();
            this.aux.add(toAux);
        }
    }


}
