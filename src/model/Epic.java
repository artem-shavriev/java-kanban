package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String nameOfTask, TaskStatus taskStatus, String description) {
        super(id, nameOfTask, taskStatus, description);
        this.taskType = taskType.EPIC;
    }

    public Epic(int id, String nameOfTask, String description) {
        super(id, nameOfTask, description);
        this.taskType = taskType.EPIC;
    }

    public Epic(String nameOfTask, String description) {
        super(nameOfTask, description);
        this.taskType = taskType.EPIC;
    }

    @Override
    public TaskType getTaskType() {
        return taskType.EPIC;
    }

    public void setSubtaskId(int id) {
        subtasksIds.add(id);
    }

    public void removeAllSubtasksIds() {
        subtasksIds.clear();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void removeSubtaskIdById(Integer subtaskId) {
        if (subtasksIds.contains(subtaskId)) {
            subtasksIds.remove(subtaskId);
        } else {
            System.out.println("Такого id нет в списке subtasksIds");
        }
    }

    public void setEpicEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
