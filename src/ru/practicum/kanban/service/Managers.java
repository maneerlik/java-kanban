package ru.practicum.kanban.service;

public class Managers {
    /**
     * Don't let anyone instantiate this class.
     */
    private Managers() {}


    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
