package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
