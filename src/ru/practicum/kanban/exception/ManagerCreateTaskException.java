package ru.practicum.kanban.exception;

public class ManagerCreateTaskException extends RuntimeException {
    public ManagerCreateTaskException(String message) {
        super(message);
    }
}
