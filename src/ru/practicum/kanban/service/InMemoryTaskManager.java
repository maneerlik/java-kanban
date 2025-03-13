package ru.practicum.kanban.service;

import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
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

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>();

    private final HistoryManager historyManager;


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
        removeTasksFromPrioritizedList(getAllTasks());
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        removeAllTasksFromHistory(epics.keySet());
        removeAllTasksFromHistory(subtasks.keySet());
        removeTasksFromPrioritizedList(getAllSubtasks());
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        removeAllTasksFromHistory(subtasks.keySet());
        removeTasksFromPrioritizedList(getAllSubtasks());
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearEpicsSubtasks();
            evaluateEpicStatus(epic);
            evaluateEpicPriority(epic);
        }
    }

    //--- Получение по идентификатору ----------------------------------------------------------------------------------
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) throw new NotFoundException(getErrorMessage(Action.GET, id));
        historyManager.add(new Task(task));
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) throw new NotFoundException(getErrorMessage(Action.GET, id));
        historyManager.add(new Epic(epic));
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) throw new NotFoundException(getErrorMessage(Action.GET, id));
        historyManager.add(new Subtask(subtask));
        return subtask;
    }

    //--- Создание задачи в менеджере ----------------------------------------------------------------------------------
    /**
     * Метод создания реализован через промежуточную ссылку для того, чтобы не было прямого доступа к объекту извне
     * */
    @Override
    public <T extends Task> T create(T task) {
        if (task == null)
            throw new ManagerCreateTaskException(getErrorMessage(Action.CREATE));

        task.setId(generateId());

        Type type = task.getType();

        switch (type) {
            case Type.TASK -> {
                Task newTask = new Task(task);
                addTaskToPrioritizedList(newTask);
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
                addTaskToPrioritizedList(newSubtask);
                subtasks.put(newSubtask.getId(), newSubtask);
                Epic epic = epics.get(newSubtask.getEpicId());
                epic.getSubtasksIds().add(newSubtask.getId());
                evaluateEpicStatus(epic);
                evaluateEpicPriority(epic);
                return task;
            }
            default -> throw new IllegalArgumentException("Task class " + type + " does not exist");
        }
    }

    //--- Обновление задачи в менеджере --------------------------------------------------------------------------------
    @Override
    public Task updateTask(Task task) {
        if (task == null)
            throw new ManagerUpdateTaskException(getErrorMessage(Action.UPDATE));

        if (!tasks.containsKey(task.getId()))
            throw new ManagerUpdateTaskException(getErrorMessage(Action.UPDATE, task.getId()));

        Task updatedTask = new Task(task);
        addTaskToPrioritizedList(updatedTask);
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
        evaluateEpicPriority(updatedEpic);
        evaluateEpicStatus(epic);
        evaluateEpicPriority(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null)
            throw new ManagerUpdateTaskException(getErrorMessage(Action.UPDATE));

        if (!subtasks.containsKey(subtask.getId()))
            throw new ManagerUpdateTaskException(getErrorMessage(Action.UPDATE, subtask.getId()));

        Subtask updatedSubtask = new Subtask(subtask);
        addTaskToPrioritizedList(updatedSubtask);
        subtasks.replace(updatedSubtask.getId(), updatedSubtask);
        Epic epic = epics.get(updatedSubtask.getEpicId());
        evaluateEpicStatus(epic);
        evaluateEpicPriority(epic);
        return subtask;
    }

    //--- Удаление по идентификатору -----------------------------------------------------------------------------------
    @Override
    public Task deleteTask(int id) {
        if (!tasks.containsKey(id)) throw new NotFoundException(getErrorMessage(Action.DELETE, id));
        removeTaskFromHistory(id);
        prioritizedTasks.remove(tasks.get(id));
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtasksIds()) {
            removeTaskFromHistory(subtaskId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        }

        removeTaskFromHistory(id);
        return epics.remove(id);
    }

    @Override
    public Subtask deleteSubtask(int id) {
        if (!subtasks.containsKey(id)) throw new NotFoundException(getErrorMessage(Action.DELETE, id));
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasksIds().remove(subtasks.get(id).getId());
        evaluateEpicStatus(epic);
        evaluateEpicPriority(epic);
        removeTaskFromHistory(id);
        prioritizedTasks.remove(subtasks.get(id));
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
        List<Subtask> epicSubtasks = getSubtasksByIds(epic.getSubtasksIds());
        epic.evaluateStatus(epicSubtasks);
    }

    //--- Просмотр истории (последние 10 просмотренных задач) ----------------------------------------------------------
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    //--- Вернуть отсортированный по приоритету список задач и подзадач ------------------------------------------------
    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
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

    public static void setIdCounter(int id) {
        idCounter = id;
    }

    /**
     *  Возвращает список подзадач на основе переданного списка идентификаторов.
     */
    private List<Subtask> getSubtasksByIds(List<Integer> subtasksIds) {
        return subtasksIds.stream().map(subtasks::get).toList();
    }

    /**
     * Проверяет, пересекается ли задача с любой из списка приоритетных.
     * @param task задача, которую нужно проверить на пересечение.
     * @return {@code true}, если пересечение найдено;
     *         {@code false}, пересечение не найдено.
     */
    private boolean isOverlapping(Task task) {
        Optional<Task> isOverlappedTask = getPrioritizedTasks().stream()
                .filter(task::isIntersect)
                .findFirst();

        return isOverlappedTask.isPresent();
    }

    private void addTaskToPrioritizedList(Task task) {
        if (!task.isPrioritizedTask()) return;
        if (prioritizedTasks.contains(task)) return;
        if (isOverlapping(task)) throw new ManagerPrioritizedException("Error. Tasks Intersection");

        prioritizedTasks.add(task);
    }

    private static Integer generateId() {
        return idCounter++;
    }

    private void evaluateEpicPriority(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByIds(epic.getSubtasksIds());
        epic.calculateStartTime(epicSubtasks);
        epic.calculateEndTime(epicSubtasks);
        epic.calculateDuration();
    }

    private void removeTasksFromPrioritizedList(List<? extends Task> deletableTasks) {
        deletableTasks.forEach(prioritizedTasks::remove);
    }

    private void removeTaskFromHistory(int id) {
        historyManager.remove(id);
    }

    private void removeAllTasksFromHistory(Set<Integer> ids) {
        ids.forEach(this::removeTaskFromHistory);
    }

    private String getErrorMessage(Action action) {
        return switch (action) {
            case CREATE -> "Create error. Null value not allowed";
            case GET -> null;
            case UPDATE -> "Update error. Null value not allowed";
            case DELETE -> null;
        };
    }

    private String getErrorMessage(Action action, int id) {
        return switch (action) {
            case CREATE -> null;
            case GET -> String.format("Record %s missing", id);
            case UPDATE -> String.format("Update error. Record %s missing", id);
            case DELETE -> String.format("Delete error. Record %s missing", id);
        };
    }

    private enum Action { CREATE, GET, UPDATE, DELETE }

    @Override
    public String toString() {
        return String.format("InMemoryTaskManager{\n\ttasks=%s,\n\tepics=%s,\n\tsubtasks=%s\n}", tasks, epics, subtasks);
    }
}
