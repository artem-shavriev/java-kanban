package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTests {
    HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldAddTaskToHistoryList() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2,"Сделать ремонт", "Покрасить стены на балконе");
        historyManager.add(epic);

        Subtask subtask = new Subtask(10, 2,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        historyManager.add(subtask);

        assertNotNull(historyManager.getHistory().get(0), "Задача не добавилась в историю.");
        assertEquals(task, historyManager.getHistory().get(0), "Задачи не совпадают");

        assertNotNull(historyManager.getHistory().get(1), "Эпик не добавился в историю.");
        assertEquals(epic, historyManager.getHistory().get(1), "Эпики не совпадают");

        assertNotNull(historyManager.getHistory().get(2), "Подзадачи не добавилась в историю.");
        assertEquals(subtask, historyManager.getHistory().get(2), "Подзадачи не совпадают");
    }

    @Test
    void shouldGetHistory() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        historyManager.add(task);
        assertNotNull(historyManager.getHistory().get(0), "История не получена.");
    }

    @Test
    void shouldNotRepeatElementInHistory() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        Epic epic = new Epic(2,"Сделать ремонт", "Покрасить стены на балконе");
        historyManager.add(epic);
        historyManager.add(epic);

        Subtask subtask = new Subtask(10, 2,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        historyManager.add(subtask);
        historyManager.add(subtask);

        Subtask subtask2 = new Subtask(11, 2,"Купить краску",
                "Выбрать краску и купить", TaskStatus.DONE);
        historyManager.add(subtask2);

        assertNotEquals(historyManager.getHistory().get(0), historyManager.getHistory().get(1),
                "Задачи в истории не повторяются.");
        assertNotEquals(historyManager.getHistory().get(1), historyManager.getHistory().get(2),
                "Эпики в истории не повторяются.");
        assertNotEquals(historyManager.getHistory().get(2), historyManager.getHistory().get(3),
                "Эпики в истории не повторяются.");

    }

    @Test
    public void shouldRemoveFromHistory() {
        Task task = new Task(1, "Уборка", "Собрать и вынести мусор", TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(2, "Сделать ремонт", "Покрасить стены на балконе");
        historyManager.add(epic);

        historyManager.remove(1);
        assertNotEquals(historyManager.getHistory().get(0), task, "Задача id:1 не удалена");
    }
}
