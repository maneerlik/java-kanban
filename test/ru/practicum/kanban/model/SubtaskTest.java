package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest extends BaseTest {

    @Test
    void subtaskCreationCorrect() {
        assertEquals(subtask.getEpicId(), epic.getId(), "epicId некорректен");
        assertEquals(Type.SUBTASK, subtask.getType(), "Тип подзадачи некорректен");
    }

    @Test
    void subtasksAreEqualIfIdsAreEqual() {
        manager.create(subtask);
        Subtask sameSubtask = new Subtask(subtask);

        assertEquals(subtask, sameSubtask, "объекты не равны");
    }

}