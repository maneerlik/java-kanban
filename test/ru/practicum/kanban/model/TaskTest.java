package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskTest extends BaseTest {

    @Test
    void taskCreationCorrect() {
        Task testTask = new Task("Тестовая задача", "Задача для проверки класса Task");

        assertNull(testTask.getId(), "id задачи не null");
        assertEquals(Status.NEW, testTask.getStatus(), "Задача создана со статусом отличным от NEW");
        assertEquals("Тестовая задача", testTask.getTitle(), "Наименование задачи некорректно");
        assertEquals("Задача для проверки класса Task", testTask.getDescription(), "Описание задачи некорректно");
        assertEquals(Type.TASK, testTask.getType(), "Тип задачи некорректен");
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        manager.create(task);
        Task anotherTask = new Task(task);

        assertEquals(task, anotherTask, "объекты не равны");
    }

}