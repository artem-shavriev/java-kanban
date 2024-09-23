import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File backedFile = new File ("data.csv");

        TaskManager taskManager = Managers.getDefault(backedFile);
        Task task1 = new Task(1,"Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        Task task2 = new Task(2,"Готовка", "Приготовить еду",  TaskStatus.NEW);
        Task task3 = new Task(3,"Стирка", "Постирать вещи",  TaskStatus.NEW);
        Epic epic1 = new Epic(4,"Поехать в отпуск", "Организовать путешествие");
        Epic epic2 = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        Subtask subtask1 = new Subtask(6, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",5);
        Subtask subtask2 = new Subtask(7, "Купить краску",  TaskStatus.DONE,
                "Выбрать краску и купить", 5);
        Subtask subtask3 = new Subtask(8,"Выбрать курорт",  TaskStatus.IN_PROGRESS,
                "Изучить варинты гостиниц и забронировать", 4);
        Subtask subtaskForUpdate = new Subtask(9, "Выбрать курорт", TaskStatus.DONE,
                "Изучить варинты гостиниц и забронировать", 4);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.updateSubtask(subtaskForUpdate);
        printAllTasks(taskManager);
        System.out.println("");

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(backedFile);
        newFileBackedTaskManager.loadFromFile(backedFile);
        System.out.println("Задачи из нового менеджера загруженные из файла:");
        printAllTasks(newFileBackedTaskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
