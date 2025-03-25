package ru.practicum.kanban.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * Класс-адаптер для сериализации и десериализации объектов {@link Duration} в формат JSON. Расширяет
 * {@link TypeAdapter} и предоставляет специфическую логику для преобразования объектов типа {@link Duration}
 * в строковое представление (ISO-8601) и обратно
 *
 * <p>Обеспечивает:
 * <ul>
 *   <li>Сериализацию объекта {@link Duration} в строку, метод {@link Duration#toString()}</li>
 *   <li>Десериализацию строки в объект {@link Duration}, метод {@link Duration#parse(CharSequence)}</li>
 *   <li>Обработку значений null</li>
 * </ul>
 *
 * @author  Smirnov Sergey
 */
public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (Objects.isNull(duration)) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toString());
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        String durationString = jsonReader.nextString();
        return Duration.parse(durationString);
    }
}
