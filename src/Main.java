import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main (String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Уборка", "Собрать и вынести мусор",  TaskStatus.NEW);
        Task task2 = new Task("Готовка", "Приготовить еду",  TaskStatus.NEW);
        Task task3 = new Task("Стирка", "Постирать вещи",  TaskStatus.NEW);
        Epic epic1 = new Epic("Поехать в отпуск", "Организовать путешествие");
        Epic epic2 = new Epic("Сделать ремонт", "Покрасить стены на балконе");
        Subtask subtask1 = new Subtask(10, 5,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(11, 5,"Купить краску",
                "Выбрать краску и купить", TaskStatus.DONE);
        Subtask subtask3 = new Subtask(12, 4,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.IN_PROGRESS);

        Subtask subtaskForUpdate = new Subtask(8, 4,"Выбрать курорт",
                "Изучить варинты гостиниц и забронировать", TaskStatus.DONE);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);


        taskManager.getTaskById(task1.getId());


        taskManager.removeTaskById(task1.getId());

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.updateSubtask(subtaskForUpdate);

        printAllTasks((InMemoryTaskManager) taskManager);

        taskManager.removeEpics();

        printAllTasks((InMemoryTaskManager) taskManager);


    }

    private static void printAllTasks(InMemoryTaskManager manager) {
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
