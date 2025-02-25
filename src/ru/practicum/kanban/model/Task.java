package ru.practicum.kanban.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private final String title;
    private final String description;
    private Integer id;
    private Instant startTime;
    private Duration duration;

    private Status status = Status.NEW;


    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.id = task.id;
        this.status = task.status;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(int id, String title, Status status, String description, Instant startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getEndTime() {
        if (isPrioritizedTask()) {
            return startTime.plus(duration);
        }
        return null;
    }

    public Duration getDuration() {
        return duration;
    }

    protected void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    protected void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Type getType() {
        return Type.TASK;
    }

    /**
     * Сериализует объект в строку CSV-формата.
     * <p>
     * Формат строки:
     * <pre>{@code <id>,<type>,<title>,<status>,<description>,<startTime>,<duration>,}</pre>
     * </p>
     *
     * Пример строки:
     * <pre>{@code "11,TASK,Накормить кота,NEW,Насыпать корма коту,2025-02-21T19:13:41.574284100Z,PT2H45M,"}</pre>
     */
    public String toCsvString() {
        String startTime = Objects.isNull(this.startTime) ? "" : this.startTime.toString();
        String duration = Objects.isNull(this.duration) ? "" : this.duration.toString();

        return String.format("%d,%s,%s,%s,%s,%s,%s,", id, getType(), title, status, description, startTime, duration);
    }

    /**
     * Проверяет, пересекается ли текущая задача с другой по времени.
     *
     * @param other другая задача, с которой проверяется пересечение.
     * @return {@code true}, если задачи пересекаются по времени, иначе {@code false}.
     *
     * <p>Задачи пересекаются, если время начала текущей задачи на временной шкале левее
     * (раньше) времени окончания другой задачи, и время окончания текущей задачи правее
     * (позже) времени начала другой задачи. Если текущая задача является не приоритетной,
     * проверка пересечения не выполняется, и метод возвращает {@code true}.
     */
    public boolean isIntersect(Task other) {
        if (isPrioritizedTask()) {
            boolean isStartsBeforeEnd = startTime.isBefore(other.getEndTime());
            boolean isEndsAfterStart = getEndTime().isAfter(other.startTime);

            return isStartsBeforeEnd && isEndsAfterStart;
        }
        return true;
    }

    public boolean isPrioritizedTask() {
        return startTime != null && duration != null;
    }

    @Override
    public int compareTo(Task other) {
        return startTime.compareTo(other.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%s{title='%s', description='%s', id=%d, status=%s}",
                this.getClass().getSimpleName(), title, description, id, status);
    }
}
