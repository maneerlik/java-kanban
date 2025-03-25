package ru.practicum.kanban.exception;

public class ManagerUpdateTaskException extends RuntimeException {
    public ManagerUpdateTaskException(String message) {
        super(message);
    }
}
