package ru.practicum.kanban.service;

import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseTest;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends BaseTest {

    @Test
    void testFileBackedTaskManagerCreationFromEmptyFile() {
        assertNotNull(fbManager);
        assertInstanceOf(FileBackedTaskManager.class, fbManager);
    }

    @Test
    void testFileBackedTaskManagerSavesEmptyState() {
        fbManager.create(task);
        fbManager.deleteTask(task.getId());
        List<String> lines;

        try {
            lines = Files.readAllLines(backup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals("id,type,name,status,description,epic", lines.getFirst(), "Неверный заголовок файла");
        assertEquals(1, lines.size(), "Файл бэкапа не пуст");
    }

    @Test
    void testFileBackedTaskManagerSavesCorrectState() {
        Task sameTask = new Task(task);
        fbManager.create(sameTask);
        Epic sameEpic = new Epic(epic);
        fbManager.create(sameEpic);
        Subtask sameSubtask = new Subtask(subtask.getTitle(), subtask.getDescription(), sameEpic.getId());
        fbManager.create(sameSubtask);
        List<String> lines;

        try {
            lines = Files.readAllLines(backup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals("id,type,name,status,description,epic", lines.getFirst(), "Неверный заголовок файла");
        assertEquals(getExpectedResult(sameTask), lines.get(1), "Неверное сохранение Task");
        assertEquals(getExpectedResult(sameEpic), lines.get(2), "Неверное сохранение Epic");
        assertEquals(getExpectedResult(sameSubtask), lines.get(3), "Неверное сохранение Subtask");
    }

    @Test
    void testIdCounterCorrectnessAfterFileLoad() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backup.toFile(), StandardCharsets.UTF_8))) {
            writer.write("""
                    id,type,name,status,description,epic
                    17,TASK,Тестовая задача,NEW,Задача в InMemoryTaskManagerTest,
                    3,EPIC,Тестовый эпик,NEW,Эпик в InMemoryTaskManagerTest,
                    11,SUBTASK,Тестовая подзадача,NEW,Подзадача в InMemoryTaskManagerTest,3
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileBackedTaskManager sameFBManager = FileBackedTaskManager.loadFromFile(backup);
        Task sameTask = new Task(task);
        sameFBManager.create(sameTask);

        assertEquals(18, sameTask.getId(), "Генератор id инициализирован неверно");
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
    private String getExpectedResult(Task task) {
        String taskString = String.format("%d,%s,%s,%s,%s,",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription()
        );

        if (task instanceof Subtask)
            return taskString + ((Subtask) task).getEpicId();

        return taskString;
    }

}
