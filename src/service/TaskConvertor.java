package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

public class TaskConvertor {

    public static String convertTaskToString(Task task) {
        String taskToString;
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskToString = String.format("%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(),
                    task.getNameOfTask(), task.getTaskStatus(),
                    task.getDescription(), subtask.getEpicId());
        } else {
            taskToString = String.format("%s,%s,%s,%s,%s", task.getId(), task.getTaskType(),
                    task.getNameOfTask(), task.getTaskStatus(), task.getDescription());
        }
        return taskToString;
    }

    public static Task convertTaskFromString(String value) {
        String[] array = value.split(",");
        int id = Integer.valueOf(array[0]);
        TaskType type = TaskType.valueOf(array[1]);
        String name = array[2];
        TaskStatus status = TaskStatus.valueOf(array[3]);
        String description = array[4];

        if (type.equals(TaskType.SUBTASK)) {
            int epicId = Integer.valueOf(array[5]);
            return new Subtask(id, name, status, description, epicId);
        } else if (type.equals(TaskType.TASK)) {
            return new Task(id, name, status, description);
        } else {
            return new Epic(id, name, status, description);
        }
    }
}
