package ru.practicum.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseHttpTest;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.kanban.server.Constants.*;

public class HttpTaskManagerSubtasksTest extends BaseHttpTest {

    private final URI SUBTASKS_URL_WITH_ID = URI.create(SUBTASKS_URL + "/3");


    @Test
    void shouldReturnSubtaskListOnGETAllSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(SUBTASKS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = String.format("[%s,%s]", st1JSON, st2JSON);
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(2, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldReturnSubtaskOnGETSubtaskId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(SUBTASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedSubtask = st1JSON;
        String actualSubtask = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(expectedSubtask, actualSubtask);
    }

    @Test
    void shouldCreateSubtaskWhenPOSTRequestSentToSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(st1JSON))
                .uri(SUBTASKS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(HTTP_CREATED, response.statusCode());
        assertEquals(3, taskManager.getAllSubtasks().size());
    }

    @Test
    void shouldUpdateSubtaskWhenPOSTRequestSentToSubtasksId() throws IOException, InterruptedException {
        Subtask ust = new Subtask
                (0, "ust", Status.NEW, "updated_subtask", afterTask(st2), DURATION, e1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(ust)))
                .uri(SUBTASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedTitle = "ust";
        String expectedDescription = "updated_subtask";
        String actualTitle = taskManager.getSubtask(3).getTitle();
        String actualDescription = taskManager.getSubtask(3).getDescription();

        assertEquals(HTTP_CREATED, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedDescription, actualDescription);
    }

    @Test
    void shouldDeleteSubtaskWhenDELETERequestSentToSubtasksId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(SUBTASKS_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(HTTP_OK, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

}
