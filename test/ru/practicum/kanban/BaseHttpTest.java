package ru.practicum.kanban;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.server.HttpTaskServer;
import ru.practicum.kanban.service.TaskManager;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public abstract class BaseHttpTest {
    protected final Duration DURATION = Duration.ofMinutes(15);

    protected HttpTaskServer server;
    protected TaskManager taskManager;
    protected Gson gson;

    protected HttpResponse.BodyHandler<String> handler;
    protected HttpClient client;

    protected Task t1;
    protected Task t2;
    protected Epic e1;
    protected Subtask st1;
    protected Subtask st2;

    protected String t1JSON;
    protected String t2JSON;
    protected String e1JSON;
    protected String e1stlJSON;
    protected String st1JSON;
    protected String st2JSON;


    @BeforeEach
    public void setUp() {
        server = new HttpTaskServer();
        gson = server.getGson();
        taskManager = server.getTaskManager();

        server.start();

        handler = HttpResponse.BodyHandlers.ofString();
        client = HttpClient.newHttpClient();

        t1 = new Task(0, "t1", Status.NEW, "task_1", Instant.now(), DURATION);
        t2 = new Task(1, "t2", Status.NEW, "task_2", afterTask(t1), DURATION);
        taskManager.create(t1);
        taskManager.create(t2);

        t1JSON = gson.toJson(t1);
        t2JSON = gson.toJson(t2);

        e1 = new Epic("e1", "epic_1");
        taskManager.create(e1);
        int eId = e1.getId();

        st1 = new Subtask(3, "st1", Status.NEW, "subtask_1", afterTask(t2), DURATION, eId);
        st2 = new Subtask(4, "st2", Status.NEW, "subtask_2", afterTask(st1), DURATION, eId);
        taskManager.create(st1);
        taskManager.create(st2);

        st1JSON = gson.toJson(st1);
        st2JSON = gson.toJson(st2);
        e1JSON = gson.toJson(taskManager.getEpic(eId));
        e1stlJSON = gson.toJson(taskManager.getSubtasksByEpic(eId));
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    protected <T extends Task> Instant afterTask(T task) {
        return task.getStartTime().plusSeconds(DURATION.toSeconds());
    }
}
