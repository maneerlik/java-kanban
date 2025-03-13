package ru.practicum.kanban.server.handler;

import com.google.gson.Gson;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

import java.util.List;

public class TaskHandler extends BaseEntityHandler<Task> {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    protected List<Task> getAllEntities() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Task getEntityById(int id) throws NotFoundException {
        return taskManager.getTask(id);
    }

    @Override
    protected Task createEntity(Task task) throws ManagerCreateTaskException, ManagerPrioritizedException {
        taskManager.create(task);
        return task;
    }

    @Override
    protected Task updateEntity(Task task) throws ManagerUpdateTaskException {
        taskManager.updateTask(task);
        return task;
    }

    @Override
    protected Task deleteEntity(int id) throws NotFoundException {
        return taskManager.deleteTask(id);
    }

    @Override
    protected List<Subtask> getEntitySubtasks(int id) {
        return List.of(); // Заглушка
    }

    @Override
    protected Class<Task> getEntityClass() {
        return Task.class;
    }

    @Override
    protected void setEntityId(Task task, int id) {
        task.setId(id);
    }
}
