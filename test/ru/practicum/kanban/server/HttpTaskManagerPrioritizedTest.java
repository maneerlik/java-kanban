package ru.practicum.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseHttpTest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.kanban.server.Constants.*;

public class HttpTaskManagerPrioritizedTest extends BaseHttpTest {

    @Test
    void shouldReturnTaskListOnGETPrioritized() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(PRIORITIZED_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = String.format("%s", gson.toJson(taskManager.getPrioritizedTasks()));
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(4, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

}
