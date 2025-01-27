package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;
import ru.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest extends BaseTest {

    @Test
    void addTaskWithNullDoesNotAddToHistory() {
        historyManager.add(null);

        assertEquals(0, historyManager.getHistory().size(), "История не пуста");
    }

    @Test
    void historyShouldBeEmptyAfterRemovingAllAddedTasks() {
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        manager.deleteTask(task.getId());
        manager.deleteEpic(epic.getId());

        List<Task> history = manager.getHistory();
        assertEquals(0, history.size(), "История не пуста");
    }

    @Test
    void historyShouldContainOnlyLastInstanceWhenAddingDuplicate() {
        int id = task.getId();

        for (int i = 0; i < 3; i++) {
            task = new Task("Тестовая задача", "Тестовая задача №" + i);
            task.setId(id);
            manager.updateTask(task); // задача обновлена
            manager.getTask(id); // добавить обновленную задачу в историю
        }

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size(), "История содержит дубликаты");
        assertEquals("Тестовая задача №2", history.getFirst().getDescription(),
                "История содержит не актуальную задачу");
    }

}