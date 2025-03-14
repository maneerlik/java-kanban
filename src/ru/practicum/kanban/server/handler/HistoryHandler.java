package ru.practicum.kanban.server.handler;

import static java.net.HttpURLConnection.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

import java.util.List;


public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    //--- Реализация handle --------------------------------------------------------------------------------------------
    @Override
    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (requestMethod.equals("GET") && path.matches("/history")) {
            getHistory(exchange);
            return;
        }
        sendNotFound(exchange, "");
    }

    //--- Реализация обработки запросов --------------------------------------------------------------------------------
    private void getHistory(HttpExchange exchange) {
        List<Task> history = taskManager.getHistory();
        sendText(exchange, gson.toJson(history), HTTP_OK);
    }
}
