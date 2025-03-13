package ru.practicum.kanban;

import ru.practicum.kanban.server.HttpTaskServer;
import ru.practicum.kanban.service.Managers;

public class Main {
    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
