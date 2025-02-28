package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends BaseTest {

    @Test
    void shouldCreatedTask() {
        assertNotNull(task);
        assertEquals("Задача", task.getTitle());
        assertEquals("Тестовая задача", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertNotNull(task.getId());
    }

    @Test
    void shouldCreatedEpic() {
        assertNotNull(epic);
        assertEquals("Эпик", epic.getTitle());
        assertEquals("Тестовый эпик", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
        assertNotNull(epic.getId());
    }

    @Test
    void shouldCreatedSubtask() {
        assertNotNull(subtask);
        assertEquals("Подзадача", subtask.getTitle());
        assertEquals("Тестовая подзадача", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
        assertNotNull(subtask.getId());
    }

    @Test
    void shouldReturnAllTasks() {
        Task anotherTask = new Task(task);
        manager.create(anotherTask);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void shouldReturnAllEpics() {
        Epic anotherEpic = new Epic(epic);
        manager.create(anotherEpic);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size());
    }

    @Test
    void shouldReturnAllSubtasks() {
        Subtask anotherSubtask = new Subtask(subtask);
        manager.create(anotherSubtask);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    void shouldUpdateTask() {
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
    void shouldUpdateEpic() {
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
    void shouldUpdateSubtask() {
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
    void deleteTask() {
        Task deletedTask = manager.deleteTask(task.getId());

        assertNotNull(deletedTask);
        assertEquals(task.getId(), deletedTask.getId());
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void deleteEpic() {
        Epic deletedEpic = manager.deleteEpic(epic.getId());

        assertNotNull(deletedEpic);
        assertEquals(epic.getId(), deletedEpic.getId());
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void deleteSubtask() {
        Subtask deletedSubtask = manager.deleteSubtask(subtask.getId());

        assertNotNull(deletedSubtask);
        assertEquals(subtask.getId(), deletedSubtask.getId());
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void shouldReturnSubtasksByEpic() {
        Subtask anotherSubtask = new Subtask(
                "Еще тестовая подзадача", "Еще подзадача в InMemoryTaskManagerTest", epic.getId());
        manager.create(anotherSubtask);

        List<Subtask> subtasks = manager.getSubtasksByEpic(epic.getId());
        assertEquals(2, subtasks.size());
    }

    @Test
    void shouldEvaluatedEpicStatus() {
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
        manager.deleteSubtask(subtask.getId());
        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldReturnHistory() {
        manager.getTask(task.getId());
        manager.getEpic(epic.getId());
        manager.getSubtask(subtask.getId());

        task.setStatus(Status.IN_PROGRESS);
        manager.deleteSubtask(subtask.getId());
        manager.updateTask(task);
        manager.updateEpic(epic);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void shouldReturnOneTasksHistory() {
        for (int i = 0; i < 10; i++) {
            manager.getTask(task.getId());
        }

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldReturnTenTasksHistory() {
        for (int i = 0; i < 10; i++) {
            Task oneMoreTask = new Task(task);
            manager.create(oneMoreTask);
            manager.getTask(oneMoreTask.getId());
        }

        assertEquals(10, manager.getHistory().size());
    }

    @Test
    void historyShouldBeCleanedUpAfterAllTasksDeletion() {
        for (int i = 0; i < 10; i++) {
            Task oneMoreTask = new Task(task);
            manager.create(oneMoreTask);
            manager.getTask(oneMoreTask.getId());
        }

        for (int i = 0; i < 10; i++) {
            Epic oneMoreEpic = new Epic(epic);
            manager.create(epic);
            manager.getEpic(epic.getId());
        }

        manager.clearTasks();

        assertEquals(10, manager.getHistory().size());
    }

    @Test
    void shouldReturnSortedPriorityTasks () {
        Task betweenTask = new Task(
                0, "Промежуточная задача", Status.NEW,
                "Задача между двумя существующими в списке приоритетов",
                task.getEndTime().plus(1, ChronoUnit.MINUTES), Duration.ofMinutes(1));

        manager.create(betweenTask);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals("Задача", prioritizedTasks.getFirst().getTitle());
        assertEquals("Промежуточная задача", prioritizedTasks.get(1).getTitle());
        assertEquals("Подзадача", prioritizedTasks.get(2).getTitle());
    }

    @Test
    void shouldThrowExceptionForOverlappingTaskTimes() {
        Task overlappingTask = new Task(0, "Пересекающаяся задача", Status.NEW,
                "Задача которая пересекается с существующей в списке приоритетов",
                task.getStartTime().plus(1, ChronoUnit.MINUTES), Duration.ofMinutes(1));

        assertThrows(ManagerCreateTaskException.class, () -> manager.create(overlappingTask));
    }

}