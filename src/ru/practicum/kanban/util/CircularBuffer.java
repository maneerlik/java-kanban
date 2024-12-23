package ru.practicum.kanban.util;

import java.util.LinkedList;

/**
 * Класс {@code CircularBuffer} является специфической реализацией {@code LinkedList}
 * для обеспечения фиксированного кугового буфера.
 *
 * @author  Smirnov Sergey
 */
public class CircularBuffer <T> extends LinkedList<T> {
    private final int maxSize;

    public CircularBuffer(int maxSize) {
        this.maxSize = maxSize;
    }


    @Override
    public boolean add(T element) {
        if (size() >= maxSize) {
            removeFirst();
        }
        return super.add(element);
    }
}
