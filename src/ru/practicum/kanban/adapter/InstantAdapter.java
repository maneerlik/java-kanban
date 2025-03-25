package ru.practicum.kanban.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Класс-адаптер для сериализации и десериализации объектов {@link Instant} в формат JSON. Расширяет
 * {@link TypeAdapter} и предоставляет специфическую логику для преобразования объектов типа {@link Instant}
 * в строковое представление (ISO-8601) и обратно
 *
 * <p>Обеспечивает:
 * <ul>
 *   <li>Сериализацию объекта {@link Instant} в строку формата ISO-8601</li>
 *   <li>Десериализацию строки в объект {@link Instant}</li>
 *   <li>Обработку значений null</li>
 * </ul>
 *
 * @author  Smirnov Sergey
 */
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
