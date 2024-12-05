package ru.practicum.kanban.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksIds = new ArrayList<>();


    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(Epic epic) {
        super(epic);
    }


    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void clearEpicsSubtasks() {
        subtasksIds.clear();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
