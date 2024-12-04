package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private static int idCounter = 0;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();


    //--- Получение всех задач -----------------------------------------------------------------------------------------
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //--- Удаление всех задач ------------------------------------------------------------------------------------------
    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearEpicsSubtasks();
            evaluateEpicStatus(epic);
        }
    }

    //--- Получение по идентификатору ----------------------------------------------------------------------------------
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    //--- Создание задачи в менеджере ----------------------------------------------------------------------------------
    /**
     * Метод создания реализован через промежуточную ссылку для того, чтобы не было прямого доступа к объекту извне
     * */
    public <T extends Task> T create(T task) {
        task.setId(generateId());

        String type = task.getType();

        switch (type) {
            case "Task" -> {
                Task newTask = new Task(task);
                tasks.put(newTask.getId(), newTask);
                return task;
            }
            case "Epic" -> {
                Epic newEpic = new Epic((Epic) task);
                epics.put(newEpic.getId(), newEpic);
                return task;
            }
            case "Subtask" -> {
                Subtask newSubtask = new Subtask((Subtask) task);
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epic = epics.get(newSubtask.getEpicId());
                epic.getSubtasksIds().add(newSubtask.getId());
                evaluateEpicStatus(epic);
                return task;
            }
            default -> throw new IllegalArgumentException("Task class " + type + " does not exist");
        }
    }

    //--- Обновление задачи в менеджере --------------------------------------------------------------------------------
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }

        Task updatedTask = new Task(task);
        tasks.replace(updatedTask.getId(), updatedTask);
        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        }

        Epic updatedEpic = new Epic(epic);
        epics.replace(updatedEpic.getId(), updatedEpic);
        evaluateEpicStatus(updatedEpic);
        evaluateEpicStatus(epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return null;
        }

        Subtask updatedSubtask = new Subtask(subtask);
        subtasks.replace(updatedSubtask.getId(), updatedSubtask);
        Epic epic = epics.get(updatedSubtask.getEpicId());
        evaluateEpicStatus(epic);
        return subtask;
    }

    //--- Удаление по идентификатору -----------------------------------------------------------------------------------
    public Task deleteTask(int id) {
        return tasks.remove(id);
    }

    public Epic deleteEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }

        return epics.remove(id);
    }

    public Subtask deleteSubtask(int id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(id);

        return subtasks.remove(id);
    }

    //--- Получение списка подзадач эпика по идентификатору ------------------------------------------------------------
    public List<Subtask> getSubtasksByEpic(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksByEpicIds = epic.getSubtasksIds();

        return subtasks.values().stream()
                .filter(subtask -> subtasksByEpicIds.contains(subtask.getId()))
                .toList();
    }

    //--- Переоценка статуса эпика -------------------------------------------------------------------------------------
    private void evaluateEpicStatus(Epic epic) {
        List<Integer> subtasksIds = epic.getSubtasksIds();

        boolean isSubtasksIdsIsEmpty = subtasksIds.isEmpty();

        boolean isAllSubtaskNEW = subtasksIds.stream()
                .allMatch(id -> subtasks.get(id).getStatus() == Status.NEW);

        boolean isAllSubtaskDONE = subtasksIds.stream()
                .allMatch(id -> subtasks.get(id).getStatus() == Status.DONE);

        if (isSubtasksIdsIsEmpty || isAllSubtaskNEW) {
            epic.setStatus(Status.NEW);
        } else if (isAllSubtaskDONE) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
    private static Integer generateId() {
        return idCounter++;
    }
}
