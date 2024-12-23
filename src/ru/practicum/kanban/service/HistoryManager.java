package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    int HISTORY_BUFFER_SIZE = 10;


    void add(Task task);

    List<Task> getHistory();
}
