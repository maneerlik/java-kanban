package ru.practicum.kanban.model;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksIds;
    private Instant endTime;


    public Epic(String title, String description) {
        super(title, description);
        subtasksIds = new ArrayList<>();
    }

    public Epic(int id, String title, Status status, String description, Instant startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
        subtasksIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        subtasksIds = new ArrayList<>(epic.subtasksIds);
        endTime = epic.endTime;
    }


    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void clearEpicsSubtasks() {
        subtasksIds.clear();
    }

    public void evaluateStatus(List<Subtask> subtasks) {
        boolean isAnySubtaskInProgress = subtasks.stream()
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);

        if (isAnySubtaskInProgress) {
            setStatus(Status.IN_PROGRESS);
            return;
        }

        boolean isSubtasksIdsIsEmpty = subtasksIds.isEmpty();

        boolean isAllSubtaskNEW = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.NEW);

        boolean isAllSubtaskDONE = subtasks.stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (isSubtasksIdsIsEmpty || isAllSubtaskDONE) {
            setStatus(Status.DONE);
        } else if (isAllSubtaskNEW) {
            setStatus(Status.NEW);
        }
    }

    public void calculateStartTime(List<Subtask> subtasks) {
        if (subtasksIds.isEmpty()) return;

        Instant startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Instant::compareTo)
                .orElse(null);

        setStartTime(startTime);
    }

    public void calculateDuration() {
        Instant startTime = getStartTime();
        Instant endTime = getEndTime();

        if (startTime == null || endTime == null) return;

        setDuration(Duration.between(startTime, endTime));
    }

    public void calculateEndTime(List<Subtask> subtasks) {
        if (subtasksIds.isEmpty()) return;

        endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }
}
