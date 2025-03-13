package ru.practicum.kanban.server;

import java.net.HttpURLConnection;
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

    public static final int HTTP_OK = HttpURLConnection.HTTP_OK;
    public static final int HTTP_CREATED = HttpURLConnection.HTTP_CREATED;
    public static final int HTTP_BAD_REQUEST = HttpURLConnection.HTTP_BAD_REQUEST;
    public static final int HTTP_NOT_FOUND = HttpURLConnection.HTTP_NOT_FOUND;
    public static final int HTTP_INTERNAL_ERROR = HttpURLConnection.HTTP_INTERNAL_ERROR;
    public static final int HTTP_BAD_METHOD = HttpURLConnection.HTTP_BAD_METHOD;
    public static final int HTTP_NOT_ACCEPTABLE = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
}
