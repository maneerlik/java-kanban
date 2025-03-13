package ru.practicum.kanban.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.kanban.server.handler.*;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;
import ru.practicum.kanban.util.GsonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.practicum.kanban.service.InMemoryTaskManager.setIdCounter;

public class HttpTaskServer {
    private static final Logger logger = Logger.getLogger(HttpTaskServer.class.getName());

    private static final int PORT = 8080;
    private static final String HOSTNAME = "localhost";

    private HttpServer server;
    private final Gson gson;

    private final TaskManager taskManager;


    public HttpTaskServer() {
        setIdCounter(0);
        taskManager = Managers.getDefault();
        gson = GsonUtil.getGson();
        configureServer();
    }

    private void configureServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 0);
            server.createContext("/tasks", new TaskHandler(taskManager, gson));
            server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            server.createContext("/epics", new EpicHandler(taskManager, gson));
            server.createContext("/history", new HistoryHandler(taskManager, gson));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Сбой при конфигурировании сервера");
        }
    }

    public Gson getGson() {
        return gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void start() {
        logger.info("Server is running at " + server.getAddress().toString());
        server.start();
    }

    public void stop() {
        server.stop(1);
        logger.info("Stopped server at " + server.getAddress().toString());
    }
}
