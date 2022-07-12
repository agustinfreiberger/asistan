package ar.edu.unicen.isistan.asistan.utils.queues;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Reusable<T> {

    void init();

    void init(@Nullable T element);

    boolean isEmpty();

    @NotNull
    T copy();

}
