package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTests {

    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        File backedFile;
        {
            try {
                backedFile = File.createTempFile("backedFile", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        manager = Managers.getDefault(backedFile);
    }

    @Test
    void shouldAddTask() {
        Task task = new Task(234,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void shouldAddEpic() {
        Epic epic = new Epic(354,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);

        final int epicId = epic.getId();
        final Epic savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldAddSubtask() {
        Epic epic = new Epic(1,"Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45),1);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        final int subtaskId = subtask.getId();
        final Subtask savedSubtask = manager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task(1,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

        manager.addTask(task);
        int taskId = task.getId();

        Task taskForUpdating = new Task(taskId,"Готовка",  TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));
        manager.updateTask(taskForUpdating);

        assertEquals(taskForUpdating, manager.getTaskById(taskId), "Задача не обновилась.");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic(323,"Поехать в отпуск", "Организовать путешествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Epic epicForUpdating = new Epic(epicId,"Поехать в путешествие", "Составить план");
        manager.updateEpic(epicForUpdating);

        assertEquals(epicForUpdating, manager.getEpicById(epicId), "Эпик не обновилася.");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic(53,"Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45),53);

        manager.addEpic(epic);
        manager.addSubtask(subtask);

        int subtaskId = subtask.getId();

        Subtask subtaskForUpdating = new Subtask(subtaskId, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить", 1);

    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task(1443,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

        manager.addTask(task);
        int taskId = task.getId();

        assertNotNull(manager.getTaskById(taskId), "Задача не возвращается");
        assertEquals(task, manager.getTaskById(taskId), "Получена другая задача.");
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic(532,"Поехать в отпуск", "Организовать путешествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        assertNotNull(manager.getEpicById(epicId), "Эпик не возвращается.");
        assertEquals(epic, manager.getEpicById(epicId), "Получен другой эпик.");
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic(5214,"Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45),5214);


        manager.addEpic(epic);
        manager.addSubtask(subtask);

        int subtaskId = subtask.getId();

        assertNotNull(manager.getSubtaskById(subtaskId), "Подзадача не возвращается.");
        assertEquals(subtask, manager.getSubtaskById(subtaskId), "Получена другая подзадача.");
    }

    @Test
    void shouldGetTasks() {
        Task task = new Task(1443,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

        manager.addTask(task);

        final ArrayList<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEpics() {
        Epic epic = new Epic(5,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);

        ArrayList<Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void shouldGetSubtasks() {
        Epic epic = new Epic(51,"Поехать в отпуск", "Организовать путишествие");
        Subtask subtask = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45),51);


        manager.addEpic(epic);
        manager.addSubtask(subtask);

        ArrayList<Subtask> subtasks = manager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldGetSubtasksOfEpic() {
        Epic epic = new Epic(124,"Поехать в отпуск", "Организовать путишествие");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45),epicId);

        manager.addSubtask(subtask);

        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);

        ArrayList<Subtask> subtasksOfEpic = manager.getSubtasksOfEpic(epicId);

        assertNotNull(subtasksOfEpic, "Список подзадач эпика не возвращается.");
        assertEquals(subtasks.get(0), subtasksOfEpic.get(0), "Подзадачи эпика не совпадают.");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task = new Task(1,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

        manager.addTask(task);
        int taskId = task.getId();
        manager.removeTaskById(taskId);

        assertNull(manager.getTaskById(taskId), "Задача не удалилась.");
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic = new Epic(73,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();
        manager.removeEpicById(epicId);

        assertNull(manager.getEpicById(epicId));
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic = new Epic(8,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(8,"Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 14, 30,00),
                Duration.ofMinutes(45),epicId);

        manager.addSubtask(subtask);
        int subtaskId = subtask.getId();
        manager.removeSubtaskById(subtaskId);

        assertNull(manager.getSubtaskById(subtaskId));
    }

    @Test
    void shouldRemoveTasks() {
        Task task = new Task(2,"Готовка",  TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

        manager.addTask(task);
        assertEquals(task, manager.getTasks().get(0));

        manager.removeTasks();
        assertTrue(manager.getTasks().isEmpty(), "Задачи не удалились.");
    }

    @Test
    void shouldRemoveEpics() {
        Epic epic = new Epic(44,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(8,"Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 14, 30,00),
                Duration.ofMinutes(45),epicId);

        manager.addSubtask(subtask);

        assertEquals(epic, manager.getEpics().get(0));

        manager.removeEpics();
        assertTrue(manager.getEpics().isEmpty(), "Эпики не удалились.");

        assertFalse(manager.getSubtasks().contains(subtask), "После удаления эпика не удалилсь его подзадачи.");
    }

    @Test
    void shouldRemoveSubtasks() {
        Epic epic = new Epic(84,"Поехать в отпуск", "Организовать путишествие");

        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(8,"Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 14, 30,00),
                Duration.ofMinutes(45),epicId);
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
        Epic epic = new Epic(363,"Поехать в отпуск", "Организовать путишествие");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask1 = new Subtask(10, "Выбрать курорт", TaskStatus.NEW,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30,00),
                Duration.ofMinutes(45), epicId);
        Subtask subtask2 = new Subtask(11, "Заказать билеты", TaskStatus.NEW,
                "Выбрать лучшуу цену билетов и заказать",
                LocalDateTime.of(2024, 10,2, 15, 30,00),
                Duration.ofMinutes(45),epicId);
        Subtask subtask3 = new Subtask(3, "Заказать билеты", TaskStatus.DONE,
                "Выбрать лучшуу цену билетов и заказать",
                LocalDateTime.of(2024, 10,3, 15, 30,00),
                Duration.ofMinutes(45),epicId);
        Subtask subtask4 = new Subtask(4, "Заказать билеты", TaskStatus.DONE,
                "Выбрать лучшуу цену билетов и заказать",
                LocalDateTime.of(2024, 10,4, 15, 30,00),
                Duration.ofMinutes(45),epicId);

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        assertEquals(epic.getTaskStatus(), TaskStatus.NEW, "Некорректный статус эпика.");

        manager.removeSubtaskById(subtask1.getId());
        manager.removeSubtaskById(subtask2.getId());
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask4);
        assertEquals(epic.getTaskStatus(), TaskStatus.DONE, "Некорректный статус эпика.");

        subtask3.setTaskStatus(TaskStatus.NEW);
        manager.updateSubtask(subtask3);
        assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS, "Некорректный статус эпика.");

        subtask3.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtask4.setTaskStatus(TaskStatus.IN_PROGRESS);
        assertEquals(epic.getTaskStatus(), TaskStatus.IN_PROGRESS, "Некорректный статус эпика.");

    }

    @Test
    void shouldGetHistoryFromHistoryManager() {
        Task task = new Task(3633,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30),
                Duration.ofMinutes(45));
        manager.addTask(task);
        int taskId = task.getId();
        Epic epic = new Epic(33,"Поехать в отпуск", "Организовать путишествие");
        manager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask(10, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 15, 30),
                Duration.ofMinutes(45),epicId);
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
        Task task = new Task(25,"Готовка",  TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));
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
        Task task = new Task(1,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));
        Task updateTask = new Task(1,"Готовка",  TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));

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
        Epic epic = new Epic(22,"Поехать в отпуск", "Организовать путешествие");
        manager.addEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(epicId, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",epicId);
        manager.addSubtask(subtask);

        assertNull(manager.getSubtaskById(epicId), "Подзадача с id идентичным id ее эпика была добавлена.");

    }

    @Test
    void ShouldNotBeIrrelevantSubtaskIdsInEpic() {
        Epic epic = new Epic(1,"Поехать в отпуск", "Организовать путешествие");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),1);
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(3, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,1, 13, 30,00),
                Duration.ofMinutes(45),1);
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask(4,"Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,1, 14, 30,00),
                Duration.ofMinutes(45),1);
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

    @Test
    void shouldSortTasksByTime() {
        Task task = new Task(1, "Уборка 2.10.2024 12:30", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));
        Task task2 = new Task(2, "Готовка 2.10.2024 11:30", TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10, 2 , 11, 30, 00),
                Duration.ofMinutes(45));
        Task task3 = new Task(3, "Готовка 3.10.2024 11:30", TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10, 3 , 11, 30, 00),
                Duration.ofMinutes(45));
        Task task4 = new Task(4, "Уборка 3.10.2024 12:30", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 3, 12, 30, 00),
                Duration.ofMinutes(45));
        Epic epic = new Epic(5,"Поехать в отпуск", "Организовать путешествие");
        Subtask subtask1 = new Subtask(6, "Купить шпатель 11.10.2024 12:30", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,11, 12, 30,00),
                Duration.ofMinutes(45),5);
        Subtask subtask2 = new Subtask(7, "Купить краску 10.10.2024 13:30", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,10, 13, 30,00),
                Duration.ofMinutes(45),5);
        Subtask subtask3 = new Subtask(8,"Выбрать курорт 9.10.2024 14:30", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,9, 14, 30,00),
                Duration.ofMinutes(45),5);

        manager.addTask(task);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task4);
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        assertEquals(task, manager.getPrioritizedTask().get(1), "Сортировка неудалась.");
        assertEquals(task2, manager.getPrioritizedTask().get(0), "Сортировка неудалась.");
        assertEquals(task3, manager.getPrioritizedTask().get(2), "Сортировка неудалась.");
        assertEquals(task4, manager.getPrioritizedTask().get(3), "Сортировка неудалась.");
        assertEquals(subtask1, manager.getPrioritizedTask().get(6), "Сортировка неудалась.");
        assertEquals(subtask2, manager.getPrioritizedTask().get(5), "Сортировка неудалась.");
        assertEquals(subtask3, manager.getPrioritizedTask().get(4), "Сортировка неудалась.");
    }

    @Test
    void shouldNotIntersection() {
        Task task = new Task(1, "Уборка 2.10.2024 12:30", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30),
                Duration.ofMinutes(45));
        Task task2 = new Task(2, "Готовка 2.10.2024 11:30", TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10, 2 , 11, 30),
                Duration.ofMinutes(120));
        Epic epic = new Epic(5,"Поехать в отпуск", "Организовать путешествие");
        Subtask subtask1 = new Subtask(6, "Купить шпатель 2.10.2024 12:30", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,2, 10, 30),
                Duration.ofMinutes(180),5);
        Subtask subtask2 = new Subtask(7, "Купить краску 10.10.2024 13:30", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,10, 13, 30),
                Duration.ofMinutes(45),5);
        Subtask subtask3 = new Subtask(8,"Выбрать курорт 10.10.2024 13:20", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,10, 13, 35),
                Duration.ofMinutes(5),5);

        manager.addTask(task);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        assertEquals(task, manager.getPrioritizedTask().get(0));
        assertEquals(subtask2, manager.getPrioritizedTask().get(1));
    }

    @Test
    void shouldUpdateEpicStartAndEndTime() {
        Epic epic = new Epic(5,"Поехать в отпуск", "Организовать путешествие");
        Subtask subtask1 = new Subtask(6, "Купить шпатель 2.10.2024 10:30", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,2, 10, 30),
                Duration.ofMinutes(30),5);
        Subtask subtask2 = new Subtask(7, "Купить краску 3.10.2024 13:30", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,2, 13, 30),
                Duration.ofMinutes(30),5);
        Subtask subtask3 = new Subtask(8,"Выбрать курорт 4.10.2024 13:20", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,2, 16, 30),
                Duration.ofMinutes(30),5);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        assertEquals(epic.getStartTime(), LocalDateTime.of(2024, 10,2, 10, 30),
                "Врема начала эпика не установлено.");
        assertEquals(epic.getEndTime(), subtask3.getEndTime(),
                "Врема конца эпика не установлено.");
    }

    @Test
    void shouldUpdateEpicDuration() {
        Epic epic = new Epic(5,"Поехать в отпуск", "Организовать путешествие");
        Subtask subtask1 = new Subtask(6, "Купить шпатель 2.10.2024 10:30", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,2, 10, 30),
                Duration.ofMinutes(30),5);
        Subtask subtask2 = new Subtask(7, "Купить краску 3.10.2024 13:30", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,2, 13, 30),
                Duration.ofMinutes(30),5);
        Subtask subtask3 = new Subtask(8,"Выбрать курорт 4.10.2024 13:20", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10,2, 16, 30),
                Duration.ofMinutes(30),5);

        manager.addEpic(epic);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        Duration durationOfAllTasks = subtask1.getDuration().plus(subtask2.getDuration().plus(subtask3.getDuration()));

        assertEquals(epic.getDuration(), durationOfAllTasks, "Продолжительность эпика не обновилась.");

        manager.removeSubtaskById(8);
        manager.removeSubtaskById(7);

        assertEquals(epic.getDuration(), subtask1.getDuration(), "Продолжительность эпика не обновилась.");
    }
}