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
import java.util.ArrayList;
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

        assertEquals("id,type,name,status,description,startTime,duration,epic",
                lines.getFirst(), "Неверный заголовок файла");
        assertEquals(1, lines.size(), "Файл бэкапа не пуст");
    }

    @Test
    void testFileBackedTaskManagerSavesCorrectState() {
        Task anotherTask = new Task(task);
        fbManager.create(anotherTask);
        Epic anotherEpic = new Epic(epic);
        fbManager.create(anotherEpic);
        Subtask anotherSubtask = new Subtask(
                subtask.getId(),
                subtask.getTitle(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getStartTime(),
                subtask.getDuration(),
                anotherEpic.getId()
        );
        fbManager.create(anotherSubtask);
        fbManager.updateEpic(anotherEpic); // т.к. после создания связанной subtask время эпика изменилось
        List<String> lines;

        try {
            lines = Files.readAllLines(backup);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals("id,type,name,status,description,startTime,duration,epic",
                lines.getFirst(), "Неверный заголовок файла");
        assertEquals(getExpectedResult(anotherTask), lines.get(1), "Неверное сохранение Task");
        assertEquals(getExpectedResult(anotherEpic), lines.get(2), "Неверное сохранение Epic");
        assertEquals(getExpectedResult(anotherSubtask), lines.get(3), "Неверное сохранение Subtask");
    }

    @Test
    void testIdCounterCorrectnessAfterFileLoad() {
        writeBackup();
        FileBackedTaskManager anotherFBManager = FileBackedTaskManager.loadFromFile(backup);
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(anotherFBManager.getAllTasks());
        allTasks.addAll(anotherFBManager.getAllEpics());
        allTasks.addAll(anotherFBManager.getAllSubtasks());
        int expectedIdCounterValue = allTasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;

        Task anotherTask = new Task(task);
        anotherFBManager.create(anotherTask);

        assertEquals(expectedIdCounterValue, anotherTask.getId(), "Генератор id инициализирован неверно");
    }

    @Test
    void testEpicSubtasksIdsAreRestoredCorrectly() {
        writeBackup();
        FileBackedTaskManager anotherFBManager = FileBackedTaskManager.loadFromFile(backup);

        int expectedSubtaskId = anotherFBManager.getAllSubtasks().getFirst().getId();
        int actualSubtaskId = anotherFBManager.getAllEpics().getFirst().getSubtasksIds().getFirst();

        assertEquals(expectedSubtaskId, actualSubtaskId, "Список subtasksIds восстановлен некорректно");
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
    private String getExpectedResult(Task task) {
        String taskString = String.format("%d,%s,%s,%s,%s,%s,%s,",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime(),
                task.getDuration()
        );

        if (task instanceof Subtask)
            return taskString + ((Subtask) task).getEpicId();

        return taskString;
    }

    private void writeBackup() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backup.toFile(), StandardCharsets.UTF_8))) {
            writer.write("""
                    id,type,name,status,description,startTime,duration,epic
                    6,TASK,Задача,NEW,Тестовая задача,2025-02-25T05:17:53.291356200Z,PT1H30M,
                    12,EPIC,Эпик,DONE,Тестовый эпик,2025-02-25T05:17:53.293355100Z,PT30M,
                    17,SUBTASK,Подзадача,NEW,Тестовая подзадача,2025-02-25T06:47:53.293355100Z,PT30M,12
                    """);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
