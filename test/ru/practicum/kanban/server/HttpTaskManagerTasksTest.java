package ru.practicum.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseHttpTest;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.kanban.server.Constants.*;

public class HttpTaskManagerTasksTest extends BaseHttpTest {

    private final URI TASKS_URL_WITH_ID = URI.create(TASKS_URL + "/0");


    @Test
    void shouldReturnTaskListOnGETAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(TASKS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = String.format("[%s,%s]", t1JSON, t2JSON);
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(2, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldReturnTaskOnGETTaskId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(TASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedTask = t1JSON;
        String actualTask = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(expectedTask, actualTask);
    }

    @Test
    void shouldCreateTaskWhenPOSTRequestSentToTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(t1JSON))
                .uri(TASKS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(HTTP_CREATED, response.statusCode());
        assertEquals(3, taskManager.getAllTasks().size());
    }

    @Test
    void shouldUpdateTaskWhenPOSTRequestSentToTasksId() throws IOException, InterruptedException {
        Task ut = new Task(0, "ut", Status.NEW, "updated_task", afterTask(t2), DURATION);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ut)))
                .uri(TASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedTitle = "ut";
        String expectedDescription = "updated_task";
        String actualTitle = taskManager.getTask(0).getTitle();
        String actualDescription = taskManager.getTask(0).getDescription();

        assertEquals(HTTP_CREATED, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void shouldDeleteTaskWhenDELETERequestSentToTasksId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(TASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(HTTP_OK, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(1, taskManager.getAllTasks().size());
    }

}
