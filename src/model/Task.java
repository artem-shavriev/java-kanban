package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String nameOfTask;
    private String description;
    private Integer id;
    private TaskStatus taskStatus;
    protected TaskType taskType;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String nameOfTask, String description, TaskStatus taskStatus) {
        this.id = id;
        this.description = description;
        this.nameOfTask = nameOfTask;
        this.taskStatus = taskStatus;
    }

    public Task(int id, String nameOfTask, String description) {
        this.id = id;
        this.description = description;
        this.nameOfTask = nameOfTask;
    }

    public Task(int id, String nameOfTask, TaskStatus taskStatus, String description, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.taskType = taskType.TASK;
        this.nameOfTask = nameOfTask;
        this.taskStatus = taskStatus;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String nameOfTask, TaskStatus taskStatus, String description) {
        this.id = id;
        this.taskType = taskType.TASK;
        this.nameOfTask = nameOfTask;
        this.taskStatus = taskStatus;
        this.description = description;
    }

    public Task(String nameOfTask, String description, TaskStatus taskStatus) {
        this.nameOfTask = nameOfTask;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public Task(String nameOfTask, String description) {
        this.nameOfTask = nameOfTask;
        this.description = description;

    }

    public TaskType getTaskType() {
        return taskType.TASK;
    }

    public String getNameOfTask() {
        return nameOfTask;
    }

    public void setNameOfTask(String nameOfTask) {
        this.nameOfTask = nameOfTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id; /*&& Objects.equals(nameOfTask, task.nameOfTask)
                && Objects.equals(description, task.description) && taskStatus == task.taskStatus;*/
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameOfTask, description, id, taskStatus);
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "nameOfTask='" + nameOfTask + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
