package ru.practicum.kanban.model;

public class Task {
    private final String title;
    private final String description;
    private Integer id = null;

    private Status status = Status.NEW;


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.id = task.id;
        this.status = task.status;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    @Override
    public String toString() {
        return String.format("%s{title='%s', description='%s', id=%d, status=%s}",
                this.getClass().getSimpleName(), title, description, id, status);
    }
}
