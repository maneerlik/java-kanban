package ru.practicum.kanban.service;

import java.nio.file.Paths;

public class Managers {
    private Managers() {
        // Don't let anyone instantiate this class.
    }


    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static FileBackedTaskManager getFileBackedManager() {
        return new FileBackedTaskManager(getDefaultHistory(), Paths.get(".\\resources\\backup.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
