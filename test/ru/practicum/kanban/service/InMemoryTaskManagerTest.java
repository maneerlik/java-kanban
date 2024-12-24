package ru.practicum.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.kanban.service.HistoryManager.HISTORY_BUFFER_SIZE;

class InMemoryTaskManagerTest extends BaseTest {

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        task = new Task("Тестовая задача", "Задача в InMemoryTaskManagerTest");
        manager.create(task);
        epic = new Epic("Тестовый эпик", "Эпик в InMemoryTaskManagerTest");
        manager.create(epic);
        subtask = new Subtask("Тестовая подзадача", "Подзадача в InMemoryTaskManagerTest", epic.getId());
        manager.create(subtask);
    }


    @Test
    public void shouldCreatedTask() {
        assertNotNull(task);
        assertEquals(task.getTitle(), "Тестовая задача");
        assertEquals(task.getDescription(), "Задача в InMemoryTaskManagerTest");
        assertEquals(task.getStatus(), Status.NEW);
        assertNotNull(task.getId());
    }

    @Test
    public void shouldCreatedEpic() {
        assertNotNull(epic);
        assertEquals(epic.getTitle(), "Тестовый эпик");
        assertEquals(epic.getDescription(), "Эпик в InMemoryTaskManagerTest");
        assertEquals(epic.getStatus(), Status.NEW);
        assertNotNull(epic.getId());
    }

    @Test
    public void shouldCreatedSubtask() {
        assertNotNull(subtask);
        assertEquals(subtask.getTitle(), "Тестовая подзадача");
        assertEquals(subtask.getDescription(), "Подзадача в InMemoryTaskManagerTest");
        assertEquals(subtask.getStatus(), Status.NEW);
        assertNotNull(subtask.getId());
    }

    @Test
    public void shouldReturnAllTasks() {
        Task sameTask = new Task(task);
        manager.create(sameTask);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    public void shouldReturnAllEpics() {
        Epic sameEpic = new Epic(epic);
        manager.create(sameEpic);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size());
    }

    @Test
    public void shouldReturnAllSubtasks() {
        Subtask sameSubtask = new Subtask(subtask);
        manager.create(sameSubtask);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    public void shouldUpdateTask() {
        Task updatedTask = new Task("Обновленная задача", "Обновленное описание задачи");
        updatedTask.setStatus(Status.IN_PROGRESS);
        updatedTask.setId(task.getId());

        manager.updateTask(updatedTask);

        assertNotNull(updatedTask);
        assertEquals("Обновленная задача", manager.getTask(task.getId()).getTitle());
        assertEquals("Обновленное описание задачи", manager.getTask(task.getId()).getDescription());
        assertEquals(Status.IN_PROGRESS, manager.getTask(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpic() {
        Epic updatedEpic = new Epic("Обновленный эпик", "Обновленное описание эпика");
        updatedEpic.getSubtasksIds().add(subtask.getId());
        updatedEpic.setId(epic.getId());
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);

        manager.updateEpic(updatedEpic);

        assertNotNull(updatedEpic);
        assertEquals("Обновленный эпик", manager.getEpic(epic.getId()).getTitle());
        assertEquals("Обновленное описание эпика", manager.getEpic(epic.getId()).getDescription());
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtask() {
        Subtask updatedSubtask =
                new Subtask("Обновленная подзадача", "Обновленное описание подзадачи", epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        updatedSubtask.setId(subtask.getId());

        manager.updateSubtask(updatedSubtask);

        assertNotNull(updatedSubtask);
        assertEquals("Обновленная подзадача", manager.getSubtask(subtask.getId()).getTitle());
        assertEquals("Обновленное описание подзадачи", manager.getSubtask(subtask.getId()).getDescription());
        assertEquals(Status.DONE, manager.getSubtask(subtask.getId()).getStatus());
    }

    @Test
    public void deleteTask() {
        Task deletedTask = manager.deleteTask(task.getId());

        assertNotNull(deletedTask);
        assertEquals(task.getId(), deletedTask.getId());
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void deleteEpic() {
        Epic deletedEpic = manager.deleteEpic(epic.getId());

        assertNotNull(deletedEpic);
        assertEquals(epic.getId(), deletedEpic.getId());
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void deleteSubtask() {
        Subtask deletedSubtask = manager.deleteSubtask(subtask.getId());

        assertNotNull(deletedSubtask);
        assertEquals(subtask.getId(), deletedSubtask.getId());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    public void shouldReturnSubtasksByEpic() {
        Subtask sameSubtask = new Subtask(
                "Еще тестовая подзадача", "Еще подзадача в InMemoryTaskManagerTest", epic.getId());
        manager.create(sameSubtask);

        List<Subtask> subtasks = manager.getSubtasksByEpic(epic.getId());
        assertEquals(2, subtasks.size());
    }

    @Test
    public void shouldEvaluatedEpicStatus() {
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
        manager.deleteSubtask(subtask.getId());
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void shouldReturnHistory() {
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        task.setStatus(Status.IN_PROGRESS);
        manager.deleteSubtask(subtask.getId());
        manager.updateTask(task);
        manager.updateEpic(epic);

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    public void shouldReturnHistoryOnlyLast10Tasks() {
        for (int i = 0; i < HISTORY_BUFFER_SIZE + 1; i++) {
            manager.getTask(task.getId());
        }

        assertEquals(HISTORY_BUFFER_SIZE, manager.getHistory().size());
    }

}