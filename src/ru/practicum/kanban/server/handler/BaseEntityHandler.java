package ru.practicum.kanban.server.handler;

import static java.net.HttpURLConnection.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.service.TaskManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Абстрактный базовый класс для обработки HTTP-запросов, связанных с сущностями (Task, Epic, Subtask) в системе
 * управления задачами. Предоставляет обобщенную структуру для выполнения операций CRUD (Create, Read, Update, Delete)
 * путем реализации обработчиков для HTTP-методов GET, POST и DELETE
 *
 * <p>Для сериализации и десериализации JSON используется библиотека {@link com.google.gson.Gson}
 *
 * <p>Подклассы должны реализовать абстрактные методы, чтобы предоставить специфическую для сущности логику: получения,
 * создания, обновления и удаления
 *
 * @param <T> тип сущности, обрабатываемой классом (Task, Epic, Subtask)
 *
 * @author  Smirnov Sergey
 */
public abstract class BaseEntityHandler<T> extends BaseHttpHandler {
    protected final Map<String, Consumer<HttpExchange>> handlers;

    protected BaseEntityHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);

        handlers = new HashMap<>();
        handlers.put("GET", this::handleGet);
        handlers.put("POST", this::handlePost);
        handlers.put("DELETE", this::handleDelete);
    }


    //--- Реализация handle --------------------------------------------------------------------------------------------
    @Override
    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();

        if (handlers.containsKey(requestMethod)) {
            handlers.get(requestMethod).accept(exchange);
            return;
        }
        sendText(exchange, "Method not allowed", HTTP_BAD_METHOD);
    }

    //--- Реализация универсальных обработчиков ------------------------------------------------------------------------
    void handleGet(HttpExchange exchange) {
        if (isRootPath(exchange)) {
            getAllEntities(exchange);
            return;
        }
        if (hasId(exchange)) {
            getEntityById(exchange);
            return;
        }
        if (hasIdSubtasks(exchange)) {
            getEntitySubtasks(exchange);
            return;
        }
        sendNotFound(exchange, "");
    }

    void handlePost(HttpExchange exchange) {
        if (isRootPath(exchange)) {
            createEntity(exchange);
            return;
        }
        if (hasId(exchange)) {
            updateEntity(exchange);
            return;
        }
        sendNotFound(exchange, "");
    }

    void handleDelete(HttpExchange exchange) {
        if (hasId(exchange)) {
            deleteEntity(exchange);
            return;
        }
        sendNotFound(exchange, "");
    }

    //--- Реализация обобщенных методов обработки запросов -------------------------------------------------------------
    private void getAllEntities(HttpExchange exchange) {
        List<T> entities = getAllEntities();
        sendText(exchange, gson.toJson(entities), HTTP_OK);
    }

    private void getEntityById(HttpExchange exchange) {
        try {
            int id = extractId(exchange);
            T entity = getEntityById(id);
            sendText(exchange, gson.toJson(entity), HTTP_OK);
        } catch (NotFoundException | NumberFormatException e) {
            handleException(exchange, e);
        }
    }

    private void createEntity(HttpExchange exchange) {
        try {
            String requestBody = readRequestBody(exchange);
            T entity = gson.fromJson(requestBody, getEntityClass());
            T createdEntity = createEntity(entity);
            sendText(exchange, gson.toJson(createdEntity), HTTP_CREATED);
        } catch (ManagerCreateTaskException | ManagerPrioritizedException e) {
            handleException(exchange, e);
        }
    }

    private void updateEntity(HttpExchange exchange) {
        try {
            int id = extractId(exchange);
            String requestBody = readRequestBody(exchange);
            T entity = gson.fromJson(requestBody, getEntityClass());
            setEntityId(entity, id);
            T updatedEntity = updateEntity(entity);
            sendText(exchange, gson.toJson(updatedEntity), HTTP_CREATED);
        } catch (NumberFormatException | ManagerUpdateTaskException e) {
            handleException(exchange, e);
        }
    }

    private void deleteEntity(HttpExchange exchange) {
        try {
            int id = extractId(exchange);
            T entity = deleteEntity(id);
            sendText(exchange, gson.toJson(entity), HTTP_OK);
        } catch (NumberFormatException | NotFoundException e) {
            handleException(exchange, e);
        }
    }

    private void getEntitySubtasks(HttpExchange exchange) {
        int id = extractId(exchange);
        List<Subtask> subtasks = taskManager.getSubtasksByEpic(id);
        sendText(exchange, gson.toJson(subtasks), HTTP_OK);
    }

    protected boolean hasIdSubtasks(HttpExchange exchange) {
        return false;
    }

    //--- Вспомогательные методы ---------------------------------------------------------------------------------------
    protected abstract List<T> getAllEntities();

    protected abstract T getEntityById(int id) throws NotFoundException;

    protected abstract T createEntity(T entity) throws ManagerCreateTaskException, ManagerPrioritizedException;

    protected abstract T updateEntity(T entity) throws ManagerUpdateTaskException;

    protected abstract T deleteEntity(int id) throws NotFoundException;

    protected abstract Class<T> getEntityClass();

    protected abstract void setEntityId(T entity, int id);
}
