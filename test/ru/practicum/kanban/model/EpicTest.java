package ru.practicum.kanban.model;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest extends BaseTest {

    @Test
    void epicCreationCorrect() {
        assertEquals(Type.EPIC, epic.getType(), "Тип эпика некорректен");
    }

    @Test
    void epicsAreEqualIfIdsAreEqual() {
        manager.create(epic);
        Epic sameEpic = new Epic(epic);

        assertEquals(epic, sameEpic, "объекты не равны");
    }

}