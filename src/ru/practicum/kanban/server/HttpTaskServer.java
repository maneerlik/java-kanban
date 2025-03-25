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

/**
 * Класс, реализующий HTTP-сервер для управления задачами в системе. Предоставляет API для работы с задачами, эпиками,
 * подзадачами, историей просмотров и приоритизированным списком задач. Использует {@link HttpServer}
 * для обработки HTTP-запросов и предоставляет контексты для различных типов сущностей
 *
 * <p>Обеспечивает:
 * <ul>
 *   <li>Создание и настройку HTTP-сервера на указанном хосте и порту</li>
 *   <li>Регистрацию обработчиков для различных типов сущностей (задачи, эпики, подзадачи)</li>
 *   <li>Запуск и остановку сервера</li>
 *   <li>Использование {@link TaskManager} для управления задачами и {@link Gson} для сериализации/десериализации
 *   JSON</li>
 * </ul>
 *
 * <p>Эндпоинты:
 * <ul>
 *   <li>{@code /tasks} - управление задачами через {@link TaskHandler}</li>
 *   <li>{@code /subtasks} - управление подзадачами через {@link SubtaskHandler}</li>
 *   <li>{@code /epics} - управление эпиками через {@link EpicHandler}</li>
 *   <li>{@code /history} - получение истории просмотров через {@link HistoryHandler}</li>
 *   <li>{@code /prioritized} - получение приоритизированного списка задач через {@link PrioritizedHandler}</li>
 * </ul>
 *
 * <p>Логирование {@link Logger}, отслеживание событий: запуск, остановка сервера и ошибки конфигурации
 *
 * @author  Smirnov Sergey
 */
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
