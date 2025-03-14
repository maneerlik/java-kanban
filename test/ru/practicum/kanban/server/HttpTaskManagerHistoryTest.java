package ru.practicum.kanban.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.practicum.kanban.BaseHttpTest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.kanban.server.Constants.*;

public class HttpTaskManagerHistoryTest extends BaseHttpTest {

    @Test
    void shouldReturnTaskListOnGETHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(HISTORY_URL)
                .build();

        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        String expectedList = String.format("%s", gson.toJson(taskManager.getHistory()));
        String actualList = jsonElement.toString();

        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(1, jsonElement.getAsJsonArray().size());
        assertEquals(expectedList, actualList);
    }

}
