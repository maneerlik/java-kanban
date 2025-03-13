package ru.practicum.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseHttpTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.kanban.server.Constants.*;

public class HttpTaskManagerEpicsTest extends BaseHttpTest {

    private final URI EPIC_URL_WITH_ID = URI.create(EPICS_URL + "/2");
    private final URI EPIC_URL_WITH_ID_SUBTASKS = URI.create(EPIC_URL_WITH_ID + "/subtasks");


    @Test
    void shouldReturnEpicListOnGETAllEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(EPICS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = String.format("[%s]", e1JSON);
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(1, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldReturnEpicOnGETEpicId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(EPIC_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedEpic = e1JSON;
        String actualEpic = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(expectedEpic, actualEpic);
    }

    @Test
    void shouldCreateEpicWhenPOSTRequestSentToEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(e1JSON))
                .uri(EPICS_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(HTTP_CREATED, response.statusCode());
        assertEquals(2, taskManager.getAllEpics().size());
    }

    @Test
    void shouldReturnSubtaskListOnPOSTRequestSentToEpicSubtasksId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(EPIC_URL_WITH_ID_SUBTASKS)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = e1stlJSON;
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(2, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

    @Test
    void shouldDeleteEpicWhenDELETERequestSentToEpicsId() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(EPIC_URL_WITH_ID)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(HTTP_OK, response.statusCode());
        assertTrue(jsonElement.isJsonObject());
        assertEquals(0, taskManager.getAllEpics().size());
    }

}
