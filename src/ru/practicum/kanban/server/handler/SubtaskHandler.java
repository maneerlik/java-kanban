package ru.practicum.kanban.server.handler;

import com.google.gson.Gson;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.service.TaskManager;

import java.util.List;

public class SubtaskHandler extends BaseEntityHandler<Subtask> {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    protected List<Subtask> getAllEntities() {
        return taskManager.getAllSubtasks();
    }

    @Override
    protected Subtask getEntityById(int id) throws NotFoundException {
        return taskManager.getSubtask(id);
    }

    @Override
    protected Subtask createEntity(Subtask subtask) throws ManagerCreateTaskException, ManagerPrioritizedException {
        taskManager.create(subtask);
        return subtask;
    }

    @Override
    protected Subtask updateEntity(Subtask subtask) throws ManagerUpdateTaskException {
        taskManager.updateSubtask(subtask);
        return subtask;
    }

    @Override
    protected Subtask deleteEntity(int id) throws NotFoundException {
        return taskManager.deleteSubtask(id);
    }

    @Override
    protected Class<Subtask> getEntityClass() {
        return Subtask.class;
    }

    @Override
    protected void setEntityId(Subtask subtask, int id) {
        subtask.setId(id);
    }
}
