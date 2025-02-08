package ru.practicum.kanban;

import ru.practicum.kanban.model.Epic;
import ru.practicum.kanban.model.Status;
import ru.practicum.kanban.model.Subtask;
import ru.practicum.kanban.model.Task;
import ru.practicum.kanban.service.FileBackedTaskManager;
import ru.practicum.kanban.service.Managers;
import ru.practicum.kanban.service.TaskManager;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // добавить 2 задачи
        Task eatCat = new Task("Накормить кота", "Дать коту сухого корма");
        manager.create(eatCat);
        Task drinkCat = new Task("Напоить кота", "Налить коту фильтрованной воды");
        manager.create(drinkCat);

        // добавить эпик и 2 подзадачи
        Epic goShop = new Epic("Сходить в магазин", "Сходить в \"Пятёрочку\" на углу");
        manager.create(goShop);
        Subtask buyBread = new Subtask(
                "Купить хлеба", "Купить буханку чёрного хлеба и половинку батона", goShop.getId());
        manager.create(buyBread);
        Subtask buyButter = new Subtask(
                "Купить масло", "Купить пачку сливочного масла 82,5%", goShop.getId());
        manager.create(buyButter);

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
        buyBread.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(buyBread);
        wash.setStatus(Status.DONE);
        manager.updateSubtask(wash);

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после изменения статусов:", manager);

        // распечатать пустую историю
        printHistory("Пустая история просмотров задач:", manager);

        // посмотреть задачи
        manager.getTask(eatCat.getId());
        manager.getTask(drinkCat.getId());
        manager.getEpic(goShop.getId());
        manager.getSubtask(buyBread.getId());
        manager.getSubtask(buyButter.getId());
        manager.getEpic(washPlates.getId());
        manager.getEpic(washPlates.getId());
        manager.getEpic(washPlates.getId());
        manager.getSubtask(wash.getId());
        manager.getSubtask(wash.getId());
        manager.getSubtask(wash.getId());
        manager.getSubtask(wash.getId());

        // распечатать историю
        printHistory("История просмотров задач:", manager);

        // удалить задачу/эпик
        manager.deleteTask(drinkCat.getId());
        manager.deleteEpic(washPlates.getId());

        manager.deleteSubtask(buyBread.getId());

        // распечатать Эпики, Задачи, Подзадачи
        print("Состояние задач после удаления:", manager);

        // дополнительное задание 6 спринта (покрыто unit-тестами ¯\_(ツ)_/¯ )
        Task makeProject = new Task("Сделать проект", "Выполнить проект 6 спринта");
        manager.create(makeProject);

        Epic cookDinner = new Epic("Приготовить ужин", "Приготовить жареный картофель");
        manager.create(cookDinner);

        Subtask buySweets = new Subtask("Купить конфет", "Купить килограмм конфет", goShop.getId());
        manager.create(buySweets);
        Subtask buyMilk = new Subtask("Купить молока", "Купить молоко 1,5%", goShop.getId());
        manager.create(buyMilk);

        manager.getTask(eatCat.getId());
        manager.getTask(makeProject.getId());
        manager.getEpic(cookDinner.getId());
        manager.getTask(eatCat.getId());
        manager.getSubtask(buySweets.getId());
        manager.getSubtask(buyMilk.getId());
        manager.deleteTask(eatCat.getId());
        manager.deleteTask(makeProject.getId());
        printHistory("История просмотров задач без дубликатов:", manager);

        manager.deleteEpic(goShop.getId());
        printHistory("История просмотров задач без эпика goShop и его подзадач:", manager);

        // проверка задания 7 спринта (покрыто unit-тестами ¯\_(ツ)_/¯ )
        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(Paths.get(".\\resources\\backup.csv"));
        print("Состояние менеджера восстановленного из пустого файла:", fbtm);

        Task testTask = new Task("Тестовая задача", "Тестовая задача 7 спринта");
        fbtm.create(testTask);
        fbtm.deleteTask(testTask.getId());

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

    private static void printHistory(String title, TaskManager manager) {
        System.out.println(title);
        System.out.println("-".repeat(120));
        manager.getHistory().forEach(System.out::println);
        System.out.println();
    }
}
