package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldAddTask() {
        Task task = new Task(234,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        manager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotEquals(234, taskId, "id не изменился на сгенерированный при добавлении.");

    }

    @Test
    void shouldAddEpic() {
        Epic epic = new Epic(354,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);

        final int epicId = epic.getId();
        final Epic savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertNotEquals(354, epicId, "id не изменился на сгенерированный при добавлении.");
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(10, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = manager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        assertNotEquals(10, subtaskId, "id не изменился на сгенерированный при добавлении.");

    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);

        manager.addTask(task);
        int taskId = task.getId();

        Task taskForUpdating = new Task(taskId,"Уборка в доме",
                "Протереть пыль",  TaskStatus.DONE);
        manager.updateTask(taskForUpdating);

        assertEquals(taskForUpdating, manager.getTaskById(taskId), "Задача не обновилась.");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путешествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Epic epicForUpdating = new Epic(epicId,"Поехать в путешествие", "Составить план");
        manager.updateEpic(epicForUpdating);

        assertEquals(epicForUpdating, manager.getEpicById(epicId), "Эпик не обновилася.");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(10, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        int subtaskId = subtask.getId();

        Subtask subtaskForUpdating = new Subtask(subtaskId, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);

    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);

        manager.addTask(task);
        int taskId = task.getId();

        assertNotNull(manager.getTaskById(taskId), "Задача не возвращается");
        assertEquals(task, manager.getTaskById(taskId), "Получена другая задача.");
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путешествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        assertNotNull(manager.getEpicById(epicId), "Эпик не возвращается.");
        assertEquals(epic, manager.getEpicById(epicId), "Получен другой эпик.");
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(10, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        int subtaskId = subtask.getId();

        assertNotNull(manager.getSubtaskById(subtaskId), "Подзадача не возвращается.");
        assertEquals(subtask, manager.getSubtaskById(subtaskId), "Получена другая подзадача.");
    }

    @Test
    void shouldGetTasks() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);

        manager.addTask(task);

        final ArrayList<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEpics() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);

        ArrayList<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldGetSubtasks() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(10, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        ArrayList<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldGetSubtasksOfEpic() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(10, epicId,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        manager.addSubtask(subtask);

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        ArrayList<Subtask> subtasksOfEpic = manager.getSubtasksOfEpic(epicId);

        assertNotNull(subtasksOfEpic, "Список подзадач эпика не возвращается.");
        assertEquals(subtasks.get(0), subtasksOfEpic.get(0), "Подзадачи эпика не совпадают.");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);

        manager.addTask(task);
        int taskId = task.getId();
        manager.removeTaskById(taskId);

        assertNull(manager.getTaskById(taskId), "Задача не удалилась.");
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();
        manager.removeEpicById(epicId);

        assertNull(manager.getEpicById(epicId));
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(10, epicId,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.NEW);

        manager.addSubtask(subtask);
        int subtaskId = subtask.getId();
        manager.removeSubtaskById(subtaskId);

        assertNull(manager.getSubtaskById(subtaskId));
    }

    @Test
    void shouldRemoveTasks() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);

        manager.addTask(task);
        assertEquals(task, manager.getTasks().get(0));

        manager.removeTasks();
        assertTrue(manager.getTasks().isEmpty(), "Задачи не удалились.");
    }

    @Test
    void shouldRemoveEpics() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(10, epicId,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.NEW);

        manager.addSubtask(subtask);

        assertEquals(epic, manager.getEpics().get(0));

        manager.removeEpics();
        assertTrue(manager.getEpics().isEmpty(), "Эпики не удалились.");

        assertFalse(manager.getSubtasks().contains(subtask), "После удаления эпика не удалилсь его подзадачи.");
    }

    @Test
    void shouldRemoveSubtasks() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(10, epicId,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.NEW);
        int subtaskId = subtask.getId();

        manager.addSubtask(subtask);
        assertEquals(subtask, manager.getSubtasks().get(0));

        manager.removeSubtasks();
        assertTrue(manager.getSubtasks().isEmpty(), "Подзадачи не удалились.");

        ArrayList<Integer> subtasksIdsOfEpic = epic.getSubtasksIds();
        assertFalse(subtasksIdsOfEpic.contains(subtaskId),
                "id удаленной подзадачи не удалилось из спика подзадач эпика");
    }

    @Test
    void shouldUpdateEpicStatus() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask(10, epicId,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(11, epicId,"Загазать билеты",
                "Выбрать самые выгодные билеты", TaskStatus.DONE);

        manager.addSubtask(subtask1);
        assertEquals(epic.getTaskStatus(), subtask1.getTaskStatus(), "Некорректный статус эпика.");

        manager.addSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS, "Некорректный статус эпика.");

        int subtask1Id = subtask1.getId();
        manager.removeSubtaskById(subtask1Id);
        assertEquals(epic.getTaskStatus(), subtask2.getTaskStatus(), "Некорректный статус эпика.");
    }

    @Test
    void shouldGetHistoryFromHistoryManager() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        manager.addTask(task);
        int taskId = task.getId();
        Epic epic = new Epic("Поехать в отпуск", "Организовать путишествие");
        manager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask(10, epicId,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.NEW);
        manager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        manager.getTaskById(taskId);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);
        ArrayList<Task> history = (ArrayList<Task>) manager.getHistory();

        assertEquals(history.get(0), task, "Задача не попала в историю.");
        assertEquals(history.get(1), epic, "Эпик не попал в историю.");
        assertEquals(history.get(2), subtask, "Подзадача не попала в историю.");

    }
    @Test
    void immutabilityOfTheTask() {
        Task task = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        manager.addTask(task);
        int taskId = task.getId();

        String receivedNameOfTask = manager.getTaskById(taskId).getNameOfTask();
        String receivedDescription = manager.getTaskById(taskId).getDescription();
        TaskStatus receivedTaskStatus = manager.getTaskById(taskId).getTaskStatus();

        assertEquals(task.getNameOfTask(), receivedNameOfTask, "Поля имени задач не совпадают.");
        assertEquals(task.getDescription(), receivedDescription, "Поля описания задач не совпадают.");
        assertEquals(task.getTaskStatus(), receivedTaskStatus, "Поля статуса задач не совпадают.");
    }

    @Test
    void shouldSavePreviousVersionOfTask() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        Task updateTask = new Task(1,"Готовка", "Приготовить еду",  TaskStatus.NEW);

        manager.addTask(task);
        manager.getTaskById(task.getId());

        Task taskFromHistory = manager.getHistory().get(0);

        assertEquals(task, taskFromHistory);
        assertEquals(task.getNameOfTask(), taskFromHistory.getNameOfTask());
        assertEquals(task.getDescription(), taskFromHistory.getDescription());
        assertEquals(task.getTaskStatus(), taskFromHistory.getTaskStatus());

        manager.updateTask(updateTask);
        assertEquals(task, taskFromHistory);
        assertEquals(task.getNameOfTask(), taskFromHistory.getNameOfTask());
        assertEquals(task.getDescription(), taskFromHistory.getDescription());
        assertEquals(task.getTaskStatus(), taskFromHistory.getTaskStatus());
    }

    @Test
    void shouldNotAddSubtaskWithSimilarEpicId() {
        Epic epic = new Epic("Поехать в отпуск", "Организовать путешествие");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(epicId, epicId,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        manager.addSubtask(subtask);

        int subtaskId = subtask.getId();
        int epicIdOfSubtask = manager.getSubtaskById(subtaskId).getEpicId();

        assertNotEquals(subtaskId, epicIdOfSubtask, "id подзадачи совпадает с ее epicId.");

    }

    @Test
    void ShouldNotBeIrrelevantSubtaskIdsInEpic() {
        Epic epic = new Epic(1,"Поехать в отпуск", "Организовать путешествие");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, 1,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(3, 1,"Купить краску",
                "Выбрать краску и купить", TaskStatus.DONE);
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask(4, 1,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.IN_PROGRESS);
        manager.addSubtask(subtask3);

        ArrayList<Integer> subtasksIds = manager.getEpicById(1).getSubtasksIds();

        manager.removeSubtaskById(3);
        assertFalse(subtasksIds.contains(3), "Id подзадачи не удалился");

        assertTrue(subtasksIds.contains(2), "Id подзадачи удалился или не был добавлен.");
        assertTrue(subtasksIds.contains(4), "Id подзадачи удалился или не был добавлен.");

        manager.removeSubtasks();
        assertFalse(subtasksIds.contains(2), "Id подзадач не удалились");
        assertFalse(subtasksIds.contains(4), "Id подзадач не удалились");
    }
}