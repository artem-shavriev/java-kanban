package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    HashMap<Integer, Node<Task>> historyHashMap = new HashMap<>();
    MyLinkedList<Task> historyList = new MyLinkedList<>();
    ArrayList<Task> list = new ArrayList<>();

    public static class MyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public void linkLast(T obj) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, obj, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
                size++;
            }
        }
    }

    public void removeNode(Node<Task> node) {
        if (node != null) {
            if (node.prev == null) {
                if (node.next == null) {
                    System.out.println("Узел удален");
                } else {
                    historyList.head = node.next;
                }
            } else {
                if (node.next != null) {
                    node.prev.next = node.next.prev;
                }
            }

            if (node.next == null) {
                if (node.prev == null) {
                    System.out.println("Узел удален");
                } else {
                    historyList.tail = node.prev;
                }
            } else {
                if (node.prev != null) {
                    node.next.prev = node.prev.next;
                }
            }

            node.data = null;
            historyList.size--;
        }
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();

        if (historyHashMap.containsKey(taskId)) {
            Node nodeForRemove = historyHashMap.get(taskId);
            removeNode(nodeForRemove);
            historyHashMap.remove(taskId);
        }
        historyList.linkLast(task);
        historyHashMap.put(taskId, historyList.tail);
    }

    @Override
    public List<Task> getHistory() {
        for (Node<Task> node : historyHashMap.values()) {
            list.add(node.data);
        }
        return list;
    }

    @Override
    public void remove(int id) {
        Node nodeForRemove = historyHashMap.get(id);
        removeNode(nodeForRemove);
        historyHashMap.remove(id);
    }
}
