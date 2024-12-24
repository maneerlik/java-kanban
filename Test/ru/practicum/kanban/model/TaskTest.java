package ru.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends BaseTest {

    private Task task;

    @BeforeEach
    public void setUp() {
        task = new Task("Тестовая задача", "Задача для проверки класса Task");
    }


    @Test
    public void taskCreationCorrect() {
        assertNull(task.getId(), "id задачи не null");
        assertEquals(task.getStatus(), Status.NEW, "Задача создана со статусом отличным от NEW");
        assertEquals(task.getTitle(), "Тестовая задача", "Наименование задачи некорректно");
        assertEquals(task.getDescription(), "Задача для проверки класса Task", "Описание задачи некорректно");
        assertEquals(task.getType(), Type.TASK, "Тип задачи некорректен");
    }

    @Test
    public void tasksAreEqualIfIdsAreEqual() {
        manager.create(task);
        Task sameTask = new Task(task);

        assertEquals(task, sameTask, "объекты не равны");
    }

}