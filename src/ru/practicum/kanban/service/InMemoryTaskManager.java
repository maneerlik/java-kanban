package ru.practicum.kanban.service;

import ru.practicum.kanban.model.*;

import java.util.*;

/**
 * Класс {@code InMemoryTaskManager} реализует интерфейс {@code TaskManager} и
 * обеспечивает хранение информации о задачах в оперативной памяти.
 *
 * @author  Smirnov Sergey
 */
public class InMemoryTaskManager implements TaskManager {
    private static int idCounter = 0;

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    //--- Получение всех задач -----------------------------------------------------------------------------------------
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //--- Удаление всех задач ------------------------------------------------------------------------------------------
    @Override
    public void clearTasks() {
        removeAllTasksFromHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        removeAllTasksFromHistory(epics.keySet());
        removeAllTasksFromHistory(subtasks.keySet());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        removeAllTasksFromHistory(subtasks.keySet());
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearEpicsSubtasks();
            evaluateEpicStatus(epic);
        }
    }

    //--- Получение по идентификатору ----------------------------------------------------------------------------------
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(new Task(task));
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(new Epic(epic));
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(new Subtask(subtask));
        return subtask;
    }

    //--- Создание задачи в менеджере ----------------------------------------------------------------------------------
    /**
     * Метод создания реализован через промежуточную ссылку для того, чтобы не было прямого доступа к объекту извне
     * */
    @Override
    public <T extends Task> T create(T task) {
        task.setId(generateId());

        Type type = task.getType();

        switch (type) {
            case Type.TASK -> {
                Task newTask = new Task(task);
                tasks.put(newTask.getId(), newTask);
                return task;
            }
            case Type.EPIC -> {
                Epic newEpic = new Epic((Epic) task);
                epics.put(newEpic.getId(), newEpic);
                return task;
            }
            case Type.SUBTASK -> {
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
    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }

        Task updatedTask = new Task(task);
        tasks.replace(updatedTask.getId(), updatedTask);
        return task;
    }

    @Override
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

    @Override
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
    @Override
    public Task deleteTask(int id) {
        removeTaskFromHistory(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksIds()) {
            removeTaskFromHistory(subtaskId);
            subtasks.remove(subtaskId);
        }

        removeTaskFromHistory(id);
        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(subtasks.get(id).getId());
        evaluateEpicStatus(epic);
        removeTaskFromHistory(id);
        return subtasks.remove(id);
    }

    //--- Получение списка подзадач эпика по идентификатору ------------------------------------------------------------
    @Override
    public List<Subtask> getSubtasksByEpic(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasksByEpicIds = epic.getSubtasksIds();

        if (subtasksByEpicIds.isEmpty()) {
            return new ArrayList<>();
        }

        return subtasksByEpicIds.stream().map(subtasks::get).toList();
    }

    //--- Переоценка статуса эпика -------------------------------------------------------------------------------------
    @Override
    public void evaluateEpicStatus(Epic epic) {
        List<Integer> subtasksIds = epic.getSubtasksIds();

        boolean isAnySubtaskInProgress = subtasksIds.stream()
                .map(subtasks::get)
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);

        if (isAnySubtaskInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }

        boolean isSubtasksIdsIsEmpty = subtasksIds.isEmpty();

        boolean isAllSubtaskNEW = subtasksIds.stream()
                .allMatch(id -> subtasks.get(id).getStatus() == Status.NEW);

        boolean isAllSubtaskDONE = subtasksIds.stream()
                .allMatch(id -> subtasks.get(id).getStatus() == Status.DONE);

        if (isSubtasksIdsIsEmpty || isAllSubtaskDONE) {
            epic.setStatus(Status.DONE);
        } else if (isAllSubtaskNEW) {
            epic.setStatus(Status.NEW);
        }
    }

    //--- Просмотр истории (последние 10 просмотренных задач) ----------------------------------------------------------
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
    private static Integer generateId() {
        return idCounter++;
    }

    protected static void setIdCounter(int id) {
        idCounter = id;
    }

    protected void addTask(Task task) {
        Type type = task.getType();

        switch (type) {
            case TASK -> tasks.put(task.getId(), task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
            case SUBTASK -> {
                Subtask subtask = (Subtask) task;
                epics.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
            }
        }
    }

    private void removeTaskFromHistory(int id) {
        historyManager.remove(id);
    }

    private void removeAllTasksFromHistory(Set<Integer> ids) {
        ids.forEach(this::removeTaskFromHistory);
    }

    @Override
    public String toString() {
        return String.format("InMemoryTaskManager{\n\ttasks=%s,\n\tepics=%s,\n\tsubtasks=%s\n}", tasks, epics, subtasks);
    }
}
