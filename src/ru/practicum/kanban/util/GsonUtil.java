package ru.practicum.kanban.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.kanban.adapter.DurationAdapter;
import ru.practicum.kanban.adapter.InstantAdapter;

import java.time.Duration;
import java.time.Instant;

public class GsonUtil {
    private GsonUtil() {
        // Don't let anyone instantiate this class.
    }


    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
    }
}
