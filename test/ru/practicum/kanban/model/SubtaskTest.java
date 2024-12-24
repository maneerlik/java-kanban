package ru.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest extends BaseTest {

    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Тестовый эпик", "Эпик для проверки класса Subtask");
        manager.create(epic);

        subtask = new Subtask("Тестовая подзадача", "Подзадача для проверки класса Subtask", epic.getId());
    }


    @Test
    public void subtaskCreationCorrect() {
        assertEquals(subtask.getEpicId(), epic.getId(), "epicId некорректен");
        assertEquals(subtask.getType(), Type.SUBTASK, "Тип подзадачи некорректен");
    }

    @Test
    public void subtasksAreEqualIfIdsAreEqual() {
        manager.create(subtask);
        Subtask sameSubtask = new Subtask(subtask);

        assertEquals(subtask, sameSubtask, "объекты не равны");
    }

}