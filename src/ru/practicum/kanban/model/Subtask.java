package ru.practicum.kanban.model;

public class Subtask extends Task {
    private final int epicId;


    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, Status status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicId = subtask.getEpicId();
    }


    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toCsvString() {
        return super.toCsvString() + epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }
}
