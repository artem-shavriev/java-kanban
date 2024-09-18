package model;

public class Subtask extends Task {
    private Integer epicId;
    private TaskType taskType = TaskType.SUBTASK;

    //конструктор с id для целей тестирования
    public Subtask(int id, int epicId, String nameOfTask, String description, TaskStatus taskStatus) {
        super(id, nameOfTask, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(String nameOfTask, String description, TaskStatus taskStatus) {
        super(nameOfTask, description, taskStatus);
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}


