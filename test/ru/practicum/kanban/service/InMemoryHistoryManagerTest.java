package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    public void addTaskWithNullDoesNotAddToHistory() {
        historyManager.add(null);

        assertEquals(0, historyManager.getHistory().size());
    }

}