package ru.practicum.kanban.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.service.TaskManager;

import java.util.List;

public class EpicHandler extends BaseEntityHandler<Epic> {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    protected List<Epic> getAllEntities() {
        return taskManager.getAllEpics();
    }

    @Override
    protected Epic getEntityById(int id) throws NotFoundException {
        return taskManager.getEpic(id);
    }

    @Override
    protected Epic createEntity(Epic epic) throws ManagerCreateTaskException, ManagerPrioritizedException {
        taskManager.create(epic);
        return epic;
    }

    @Override
    protected Epic updateEntity(Epic epic) throws ManagerUpdateTaskException {
        throw new UnsupportedOperationException("Update is not supported for Epics");
    }

    @Override
    protected Epic deleteEntity(int id) throws NotFoundException {
        return taskManager.deleteEpic(id);
    }

    @Override
    protected Class<Epic> getEntityClass() {
        return Epic.class;
    }

    @Override
    protected void setEntityId(Epic epic, int id) {
        epic.setId(id);
    }

    @Override
    protected boolean hasIdSubtasks(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().matches("^/\\w+/\\d+/subtasks$");
    }
}
