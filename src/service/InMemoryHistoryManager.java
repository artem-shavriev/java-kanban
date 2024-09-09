package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList = new ArrayList<>();
    private Set<Task> historySet = new HashSet<>();

    @Override
    public void add(Task task) {
        historySet.addAll(historyList);
        boolean isPresent = historySet.add(task);
        if (!isPresent) {
            historyList.remove(task);
            historyList.add(task);
        } else {
            historyList.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void remove(Task task) {
        historyList.remove(task);
    }
}
//Текущий комит:
//Сделать историю посещений неограниченной по размеру.
//Избавиться от повторных просмотров в истории.
//Добавить метод removeId для удаления из истории просмотров при удалении задач.