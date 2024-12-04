package ru.practicum.kanban;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // добавить 2 задачи
        Task task_1 = new Task("Накормить кота", "Дать коту сухого корма");
        manager.create(task_1);
        Task task_2 = new Task("Напоить кота", "Налить коту фильтрованной воды");
        manager.create(task_2);

        // добавить эпик и 2 подзадачи
        Epic epic_1 = new Epic("Сходить в магазин", "Сходить в \"Пятёрочку\" на углу");
        manager.create(epic_1);
        Subtask subtask_1 = new Subtask(
                "Купить хлеба", "Купить буханку чёрного хлеба и половинку батона", epic_1.getId());
        manager.create(subtask_1);
        Subtask subtask_2 = new Subtask(
                "Купить масло", "Купить пачку сливочного масла 82,5%", epic_1.getId());
        manager.create(subtask_2);

        // добавить эпик и 1 подзадачу
        Epic epic_2 = new Epic("Помыть посуду", "Помыть посуду в раковинне и на столе");
        manager.create(epic_2);
        Subtask subtask_3 = new Subtask("Собрать и помыть посуду",
                "Собрать посуду со стола в раковинну и все помыть", epic_2.getId());
        manager.create(subtask_3);

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после создания:", manager);

        // изменить статусы задач и подзадач
        task_1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task_1);
        task_2.setStatus(Status.DONE);
        manager.updateTask(task_2);
        subtask_1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask_1);
        subtask_3.setStatus(Status.DONE);
        manager.updateSubtask(subtask_3);

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после изменения статусов:", manager);

        // удалить задачу/эпик
        manager.deleteTask(task_2.getId());
        manager.deleteEpic(epic_2.getId());

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после удаления:", manager);
    }

    private static void print(String title, TaskManager manager) {
        System.out.println(title);
        System.out.println("-".repeat(120));
        manager.getAllTasks().forEach(System.out::println);
        System.out.println();
        manager.getAllEpics().forEach(System.out::println);
        System.out.println();
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println();
    }
}
