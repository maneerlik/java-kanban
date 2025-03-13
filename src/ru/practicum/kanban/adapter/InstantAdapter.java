package ru.practicum.kanban.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class InstantAdapter extends TypeAdapter<Instant> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        if (Objects.isNull(instant)) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(FORMATTER.format(instant));
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String instantString = jsonReader.nextString();
        return Instant.from(FORMATTER.parse(instantString));
    }
}
