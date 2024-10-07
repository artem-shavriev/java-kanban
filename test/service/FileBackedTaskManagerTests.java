package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.BufferedReader;
import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTests extends TaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;
    File backedFile;

    {
        try {
            backedFile = File.createTempFile("backedFile", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = new FileBackedTaskManager(backedFile);
    }

    @Test
    void shouldReturnTaskToString() {
        Task task = new Task(1, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2, "Сделать ремонт", TaskStatus.NEW, "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10, 1, 13, 30, 00),
                Duration.ofMinutes(45), 2);
        fileBackedTaskManager.addSubtask(subtask);

        String taskToString = "1,TASK,Уборка,NEW,Собрать и вынести мусор,2024-10-02T12:30,45";
        String epicToString = "2,EPIC,Сделать ремонт,DONE,Покрасить стены на балконе";
        String subtaskToString = "3,SUBTASK,Купить краску,DONE,Выбрать краску и купить,2024-10-01T13:30,45,2";

        assertEquals(taskToString, TaskConvertor.convertTaskToString(task),
                "Неверный вывод taskToString");
        assertEquals(epicToString, TaskConvertor.convertTaskToString(epic),
                "Неверный вывод epicToString");
        assertEquals(subtaskToString, TaskConvertor.convertTaskToString(subtask),
                "Неверный вывод subtaskToString");
    }

    @Test
    void shouldReturnTaskFromString() {
        Task task = new Task(1, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));

        Epic epic = new Epic(2, "Сделать ремонт", TaskStatus.NEW, "Покрасить стены на балконе");

        Subtask subtask = new Subtask(3, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10, 1, 13, 30, 00),
                Duration.ofMinutes(45), 2);

        String taskString = "1,TASK,Уборка,NEW,Собрать и вынести мусор,2024-10-02T12:30,45";
        String epicString = "2,EPIC,Сделать ремонт,NEW,Покрасить стены на балконе";
        String subtaskString = "3,SUBTASK,Купить краску,DONE,Выбрать краску и купить,2024-10-01T13:30,45,2";

        assertEquals(TaskConvertor.convertTaskFromString(taskString), task);
        assertEquals(TaskConvertor.convertTaskFromString(epicString), epic);
        assertEquals(TaskConvertor.convertTaskFromString(subtaskString), subtask);
    }

    @Test
    void shouldSaveTasksInBackedFile() {
        Task task = new Task(1, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2, "Сделать ремонт", TaskStatus.NEW, "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10, 1, 13, 30, 00),
                Duration.ofMinutes(45), 2);
        fileBackedTaskManager.addSubtask(subtask);

        ArrayList<String> array = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileBackedTaskManager.backedFile))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                array.add(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(TaskConvertor.convertTaskFromString(array.get(0)), task);
        assertEquals(TaskConvertor.convertTaskFromString(array.get(1)), epic);
        assertEquals(TaskConvertor.convertTaskFromString(array.get(2)), subtask);
    }

    @Test
    void shouldLoadFromFile() {
        Task task = new Task(1, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 3, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2, "Сделать ремонт", TaskStatus.NEW, "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10, 1, 13, 30, 00),
                Duration.ofMinutes(45), 2);
        fileBackedTaskManager.addSubtask(subtask);

        Task task2 = new Task(44, "Уборка2", TaskStatus.NEW, "Собрать и вынести мусор2",
                LocalDateTime.of(2024, 10, 8, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task2);

        Epic epic2 = new Epic(654, "Сделать ремонт2", "Покрасить стены на балконе2");
        fileBackedTaskManager.addEpic(epic2);

        Subtask subtask2 = new Subtask(63, "Купить краску2", TaskStatus.DONE,
                "Выбрать краску и купить2",
                LocalDateTime.of(2024, 10, 5, 13, 30, 00),
                Duration.ofMinutes(45), 654);
        fileBackedTaskManager.addSubtask(subtask2);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(backedFile);

        assertEquals(newFileBackedTaskManager.getTasks().get(0), task,
                "Задача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getTasks().get(1), task2,
                "Задача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getEpics().get(0), epic,
                "Эпик не загрузился из файла");
        assertNotNull(newFileBackedTaskManager.getEpics().get(1), "Эпик не загрузился из файла");
        assertEquals(newFileBackedTaskManager.getSubtasks().get(0), subtask,
                "Подзадача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getSubtasks().get(1), subtask2,
                "Подзадача не загрузилась из файла");
    }

    @Test
    void shouldMatchIds() {
        Task task = new Task(1, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(24, "Сделать ремонт", "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(55, "Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10, 1, 14, 30, 00),
                Duration.ofMinutes(45), 24);
        fileBackedTaskManager.addSubtask(subtask);

        Task task2 = new Task(653, "Уборка", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 3, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task2);

        Epic epic2 = new Epic(444, "Сделать ремонт2", "Покрасить стены на балконе2");
        fileBackedTaskManager.addEpic(epic2);

        Subtask subtask2 = new Subtask(634, "Выбрать курорт", TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать",
                LocalDateTime.of(2024, 10, 7, 14, 30, 00),
                Duration.ofMinutes(45), 444);
        fileBackedTaskManager.addSubtask(subtask2);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(backedFile);

        assertEquals(newFileBackedTaskManager.getTasks().get(0).getId(), 1,
                "Задача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getTasks().get(1).getId(), 653,
                "Задача не загрузилась из файла");

        assertEquals(newFileBackedTaskManager.getEpics().get(0).getId(), 24,
                "Эпик не загрузился из файла");
        assertEquals(newFileBackedTaskManager.getEpics().get(1).getId(), 444,
                "Эпик не загрузился из файла");

        assertEquals(newFileBackedTaskManager.getSubtasks().get(0).getId(), subtask.getId(),
                "Подзадача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getSubtasks().get(1).getId(), subtask2.getId(),
                "Подзадача не загрузилась из файла");
    }

    @Test
    void shouldNotAddTaskWithSimilarId() {
        Task task = new Task("Уборка1", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 1, 12, 30, 00),
                Duration.ofMinutes(45));
        Task task1 = new Task("Уборка2", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 2, 12, 30, 00),
                Duration.ofMinutes(45));
        Task task2 = new Task("Уборка3", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 3, 12, 30, 00),
                Duration.ofMinutes(45));
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(backedFile);

        Task task3 = new Task("Уборка4", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 4, 12, 30, 00),
                Duration.ofMinutes(45));
        Task task4 = new Task("Уборка5", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 5, 12, 30, 00),
                Duration.ofMinutes(45));
        Task task5 = new Task("Уборка6", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10, 6, 12, 30, 00),
                Duration.ofMinutes(45));

        newFileBackedTaskManager.addTask(task3);
        newFileBackedTaskManager.addTask(task4);
        newFileBackedTaskManager.addTask(task5);

        for (Task t : newFileBackedTaskManager.getTasks()) {
            System.out.println(t);
        }
    }

    @Test
    void shouldThrowException() {
        File file = new File("/path/to/file.txt");
        FileBackedTaskManager newManager = new FileBackedTaskManager(file);
        Epic epic = new Epic(444, "Сделать ремонт2", "Покрасить стены на балконе2");

        assertThrows(ManagerSaveException.class, () -> {
            newManager.addEpic(epic);
        }, "Неверный путь к файлу должен приводить к исключению");

        assertThrows(RuntimeException.class, () -> {
                    newManager.loadFromFile(file);
                }, "Неверный путь к файлу должен приводить к исключению"
        );
    }
}
