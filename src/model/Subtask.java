package model;

public class Subtask extends Task {
    private Integer epicId;
    private TaskType taskType;

    public Subtask(int id, String nameOfTask, TaskStatus taskStatus, String description, int epicId) {
        super(id, nameOfTask, taskStatus, description);
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


