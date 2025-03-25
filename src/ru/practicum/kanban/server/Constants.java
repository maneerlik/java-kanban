package ru.practicum.kanban.server;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Constants {
    private Constants() {
        // Don't let anyone instantiate this class.
    }


    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final URI BASE_URL = URI.create("http://localhost:8080");
    public static final URI TASKS_URL = URI.create(BASE_URL + "/tasks");
    public static final URI SUBTASKS_URL = URI.create(BASE_URL + "/subtasks");
    public static final URI EPICS_URL = URI.create(BASE_URL + "/epics");
    public static final URI HISTORY_URL = URI.create(BASE_URL + "/history");
    public static final URI PRIORITIZED_URL = URI.create(BASE_URL + "/prioritized");
}
