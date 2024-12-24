package ru.practicum.kanban;

import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

public abstract class BaseTest {

    protected TaskManager manager = Managers.getDefault();

}
