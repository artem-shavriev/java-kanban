package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.BufferedReader;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTests {
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
        Task task = new Task(1,"Уборка", TaskStatus.NEW, "Собрать и вынести мусор");
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2,"Сделать ремонт", TaskStatus.NEW,"Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить", 2);

        String taskToString = "1,TASK,Уборка,NEW,Собрать и вынести мусор";
        String epicToString = "2,EPIC,Сделать ремонт,NEW,Покрасить стены на балконе";
        String subtaskToString = "3,SUBTASK,Купить шпатель,NEW,Выбрать в магазине шпатель и купить,2";

        assertEquals(taskToString, TaskConvertor.convertTaskToString(task),
                "Неверный вывод taskToString");
        assertEquals(epicToString, TaskConvertor.convertTaskToString(epic),
                "Неверный вывод epicToString");
        assertEquals(subtaskToString, TaskConvertor.convertTaskToString(subtask),
                "Неверный вывод subtaskToString");
    }

    @Test
    void shouldReturnTaskFromString() {
        Task task = new Task(1, "Уборка",
                TaskStatus.NEW, "Собрать и вынести мусор");

        Epic epic = new Epic(2, "Сделать ремонт",
                TaskStatus.NEW, "Покрасить стены на балконе");

        Subtask subtask = new Subtask(3, "Купить шпатель",
                TaskStatus.NEW, "Выбрать в магазине шпатель и купить", 2);

        String taskString = "1,TASK,Уборка,NEW,Собрать и вынести мусор";
        String epicString = "2,EPIC,Сделать ремонт,NEW,Покрасить стены на балконе";
        String subtaskString = "3,SUBTASK,Купить шпатель,NEW,Выбрать в магазине шпатель и купить,2";

         assertEquals(TaskConvertor.convertTaskFromString(taskString), task);
         assertEquals(TaskConvertor.convertTaskFromString(epicString), epic);
         assertEquals(TaskConvertor.convertTaskFromString(subtaskString), subtask);
    }

    @Test
    void shouldSaveTasksInBackedFile() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2,"Сделать ремонт", "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(3, 2,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask);

        ArrayList<String> array = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileBackedTaskManager.backedFile))) {
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
        Task task = new Task(1, "Уборка", "Собрать и вынести мусор", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2, "Сделать ремонт", "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(5, 2, "Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask);

        Task task2 = new Task(3, "Уборка2", "Собрать и вынести мусор2", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task2);

        Epic epic2 = new Epic(4, "Сделать ремонт2", "Покрасить стены на балконе2");
        fileBackedTaskManager.addEpic(epic2);

        Subtask subtask2 = new Subtask(6, 4, "Купить шпатель2",
                "Выбрать в магазине шпатель и купить2", TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask2);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(backedFile);

        assertEquals(newFileBackedTaskManager.getTasks().get(0), task,
                "Задача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getTasks().get(1), task2,
                "Задача не загрузилась из файла");

        assertEquals(newFileBackedTaskManager.getEpics().get(0), epic,
                "Эпик не загрузился из файла");
        assertEquals(newFileBackedTaskManager.getEpics().get(1), epic2,
                "Эпик не загрузился из файла");

        assertEquals(newFileBackedTaskManager.getSubtasks().get(0), subtask,
                "Подзадача не загрузилась из файла");
        assertEquals(newFileBackedTaskManager.getSubtasks().get(1), subtask2,
                "Подзадача не загрузилась из файла");
    }

    @Test
    void shouldMatchIds() {
        Task task = new Task(1, "Уборка", "Собрать и вынести мусор", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(24, "Сделать ремонт", "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(55, 24, "Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask);

        Task task2 = new Task(653, "Уборка2", "Собрать и вынести мусор2", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task2);

        Epic epic2 = new Epic(444, "Сделать ремонт2", "Покрасить стены на балконе2");
        fileBackedTaskManager.addEpic(epic2);

        Subtask subtask2 = new Subtask(634, 444, "Купить шпатель2",
                "Выбрать в магазине шпатель и купить2", TaskStatus.NEW);
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
}
