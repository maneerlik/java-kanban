package ru.practicum.kanban;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.HistoryManager;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

/**
 * Класс {@code BaseTest} реализует базовый класс для тестов.
 *
 * @author  Smirnov Sergey
 */
public abstract class BaseTest {

    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected TaskManager manager = Managers.getDefault();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void setUp() {
        task = new Task("Тестовая задача", "Задача в InMemoryTaskManagerTest");
        manager.create(task);
        epic = new Epic("Тестовый эпик", "Эпик в InMemoryTaskManagerTest");
        manager.create(epic);
        subtask = new Subtask("Тестовая подзадача", "Подзадача в InMemoryTaskManagerTest", epic.getId());
        manager.create(subtask);
    }

}
