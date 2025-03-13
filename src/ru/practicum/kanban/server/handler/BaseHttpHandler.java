package ru.practicum.kanban.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.kanban.exception.ManagerCreateTaskException;
import ru.practicum.kanban.exception.ManagerPrioritizedException;
import ru.practicum.kanban.exception.ManagerUpdateTaskException;
import ru.practicum.kanban.exception.NotFoundException;
import ru.practicum.kanban.service.TaskManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.practicum.kanban.server.Constants.*;


public abstract class BaseHttpHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(BaseHttpHandler.class.getName());

    protected TaskManager taskManager;
    protected Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }


    public abstract void handle(HttpExchange exchange);

    protected void sendText(HttpExchange exchange, String text, Integer status) {
        try (exchange) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(status, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException ignored) {
            logger.log(Level.SEVERE, ignored.getMessage(), ignored);
        }
    }

    protected String readRequestBody(HttpExchange exchange) {
        String body = null;

        try {
            body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        } catch (IOException ignored) {
            logger.log(Level.SEVERE, ignored.getMessage(), ignored);
        }

        return body;
    }

    protected void sendNotFound(HttpExchange exchange, String text) {
        if (text.isBlank()) {
            text = String.format("%s %s not found", exchange.getRequestMethod(), exchange.getRequestURI());
            sendText(exchange, text, HTTP_NOT_FOUND);
            return;
        }
        sendText(exchange, text, HTTP_NOT_FOUND);
    }

    protected boolean isRootPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().matches("^/\\w+$");
    }

    protected boolean hasId(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().matches("^/\\w+/\\d+$");
    }

    protected int extractId(HttpExchange exchange) {
        String[] segments = exchange.getRequestURI().getPath().split("/");
        return Integer.parseInt(segments[2]);
    }

    protected void handleException(HttpExchange exchange, Exception exception) {
        switch (exception) {
            case NotFoundException e -> sendNotFound(exchange, e.getMessage());
            case NumberFormatException e -> sendText(exchange, "Bad request", HTTP_BAD_REQUEST);
            case ManagerCreateTaskException e -> sendText(exchange, e.getMessage(), HTTP_BAD_REQUEST);
            case ManagerUpdateTaskException e -> sendText(exchange, e.getMessage(), HTTP_BAD_REQUEST);
            case ManagerPrioritizedException e -> sendText(exchange, e.getMessage(), HTTP_NOT_ACCEPTABLE);
            default -> sendText(exchange, "Internal server error", HTTP_INTERNAL_ERROR);
        }
    }
}
