package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTests {
    FileBackedTaskManager fileBackedTaskManager;
    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = new FileBackedTaskManager();
    }

    @Test
    void shouldReturnTaskToString() {
        Task task = new Task(1,"Уборка", "Собрать и вынести мусор", TaskStatus.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic(2,"Сделать ремонт", "Покрасить стены на балконе");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask(10, 2,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        fileBackedTaskManager.addSubtask(subtask);

        String taskToString = "1,TASK,Уборка,NEW,Собрать и вынести мусор";
        String epicToString = "2,EPIC,Сделать ремонт,NEW,Покрасить стены на балконе";
        String subtaskToString = "3,SUBTASK,Купить шпатель,NEW,Выбрать в магазине шпатель и купить,2";

        assertEquals(taskToString, fileBackedTaskManager.toString(task),
                "Неверный вывод taskToString");
        assertEquals(epicToString, fileBackedTaskManager.toString(epic),
                "Неверный вывод epicToString");
        assertEquals(subtaskToString, fileBackedTaskManager.toString(subtask),
                "Неверный вывод subtaskToString");
    }

    @Test
    void shouldReturnTaskFromString() {
        Task task = new Task(1, TaskType.TASK, "Уборка",
                TaskStatus.NEW, "Собрать и вынести мусор");

        Epic epic = new Epic(2, TaskType.EPIC, "Сделать ремонт",
                TaskStatus.NEW, "Покрасить стены на балконе");

        Subtask subtask = new Subtask(3, TaskType.SUBTASK,"Купить шпатель",
                TaskStatus.NEW, "Выбрать в магазине шпатель и купить", 2);

        String taskString = "1,TASK,Уборка,NEW,Собрать и вынести мусор";
        String epicString = "2,EPIC,Сделать ремонт,NEW,Покрасить стены на балконе";
        String subtaskString = "3,SUBTASK,Купить шпатель,NEW,Выбрать в магазине шпатель и купить,2";

         assertEquals(fileBackedTaskManager.taskFromString(taskString), task);
         assertEquals(fileBackedTaskManager.taskFromString(epicString), epic);
         assertEquals(fileBackedTaskManager.taskFromString(subtaskString), subtask);
    }

}
