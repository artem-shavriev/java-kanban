package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(int id, String nameOfTask, TaskStatus taskStatus, String description,
                   LocalDateTime startTime, Duration duration, int epicId) {
        super(id, nameOfTask, taskStatus, description, startTime, duration);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return taskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}


