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
        Task eatCat = new Task("Накормить кота", "Дать коту сухого корма");
        manager.create(eatCat);
        Task drinkCat = new Task("Напоить кота", "Налить коту фильтрованной воды");
        manager.create(drinkCat);

        // добавить эпик и 2 подзадачи
        Epic goShop = new Epic("Сходить в магазин", "Сходить в \"Пятёрочку\" на углу");
        manager.create(goShop);
        Subtask byBread = new Subtask(
                "Купить хлеба", "Купить буханку чёрного хлеба и половинку батона", goShop.getId());
        manager.create(byBread);
        Subtask byButter = new Subtask(
                "Купить масло", "Купить пачку сливочного масла 82,5%", goShop.getId());
        manager.create(byButter);

        // добавить эпик и 1 подзадачу
        Epic washPlates = new Epic("Помыть посуду", "Помыть посуду в раковинне и на столе");
        manager.create(washPlates);
        Subtask wash = new Subtask("Собрать и помыть посуду",
                "Собрать посуду со стола в раковинну и все помыть", washPlates.getId());
        manager.create(wash);

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после создания:", manager);

        // изменить статусы задач и подзадач
        eatCat.setStatus(Status.IN_PROGRESS);
        manager.updateTask(eatCat);
        drinkCat.setStatus(Status.DONE);
        manager.updateTask(drinkCat);
        byBread.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(byBread);
        wash.setStatus(Status.DONE);
        manager.updateSubtask(wash);

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после изменения статусов:", manager);

        // удалить задачу/эпик
        manager.deleteTask(drinkCat.getId());
        manager.deleteEpic(washPlates.getId());

        manager.deleteSubtask(byBread.getId());

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
