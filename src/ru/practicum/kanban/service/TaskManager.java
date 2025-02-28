package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;

import java.util.List;

public interface TaskManager {
    //--- Получение всех задач -----------------------------------------------------------------------------------------
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    //--- Удаление всех задач ------------------------------------------------------------------------------------------
    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    //--- Получение по идентификатору ----------------------------------------------------------------------------------
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    //--- Создание задачи в менеджере ----------------------------------------------------------------------------------
    <T extends Task> T create(T task);

    //--- Обновление задачи в менеджере --------------------------------------------------------------------------------
    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    //--- Удаление по идентификатору -----------------------------------------------------------------------------------
    Task deleteTask(int id);

    Epic deleteEpic(int id);

    Subtask deleteSubtask(int id);

    //--- Получение списка подзадач эпика по идентификатору ------------------------------------------------------------
    List<Subtask> getSubtasksByEpic(int id);

    //--- Переоценка статуса эпика -------------------------------------------------------------------------------------
    void evaluateEpicStatus(Epic epic);

    //--- Просмотр истории (последние 10 просмотренных задач) ----------------------------------------------------------
    List<Task> getHistory();

    //--- Получение отсортированного по приоритету списка задач и подзадач ---------------------------------------------
    List<Task> getPrioritizedTasks();
}
