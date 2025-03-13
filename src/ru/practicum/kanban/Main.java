package ru.practicum.kanban;

import ru.practicum.kanban.server.HttpTaskServer;

public class Main {
    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
