package ru.practicum.kanban.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest extends BaseTest {

    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Тестовый эпик", "Эпик для проверки класса Epic");
    }


    @Test
    public void epicCreationCorrect() {
        assertEquals(epic.getType(), Type.EPIC, "Тип эпика некорректен");
    }

    @Test
    public void epicsAreEqualIfIdsAreEqual() {
        manager.create(epic);
        Epic sameEpic = new Epic(epic);

        assertEquals(epic, sameEpic, "объекты не равны");
    }

}