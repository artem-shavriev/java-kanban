package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConvertor {

    public static String convertTaskToString(Task task) {
        String taskToString;

        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskToString = String.format("%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(),
                    task.getNameOfTask(), task.getTaskStatus(), task.getDescription(),
                    subtask.getStartTime(), subtask.getDuration().toMinutes(), subtask.getEpicId());
        } else if (task.getTaskType().equals(TaskType.EPIC)) {
            taskToString = String.format("%s,%s,%s,%s,%s", task.getId(), task.getTaskType(),
                    task.getNameOfTask(), task.getTaskStatus(), task.getDescription());
        } else {
            taskToString = String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getTaskType(),
                    task.getNameOfTask(), task.getTaskStatus(), task.getDescription(),
                    task.getStartTime(), task.getDuration().toMinutes());
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
            LocalDateTime startTime = LocalDateTime.parse(array[5]);
            Long durationInMinutes = Long.valueOf(array[6]);
            Duration duration = Duration.ofMinutes(durationInMinutes);
            int epicId = Integer.valueOf(array[7]);
            return new Subtask(id, name, status, description, startTime, duration, epicId);
        } else if (type.equals(TaskType.TASK)) {
            LocalDateTime startTime = LocalDateTime.parse(array[5]);
            Long durationInMinutes = Long.valueOf(array[6]);
            Duration duration = Duration.ofMinutes(durationInMinutes);
            return new Task(id, name, status, description, startTime, duration);
        } else {
            return new Epic(id, name, status, description);
        }
    }
}
