package ru.practicum.kanban.service;

import ru.practicum.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс {@code InMemoryHistoryManager} реализует интерфейс {@code HistoryManager} и
 * обеспечивает хранение истории просмотренных задач в {@code HashMap<Integer, Node>}
 * значение {@code Node} которой содержит узел двусвязного списка.
 *
 * @author  Smirnov Sergey
 */
public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;

    private Node first;
    private Node last;


    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }


    //--- Пометить задачу как просмотренную ----------------------------------------------------------------------------
    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) remove(task.getId());
            linkLast(task);
            history.put(task.getId(), last);
        }
    }

    //--- Удалить пометку о просмотре задачи ---------------------------------------------------------------------------
    @Override
    public void remove(Integer id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    //--- Просмотр истории (последние 10 просмотренных задач) ----------------------------------------------------------
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    /* Реализация LinkedList */

    //--- Добавить узел в конец списка ---------------------------------------------------------------------------------
    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;

        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
    }

    //--- Вернуть список всех задач из списка --------------------------------------------------------------------------
    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();

        for (Node x = first; x != null; x = x.next) {
            result.add(x.item);
        }

        return result;
    }

    //--- Удалить узел -------------------------------------------------------------------------------------------------
    private void removeNode(Node node) {
        node.item = null;
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }

    //--- Реализация узла двусвязного списка задач ---------------------------------------------------------------------
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
