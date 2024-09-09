package model;

public class Subtask extends Task {
    private Integer epicId;

    //конструктор с id для целей тестирования
    public Subtask(int id, int epicId, String nameOfTask, String description, TaskStatus taskStatus) {
        super(id, nameOfTask, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(String nameOfTask, String description, TaskStatus taskStatus) {
        super(nameOfTask, description, taskStatus);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}


