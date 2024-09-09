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
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        historyManager.add(task);

        Epic epic = new Epic(1,"Сделать ремонт", "Покрасить стены на балконе");
        historyManager.add(epic);

        Subtask subtask = new Subtask(10, 1,"Купить шпатель",
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
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        historyManager.add(task);
        assertNotNull(historyManager.getHistory().get(0), "История не получгена.");
    }

    @Test
    void shouldCheckingHistoryLength() {
        int maxHistorySize = 10;
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        for (int i = 0; i <= 15; i++) {
            historyManager.add(task);
        }
        int historyListSize = historyManager.getHistory().size();
        assertTrue(maxHistorySize >= historyListSize, "В историю добалвено более 10 задач");
    }
}
