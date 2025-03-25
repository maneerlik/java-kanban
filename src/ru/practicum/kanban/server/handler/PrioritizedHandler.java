package ru.practicum.kanban.server.handler;

import static java.net.HttpURLConnection.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

import java.util.List;


public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    //--- Реализация handle --------------------------------------------------------------------------------------------
    @Override
    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (requestMethod.equals("GET") && path.matches("/prioritized")) {
            getPrioritizedTasks(exchange);
            return;
        }
        sendNotFound(exchange, "");
    }

    //--- Реализация обработки запросов --------------------------------------------------------------------------------
    private void getPrioritizedTasks(HttpExchange exchange) {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendText(exchange, gson.toJson(prioritizedTasks), HTTP_OK);
    }
}
