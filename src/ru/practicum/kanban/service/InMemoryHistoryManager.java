package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.util.CircularBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code InMemoryHistoryManager} реализует интерфейс {@code HistoryManager} и
 * обеспечивает хранение истории просмотренных задач.
 *
 * @author  Smirnov Sergey
 */
public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_BUFFER_SIZE = 10;

    private CircularBuffer<Task> history = new CircularBuffer<>(HISTORY_BUFFER_SIZE);

    //--- Пометить задачу как просмотренную ----------------------------------------------------------------------------
    @Override
    public void add(Task task) {
        if (task != null) history.add(task);
    }

    //--- Просмотр истории (последние 10 просмотренных задач) ----------------------------------------------------------
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
